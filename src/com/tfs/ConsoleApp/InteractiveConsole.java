package com.tfs.ConsoleApp;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.tfs.TFSConnectionUtils.TestStatusUpdateUtility;
import com.tfs.TFSConnectionUtils.TestStatusUpdateUtility.AutomationStatus;
import com.tfs.fileUtils.FileSearch;
import com.tfs.fileUtils.Scraper;
import com.tfs.utilities.Log;

public class InteractiveConsole {

	private Scanner sc;
	private FileSearch fs;
	private Scraper scraper;
	private TestStatusUpdateUtility tsu;

	public InteractiveConsole() {
		sc = new Scanner(System.in);
		scraper = new Scraper();
		tsu = new TestStatusUpdateUtility();
	}

	/**
	 * Update Status in TFS. Fetch Details from User on Console
	 */
	public void updateTestCaseStatus() {

		try {
			createTFSConenction();
			// Fetch Directory for Test Suite or test Suite Path
			File dir = enterRootDirectory();

			// In Case of Directory, fetch all files or specified files and
			// Parse files to get Test Case ID
			if (dir.isDirectory()) {
				List<String> filePaths = searchMultipleFileLocation(dir);
				scraper.parseTestSuite(filePaths);
			} else {
				scraper.parseTestSuite(dir.getAbsolutePath());
			}

			// Update Automation Status for retrieved Test Case ID
			tsu.updateStatus(scraper.getTCIDToMarkAutomated(),
					AutomationStatus.PLANNED);

			// Update if NA Status is not null
			if (scraper.getTCIDToMarkNA() != null)
				tsu.updateStatus(scraper.getTCIDToMarkNA(),
						AutomationStatus.NOT_AUTOMATED);

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			tsu.closeTFSConenction();
		}
	}

	private File enterRootDirectory() {
		Log.msg("Enter Location for Test Suite(Including File Extension). In Case of Multiple Test Suites, enter directory path \n");
		File file = null;
		String filePath = null;
		while (true) {
			filePath = sc.nextLine().trim();
			file = new File(filePath);
			if (file.isFile() || file.isDirectory()) {
				break;
			} else {
				Log.warn("\n Entered value is not acceptable. Please enter a valid directory path or file location");
			}
		}
		return file;
	}

	private List<String> searchMultipleFileLocation(File dir) {
		List<String> filePaths = new ArrayList<String>();
		fs = new FileSearch();
		fs.setDirectoryPath(dir.getAbsolutePath());
		boolean flag = true;
		while (flag) {
			Log.msg("Choose from below: \n" + "1. All Files \n"
					+ "2. Specific Files \n");
			int option = sc.nextInt();
			sc.nextLine();
			switch (option) {
			case 1:
				switchCase_AllFiles();
				flag = false;
				break;
			case 2:
				switchCase_SpecifiedFiles();
				flag = false;
				break;
			default:
				Log.warn("Invalid Value\n");

			}
		}
		filePaths.addAll(fs.getResult());
		return filePaths;
	}

	private void switchCase_SpecifiedFiles() {
		Log.msg("Enter Test Suite names seperated by comma(,)(Including File Extension) \n");
		String multiFiles = sc.nextLine();
		List<String> fileNames = Arrays.asList(multiFiles.split(","));
		fs.searchDirectory(fileNames);
	}

	private void switchCase_AllFiles() {
		Log.info("Retreiving all files in the given directory");
		fs.searchAllFiles();
	}

	private void createTFSConenction() {
		Log.msg("Enter Username to connect to TFS Server ( SOTI username without domain)");
		String username = sc.nextLine();
		Log.msg("Enter Password to connect to TFS Server");
		Console con = System.console();
		String password = null;
		if (con != null)
			password = String.valueOf(con.readPassword());

		Log.msg("Enter TFS Project Name \n");
		String projectName = sc.nextLine().trim();
		tsu.createConnectionAndGetProject(username, password, projectName);
	}

	public void getTestCaseCount() {
		try {
			createTFSConenction();
			Log.msg("Enter Iteration Path");
			String itrPath = sc.nextLine().trim();
			tsu.getWorkItem(itrPath);
		} catch (Exception e) {
			Log.error("Error in getting Test Case Count. \n" + e.getMessage());
		}

	}

	public void initialize() {
		try {
			boolean flag = true;
			while (flag) {
				Log.msg("Please Choose: \n"
						+ "1. Update Automation Status in TFS \n"
						+ "2. Export Total Test Cases as CSV");
				int input = sc.nextInt();
				sc.nextLine();
				switch (input) {
				case 1:
					updateTestCaseStatus();
					flag = false;
					break;
				case 2:
					getTestCaseCount();
					flag = false;
					break;
				default:
					Log.warn("Invalid Value\n");
				}
			}
		} catch (Exception e) {
			Log.error("Error while performing the Current Operation");
		} finally {
			if (tsu != null)
				tsu.closeTFSConenction();
		}
	}

	public static void main(String[] args) {
		InteractiveConsole cs = new InteractiveConsole();
		cs.initialize();
		Log.info("Program has ended, please type 'exit' to close the console");
		if (cs.sc.nextLine() == "exit") {
			cs.sc.close();
			System.exit(0);
		}

	}

}
