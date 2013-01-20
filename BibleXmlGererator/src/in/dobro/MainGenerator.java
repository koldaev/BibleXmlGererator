package in.dobro;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class MainGenerator extends SuperClassGenerator {

	public MainGenerator(String tolang) throws SQLException, IOException {
		super(tolang);
	}
	
	
	public static void main(String[] args) throws SQLException, IOException {
		Scanner in = new Scanner(System.in);
        while(true) {
        	System.out.println("\nПожалуйста, введите один язык Библии как домен страны, например ru\n\n");
    		String lang = in.nextLine();
        	new MainGenerator(lang);
        } 
	}

}
