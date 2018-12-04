package com.tfs.fileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.tfs.utilities.Log;

public class FileSearch {
	private String dirPath;
	private String fileNameToSearch;
	private List<String> result = new ArrayList<String>();
	private List<String> fileNamesToSearch = new ArrayList<String>();

	public void setDirectoryPath(String dirPath){
		this.dirPath = dirPath;
	}

	private void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	private void setFileNamesToSearch(List<String> fileNamesToSearch) {
		this.fileNamesToSearch.addAll(fileNamesToSearch);
	}

	public List<String> getResult() {
		return result;
	}
	
	/**
	 * Search the FileName given as argument and fetch location of the file. 
	 * @param String fileNameToSearch
	 */
	public void searchDirectory(String fileNameToSearch) {
		File directory = new File(dirPath);
		setFileNameToSearch(fileNameToSearch);
		search(directory);
		if(fileNamesToSearch != null){
			for(String fileNotFound: fileNamesToSearch)
				Log.info("File Not Found "+fileNotFound);
		}
	}

	/**
	 * Search List of  Files given as argument and fetch location of each file. 
	 * @param List fileNameToSearch
	 */
	public void searchDirectory(List<String> fileNameToSearch) {
		File directory = new File(dirPath);
		setFileNamesToSearch(fileNameToSearch);
		search(directory);
		if(fileNamesToSearch != null){
			for(String fileNotFound: fileNamesToSearch)
				Log.warn("File Not Found "+fileNotFound);
		}
	}

	private void search(File file) {

		Log.info("Searching directory ... " + file.getAbsoluteFile());

		// do you have permission to read this directory?
		if (file.canRead()) {
			for (File temp : file.listFiles()) {
				if (temp.isDirectory()) {
					search(temp);
				} else {
					if (fileNameToSearch != null) {
						if (fileNameToSearch.equals(temp.getName().toLowerCase())) {
							result.add(temp.getAbsoluteFile().toString());
							Log.info("Found file ... "+temp);
						}
					}else if(fileNamesToSearch != null){
						for(String fileName:fileNamesToSearch){
							if (fileName.toLowerCase().equals(temp.getName().toLowerCase())) {
								result.add(temp.getAbsoluteFile().toString());
								Log.info("Found file ... "+fileName);
								fileNamesToSearch.remove(fileName);
								break;
							}
						}
					}
				}
			}
		} else {
			Log.error(file.getAbsoluteFile() + "Permission Denied");
		}

	}

	
	/**
	 * Fetch location of all the files from given directory and sub directories
	 */
	public void searchAllFiles(){
		File file = new File(dirPath);
		Log.info("Searching directory ... " + file.getAbsoluteFile());
		if (file.canRead()) {
			for (File temp : file.listFiles()) {
				if (temp.isDirectory()) {
					searchAllFiles();
				} else {
					result.add(temp.getAbsolutePath());
				}
			}
		} else {
			Log.error(file.getAbsoluteFile() + "Permission Denied");
		}
	}
	
	
}
