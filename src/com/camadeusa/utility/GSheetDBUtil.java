package com.camadeusa.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;

import com.camadeusa.NetworkCore;
import com.camadeusa.player.NetworkPlayer;
import com.camadeusa.player.PlayerRank;
import com.camadeusa.utility.fetcher.ArchrCallback;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

/*
 * Author: CAmadeusA
 * Uses Google Sheet like a relational database, made easy. Still need to update for oauth2 tomorrow. 
 * 
 * Usage:
 * 
 * Adding Data:
 * addData({your Map<String, Object> data})
 * 
 * Searching for data from a row:
 * for (ArrayList<Map<String, Object>> row : searchDataForString(myUUID or myName)) {
 * row.Foo.Bar();
 * }
 * 
 * Getting data for a specific row:
 * ListEntry row = getRow({ColumnKey}, {ColumnValue});
 * Map<String, Object> rowValues = getRowData(row);
 * 
 * 
 * Editing Data:
 * ListEntry row = getRow({ColumnKey}, {ColumnValue});
 * updateRow(row, {rowValues})
 * row.update();
 * 
 * 
 * Deleting Data:
 * ListEntry row = getRow({ColumnKey}, {ColumnValue});
 * row.delete();
 */

public class GSheetDBUtil {

	// Flags to define later:
	private  final String email = "camadeusa@archrnetwork.iam.gserviceaccount.com";
	private  File servicePKCS12File = null;

	private  final String SPREADSHEET_SERVICE_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
	private  SpreadsheetService service;
	public boolean connected = false;

	String sheetname;
	String worksheetname;
	
	public GSheetDBUtil(String sheetName_, String worksheetName) {
		try {
			sheetname = sheetName_;
			worksheetname = worksheetName;
			servicePKCS12File = new File(new File("").getAbsolutePath() + "/resources/ArchrNetwork-1145bb40af97.p12");
			createSpreadSheetService();
		} catch (GeneralSecurityException | IOException | ServiceException e) {
			e.printStackTrace();
		}

	}
	
	private void createSpreadSheetService() throws GeneralSecurityException, IOException, ServiceException {
		   HttpTransport httpTransport = new NetHttpTransport();
		   JacksonFactory jsonFactory = new JacksonFactory();
		   String [] SCOPESArray= {"https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds"};
		   final List SCOPES = Arrays.asList(SCOPESArray);
		   GoogleCredential credential = new GoogleCredential.Builder()
		     .setTransport(httpTransport)
		     .setJsonFactory(jsonFactory)
		     .setServiceAccountId(email)
		     .setServiceAccountScopes(SCOPES)
		     .setServiceAccountPrivateKeyFromP12File(servicePKCS12File)
		     .build();

		   service = new SpreadsheetService("data");
		   service.setOAuth2Credentials(credential);
		   connected = true;
	}

	private SpreadsheetEntry getSpreadsheet(String sheetName) {
		try {
			URL spreadSheetFeedUrl = new URL(SPREADSHEET_SERVICE_URL);

			SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(spreadSheetFeedUrl);
			spreadsheetQuery.setTitleQuery(sheetName);
			spreadsheetQuery.setTitleExact(true);
			SpreadsheetFeed spreadsheet = service.getFeed(spreadsheetQuery, SpreadsheetFeed.class);

			if (spreadsheet.getEntries() != null && spreadsheet.getEntries().size() == 1) {
				return spreadsheet.getEntries().get(0);
			} else {
				return null;
			}
		} catch (Exception ex) {
			//
		}

		return null;
	}

	private WorksheetEntry getWorkSheet(String sheetName, String workSheetName) {
		try {
			SpreadsheetEntry spreadsheet = getSpreadsheet(sheetName);

			if (spreadsheet != null) {
				WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
				List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

				for (WorksheetEntry worksheetEntry : worksheets) {
					String wktName = worksheetEntry.getTitle().getPlainText();
					if (wktName.equals(workSheetName)) {
						return worksheetEntry;
					}
				}
			}
		} catch (Exception ex) {
			//
		}

		return null;
	}

	public Map<String, Object> getRowData(ListEntry row) {
		Map<String, Object> rowValues = new HashMap<String, Object>();
		for (String tag : row.getCustomElements().getTags()) {
			Object value = row.getCustomElements().getValue(tag);
			rowValues.put(tag, value);
		}
		return rowValues;
	}

	private ListEntry createRow(Map<String, Object> rowValues) {
		ListEntry row = new ListEntry();
		for (String columnName : rowValues.keySet()) {
			Object value = rowValues.get(columnName);
			row.getCustomElements().setValueLocal(columnName, String.valueOf(value));
		}
		return row;
	}

	public void updateRow(ListEntry row, Map<String, Object> rowValues) {
		for (String columnName : rowValues.keySet()) {
			Object value = rowValues.get(columnName);
			row.getCustomElements().setValueLocal(columnName, String.valueOf(value));
		}
	}
	
	/*
	 * Returns a list of row values. Can iterate through all possible matches. 
	 */
	
	public  ArrayList<Map<String, Object>> searchDataForString(String query) {
		WorksheetEntry worksheet = getWorkSheet(sheetname, worksheetname);
		URL listFeedUrl = worksheet.getListFeedUrl();
		ListQuery listQuery = new ListQuery(listFeedUrl);
		listQuery.setFullTextQuery(query);

		ListFeed listFeed = null;
		try {
			listFeed = service.query(listQuery, ListFeed.class);
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Map<String, Object>> rowList = new ArrayList<>();
		for (ListEntry row : listFeed.getEntries()) {
		    Map<String, Object> rowValues = getRowData(row); // Blah
		    rowList.add(rowValues);
		}
		return rowList;
	}
	
	// https://developers.google.com/chart/interactive/docs/querylanguage?csw=1#Format
	public ArrayList<Map<String, Object>> queryData(String query) {
		WorksheetEntry worksheet = getWorkSheet(sheetname, worksheetname);
		URL listFeedUrl = worksheet.getListFeedUrl();
		ListQuery listQuery = new ListQuery(listFeedUrl);
		listQuery.setSpreadsheetQuery(query);

		ListFeed listFeed = null;
		try {
			listFeed = service.query(listQuery, ListFeed.class);
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Map<String, Object>> rowList = new ArrayList<>();
		for (ListEntry row : listFeed.getEntries()) {
		    Map<String, Object> rowValues = getRowData(row);
		    rowList.add(rowValues);
		}
		return rowList;
	}

	private ListFeed getListFeedForRow(String pkColumnName,
			Object pkColumnValue) throws Exception {
		WorksheetEntry worksheet = getWorkSheet(sheetname, worksheetname);
		URL listFeedUrl = worksheet.getListFeedUrl();
		ListQuery listQuery = new ListQuery(listFeedUrl);
		if (pkColumnValue instanceof String) {
			listQuery.setSpreadsheetQuery(pkColumnName + " = \"" + pkColumnValue + "\"");
		} else {
			listQuery.setSpreadsheetQuery(pkColumnName + " = " + pkColumnValue);
		}

		return service.query(listQuery, ListFeed.class);
	}

	/*
	 * Gets the row for specified column id and (row) value. 
	 */
	
	public ListEntry getRow(String pkColumnName, Object pkColumnValue)
			throws Exception {
		ListFeed listFeed = getListFeedForRow(pkColumnName, pkColumnValue);
		if (listFeed.getEntries().size() == 1) {
			return listFeed.getEntries().get(0);
		} else {
			return null;
		}
	}

	public void addData(Map<String, Object> data) {
		WorksheetEntry worksheet = getWorkSheet(sheetname, worksheetname);
		URL listFeedUrl = worksheet.getListFeedUrl();
		Map<String, Object> rowValues = data;
		ListEntry row = createRow(rowValues);
		try {
			row = service.insert(listFeedUrl, row);
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
