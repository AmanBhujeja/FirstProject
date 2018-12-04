package com.tfs.TFSConnectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.fields.Field;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.DefaultNTCredentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;
import com.microsoft.tfs.core.util.CredentialsUtils;
import com.tfs.utilities.CSVUtils;
import com.tfs.utilities.Log;

public class TestStatusUpdateUtility {

	private final String FIELD_AUTOMATION_STATUS = "Microsoft.VSTS.TCM.AutomationStatus";
	private TFSTeamProjectCollection tpc;
	private final String TFS_URL = "http://taipan:8080/tfs/";
	private Project project;
	private String WIQL_GET_ALL_TEST_TESTCASES = "SELECT [System.Id], [System.Title], [Microsoft.VSTS.TCM.AutomationStatus] FROM WorkItems WHERE [System.WorkItemType] "
			+ "IN GROUP \'Test Case Category\' AND [System.IterationPath] = \'%s\' "
			+ " ORDER BY [System.Id]";

	private String WIQL_GET_TESTCASES_CUSTOM_STATUS = "SELECT [System.Id], [System.Title], [Microsoft.VSTS.TCM.AutomationStatus] FROM WorkItems WHERE [System.WorkItemType] "
			+ "IN GROUP \'Test Case Category\' "
			+ "AND [Microsoft.VSTS.TCM.AutomationStatus] = \'%s\' "
			+ "AND [System.IterationPath] = \'%s\' " + " ORDER BY [System.Id]";

	public enum AutomationStatus {
		AUTOMATED("Automated"),
		PLANNED("Planned"), 
		NOT_AUTOMATED("Not Automated");

		private String value;

		AutomationStatus(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	
	public void createConnectionAndGetProject(final String username, final String password, final String projectName) {
		System.setProperty("com.microsoft.tfs.jni.native.base-directory", ".//TFS-SDK-14.134.0//redist//native");
		try {
			// PropertyUtils.loadPropertyFile("config");
			URI uri = URI.create(TFS_URL);
			Credentials credentials;
			if ((username == null || username.length() == 0) && CredentialsUtils.supportsDefaultCredentials())
	        {
	            credentials = new DefaultNTCredentials();
	        }
	        else{
	            credentials = new UsernamePasswordCredentials("SOTI\\"+username, password);
	        }

			Log.info("Creating Connection with TFS And Accesing Project ");
			tpc = new TFSTeamProjectCollection(uri, credentials);
			Log.info("Accessing the Project: " + projectName);
			project = tpc.getWorkItemClient().getProjects().get(projectName);
			Log.info("Connection successfully created");
		} catch (Exception e) {
			Log.error("Error occurred while creating Connection and accessing project \n"+ e.getMessage());
		}
	}

	public void updateStatus(final Set<Integer> TCID,
			final AutomationStatus automationStatus) {
		WorkItem workItem = null;
		Field field = null;
		Iterator<Integer> itr_TCID = TCID.iterator();
		while (itr_TCID.hasNext()) {
			try {
				int testCase = itr_TCID.next();
				Log.info("Searching test case " + testCase);
				workItem = project.getWorkItemClient()
						.getWorkItemByID(testCase);
				if (workItem == null) {
					Log.warn("Test Case with ID " + testCase + " is not found");
					continue;
				}
				field = workItem.getFields().getField(FIELD_AUTOMATION_STATUS);
				String currentStatus = field.getValue().toString();

				if (!currentStatus.equals(automationStatus.getValue())) {
					field.setValue(automationStatus.getValue());
					workItem.save();
					Log.info("Automation Status is updated to"
							+ automationStatus.getValue() + " for Test Case "
							+ testCase);
				}
			} catch (Exception e) {
				Log.error("Error in updating Test Case Status \n"
						+ e.getMessage());
				e.printStackTrace();
			}
		}

	}

	public void closeTFSConenction() {
		tpc.close();
	}

	public void getWorkItem(String itrPath) throws IOException {
		WorkItemClient workItemClient = project.getWorkItemClient();

		// String wiql = String.format(WIQL_GET_ALL_TEST_TESTCASES,
		// "MobiControl\\FTR\\MADP");
		String wiql = String.format(WIQL_GET_ALL_TEST_TESTCASES, itrPath);
		
		WorkItemCollection workItems = workItemClient.query(wiql);

		int count = workItems.size();
		if (count > 0) {
			Log.info("Found " + count + " work items");

			// Create a CSV File
			CSVUtils.createCSVFile();
			Log.info("Writting Data into CSV File");
			// Write the headers
			CSVUtils.writeCSVFile("ID", "Title", "AutomationStatus");
			
			// Write the Data
			for (int i = 0; i < count; i++) {
				WorkItem workItem = workItems.getWorkItem(i);
				int workID = workItem.getID();
				String workItemID = Integer.toString(workID);
				String title = workItem.getTitle().replaceAll("\\,", "");
				String autoStatus =  workItem.getFields().getField(FIELD_AUTOMATION_STATUS).getValue().toString();
				CSVUtils.writeCSVFile(workItemID,title,autoStatus);
			}
			Log.info("Writting Data into CSV File is completed");
		} else {
			Log.info("No WorkItem found with current query");
		}
	}

}
//MobiControl\FTR\MADP