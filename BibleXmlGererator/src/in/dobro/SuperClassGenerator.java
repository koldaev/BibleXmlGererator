package in.dobro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SuperClassGenerator {
	
	static String langtoxml;
	
	static Properties connInfo = new Properties();
	static {
		connInfo.put("characterEncoding","UTF8");
		connInfo.put("user", "root");
		connInfo.put("password", "zxasqw12");
	}

	Connection			conn  = null;        
	//переменные генерации классов Библии
	PreparedStatement   pstmt = null;
    ResultSet         	rs    = null;
    //переменные названий книг Библии
    PreparedStatement   namebooks = null;
    ResultSet           rnamebooks    = null;
    //xml-файл
    FileWriter fstreamxml = null;
    
    BufferedWriter outfstreamxml = null;

    File dir;
    
	public SuperClassGenerator(String inlang) throws SQLException, IOException {
		langtoxml = inlang;
		pregeneratorxml();
	}

	private void pregeneratorxml() throws SQLException, IOException {
		conn = DriverManager.getConnection("jdbc:mysql://localhost/bible_"+langtoxml+"?", connInfo);
        dir = new File(langtoxml+"_xml");
        dir.mkdir();
        namebooks = conn.prepareStatement("SELECT idbible, biblename, chapters FROM "+langtoxml+"bible");
        filecreate();
        generatorxml();
	}

	private void filecreate() throws IOException {
		fstreamxml = new FileWriter(dir+"/"+langtoxml+"_bible.xml");
		outfstreamxml = new BufferedWriter(fstreamxml);
		outfstreamxml.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		outfstreamxml.write("<biblexml>\n");
		outfstreamxml.write("\t<descripion>\n");
		outfstreamxml.write("\t\t<version language=\""+langtoxml+"\">Библия. Русский Синодальный перевод</version>\n");
		outfstreamxml.write("\t</descripion>\n");
		outfstreamxml.write("\t<chapterviews>\n");
		outfstreamxml.write("\t\t<chapter name=\"Глава\"/>\n");
		outfstreamxml.write("\t\t<psalom name=\"Псалом\"/>\n");
		outfstreamxml.write("\t</chapterviews>\n");
		outfstreamxml.write("\t<booknames>\n");
		
	}

	private void generatorxml() throws SQLException, IOException {
		if (namebooks.execute()) {
        	rnamebooks = namebooks.getResultSet();
        	while(rnamebooks.next()) {
        		outfstreamxml.write("\t\t<book=\"" + rnamebooks.getString("biblename")  + "\" chapters=\""+rnamebooks.getString("chapters")+"\"/>\n");
        	}
        	outfstreamxml.write("\t</booknames>\n");
		}
		outfstreamxml.write("</biblexml>");
		outfstreamxml.close();
	}

}
