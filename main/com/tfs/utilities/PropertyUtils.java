package com.tfs.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {

	private static Properties prop = new Properties();
	
	public static void loadPropertyFile(final String fileName){
		try {
			String filepath = System.getProperty("user.dir")+"//Resources//"+fileName.toLowerCase()+".properties";
			InputStream fis = new FileInputStream(filepath);
			prop.load(fis);
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}
	
	public static String getProperty(final String propName){
		return prop.getProperty(propName.toLowerCase());
	}
	

}
