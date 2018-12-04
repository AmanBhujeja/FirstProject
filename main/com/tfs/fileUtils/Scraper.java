package com.tfs.fileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.tfs.utilities.Log;

public class Scraper {
	private Set<Integer> testCasesToAutomate = new HashSet<Integer>();
	private Set<Integer> testCasesToNotAutomate = new HashSet<Integer>();
	private final String TC_AUTOMATE_STATUS_PATTERN = "(?![tT][cC]\\d+_*[nN][aA])([tT][cC]\\d+\\S*)";
	private final String TC_NA_STATUS_PATTERN = "[tT][cC]\\d+_*[nN][aA]";
	
	/**
	 * Parse given Test Suite and fetch Test Cases in the given suite.
	 * @param filePath
	 * @return Collection of all Test Case ID
	 */
	public void parseTestSuite(String filePath) {
		retrieveTestCases(filePath);
		Log.info("Test Case ID(s) retrieved to mark Planned(Automated): "+Arrays.toString(testCasesToAutomate.toArray()));
		Log.info("Test Case ID(s) retrieved to mark Not Automated: "+Arrays.toString(testCasesToNotAutomate.toArray()));
	}
	
	/**
	 * Parse List of Test Suites and fetch Test Cases in each suite.
	 * @param filePath
	 * @return Collection of all Test Case ID
	 */
	public void parseTestSuite(List<String> filePaths) {
		for(String filePath:filePaths){
			retrieveTestCases(filePath);
		}
		Log.info("Test Case ID(s) retrieved to mark Planned(Automated): "+Arrays.toString(testCasesToAutomate.toArray()));
		Log.info("Test Case ID(s) retrieved to mark Not Automated: "+Arrays.toString(testCasesToNotAutomate.toArray()));
	}
		
	private void retrieveTestCases(String filePath){
		try {
			
			String data = new String(Files.readAllBytes(Paths.get(filePath)));
			
			Pattern pattern = Pattern.compile(TC_AUTOMATE_STATUS_PATTERN);
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {
				String find = matcher.group();
				find = find.replaceAll("\\D", "").trim();
				testCasesToAutomate.add(Integer.parseInt(find));
			}
			
			pattern = Pattern.compile(TC_NA_STATUS_PATTERN);
			matcher = pattern.matcher(data);
			while (matcher.find()) {
				String find = matcher.group();
				find = find.replaceAll("\\D", "").trim();
				testCasesToNotAutomate.add(Integer.parseInt(find));
			}
			
			testCasesToAutomate.removeAll(testCasesToNotAutomate);
			
		} catch (IOException e) {
			Log.error("Error while Retreiving Test Case ID \n"+e.getMessage());
		}
	}
	
	public Set<Integer> getTCIDToMarkAutomated(){
		return testCasesToAutomate;
	}
	
	public Set<Integer> getTCIDToMarkNA(){
		return testCasesToNotAutomate;
	}
	
	public static void main(String[] args) {
		Scraper sc = new Scraper();
		sc.parseTestSuite("C:\\Users\\abhujeja\\Documents\\testCase_DummyTxt.txt");
		
	}
}
