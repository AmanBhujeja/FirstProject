package com.tfs.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVUtils {

	private static FileWriter writer = null;
	private static final String dateAndTimeFormat = "MM-dd-yyyy_hh.mm.ss";
	private static String FileName = null;
	public static void createCSVFile() {
		SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
		String dateTime = sdf.format(new Date());
		String dirPath = "./CSV/";
		FileName = dirPath+"\\ExportedCSV-" + dateTime + ".csv";

		try {
			Log.info("Creating CSV at: "+FileName);
			File dir = new File(dirPath);
			if(!dir.exists())
				dir.mkdir();
			
			File file = new File(FileName);
			writer = new FileWriter(file);
			Log.info("CSV File created at: "+FileName);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
	public static void writeCSVFile(String...data) throws IOException{
		try {
			writer = new FileWriter(FileName,true);
			for(int i=0;i<data.length;i++){
				writer.append(data[i]);
				if(i!=data.length)
					writer.append(',');
			}
			writer.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
