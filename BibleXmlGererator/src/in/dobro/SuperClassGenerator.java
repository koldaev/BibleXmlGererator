package in.dobro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SuperClassGenerator {
	
	static String langtoxml;
	static String[] strfield = null;
	
	
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
  //переменные названий стихов Библии
    PreparedStatement   poembooks = null;
    ResultSet           rpoembooks    = null;
    //xml-файл
    FileWriter fstreamxml = null;
    
    BufferedWriter outfstreamxml = null;

    File dir;
    
    static int[] bookchapters = new int[67];
    
	public SuperClassGenerator(String inlang) throws SQLException, IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		langtoxml = inlang;
		
		//достаем точную локализацию из массива  
        Class cl = Class.forName("in.dobro.names");
		Field outext = cl.getDeclaredField(langtoxml + "names");
		strfield = (String[])outext.get(cl);
		
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
		fstreamxml = new FileWriter(dir+"/"+langtoxml+"bible.xml");
		outfstreamxml = new BufferedWriter(fstreamxml);
		outfstreamxml.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		outfstreamxml.write("<biblexml>\n");
		outfstreamxml.write("\t<descripion>\n");
		outfstreamxml.write("\t\t<version language=\""+langtoxml+"\">"+strfield[0]+"</version>\n");
		outfstreamxml.write("\t</descripion>\n");
		outfstreamxml.write("\t<chapterviews>\n");
		outfstreamxml.write("\t\t<chapter name=\""+strfield[2]+"\"/>\n");
		outfstreamxml.write("\t\t<psalom name=\""+strfield[9]+"\"/>\n");
		outfstreamxml.write("\t</chapterviews>\n");
		outfstreamxml.write("\t<booknames>\n");
		
	}

	private void generatorxml() throws SQLException, IOException {
		if (namebooks.execute()) {
        	rnamebooks = namebooks.getResultSet();
        	while(rnamebooks.next()) {
        		Integer id = rnamebooks.getInt("idbible");
        		bookchapters[id] = rnamebooks.getInt("chapters");
        		outfstreamxml.write("\t\t<book name=\"" + rnamebooks.getString("biblename")  + "\" idbook=\"book" + id + "\" chapters=\""+rnamebooks.getString("chapters")+"\"/>\n");
        	}
        	outfstreamxml.write("\t</booknames>\n");
		}
		
		booktext();
		
		outfstreamxml.write("</biblexml>");
		outfstreamxml.close();
	}

	private void booktext() throws SQLException, IOException {
		for(int i=1;i<=66;i++) {
			outfstreamxml.write("\t<booktext id=\"book" + i + "\">\n");
			
				for(int k=1;k<=bookchapters[i];k++) {
					outfstreamxml.write("\t\t<chapter id=\"book"+i+"."+k+"\">\n");
					
					poembooks = conn.prepareStatement("SELECT * FROM "+langtoxml+"text where bible = " + i + " and chapter = " + k);
					if (poembooks.execute()) {
						rpoembooks = poembooks.getResultSet();
						while(rpoembooks.next()) {
							outfstreamxml.write("\t\t\t<verse id=\""+i+"."+k+"."+rpoembooks.getInt("poem")+"\">"+rpoembooks.getString("poemtext")+"</verse>\n");
						}
					}
					
					outfstreamxml.write("\t\t</chapter>\n");
				}
			
			outfstreamxml.write("\t</booktext>\n");
		}
	}

}
