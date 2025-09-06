package com.ai.aitest.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.ai.aitest.util.*;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import javafx.scene.layout.*;
import com.ai.aitest.*;
import javafx.scene.control.*;

public class MyImageVectorSearch_Controller {

	ObjectStorageUtil objStore;
	@FXML
	private Button bt_buckets;

	@FXML
	private Button bt_copydoc;
	@FXML
	private Button bt_doc;

	@FXML
	private Button bt_vectortable;
	@FXML
	private Button bt_chunk;
	@FXML
	private Button 	bt_embedding_search;
	@FXML
	private Button 	bt_vectormatch;
	@FXML
	private Button 	bt_vectorize;
	@FXML
	private Button 	bt_submit;
	@FXML
	private Button 	bt_clear;
	
	@FXML
	private TextArea ta_prompt;
	
	@FXML
	private TextArea ta_display;
	
	@FXML
	private ComboBox<String> cb_bucketname;

	@FXML
	private TableView tv_showdata;

	@FXML
	private ComboBox<String> cb_filename;

	@FXML
	private Label lb_file;

	@FXML
	private Label lb_bucketsize;

	String selectedBucket;

	String queryStatement = "select * from documentation_tab";

	String dbUserName = "nationalparks";
	
	String searchVector;
	
	String matchResult;

	// DBUtil dbUtil=DBUtil.getDBUtil();

	final String DB_URL = "jdbc:oracle:thin:@zooqdb2b4qoa2k79_medium?TNS_ADMIN=c:/software/Wallet_ZOOQDB2B4QOA2K79";
	public String DB_PASSWORD = "Liuxin@dyy123456";
	//final String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";
	
	    
		public Connection getDBConnection() throws Exception{
			
			Connection connection = DriverManager.getConnection(DB_URL, dbUserName, DB_PASSWORD);
			System.out.println("Connected to Oracle Autonomous Database successfully!");
			return connection;

	}


	public void showTable(Connection conn,String queryStatement) throws Exception {
		System.out.println("in the showTable");


		Statement statement = conn.createStatement();
		
		ResultSet queryResult = statement.executeQuery(queryStatement);
		System.out.println("after executeQuery" + queryStatement);
		System.out.println("-----------------------------------------------------------");
		System.out.println("\nCongratulations! MyImageVectorSearch You have successfully used Oracle ADB\n");

		var metadata = queryResult.getMetaData();
		int columnCount = metadata.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			System.out.println("metadata.getColumnLabel(i) " + i + " " + metadata.getColumnName(i));
		}

		for (int i = 1; i <= columnCount; i++) {

			final int idx = i;
			var column = new TableColumn<Object[], Object>(metadata.getColumnLabel(idx));
			column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()[idx - 1]));
			tv_showdata.getColumns().add(column);
			System.out.println("column is :" + column);
		}

		var items = tv_showdata.getItems();
		while (queryResult.next()) {
			var row = new Object[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				//row[i - 1] = queryResult.getObject(i);
				row[i - 1] = queryResult.getString(i);
			}
			items.add(row);
			System.out.println("row is :" + row);
		}

		queryResult.close();

//        	    procin.close();


	}

	public void initialize() {

		// dbUtil.setDbUserName(dbUserName);
		
		tv_showdata.setEditable(true);
		tv_showdata.getSelectionModel().setCellSelectionEnabled(true);
		tv_showdata.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		objStore = ObjectStorageUtil.getInstance();
		
		/////////////show all buckets in the ComboBox///////
		bt_buckets.setOnAction(e -> {
			cb_bucketname.getItems().clear();
			List<String> listBuckets = objStore.getAllBuckets();

			if (listBuckets != null) {
				for (String bucket : listBuckets) {
					// System.out.println(file.getName());
					cb_bucketname.getItems().add(bucket);
				}
			}

		});
		
		////////////list all of the files of a bucket in the ComboBox and set the selectedBucket variable////////
		cb_bucketname.setOnAction((event) -> {

			int selectedIndex = cb_bucketname.getSelectionModel().getSelectedIndex();
			selectedBucket = cb_bucketname.getSelectionModel().getSelectedItem();

			System.out.println("Selection made: [" + selectedIndex + "] " + selectedBucket);
			System.out.println("   cb_bucketname.getValue(): " + cb_bucketname.getValue());
			// System.out.println("cb_bucketname.getSelectionModel().getSelectedItem(): "
			// + cb_bucketname.getSelectionModel().getSelectedItem());

			cb_filename.getItems().clear();
			List<String> listObjectNames = objStore.getBucketObjects(selectedBucket);

			lb_bucketsize.setText("" + listObjectNames.size());
			if (listObjectNames != null) {
				for (String file : listObjectNames) {
					// System.out.println(file.getName());
					cb_filename.getItems().add(file);
				}
			}

		});

		//////////////////////
		/*
		 * cb_filename.setOnAction((event) -> {
		 * 
		 * 
		 * int selectedIndex = cb_filename.getSelectionModel().getSelectedIndex();
		 * selectedFile = cb_filename.getSelectionModel().getSelectedItem();
		 * 
		 * System.out.println("Selection made: [" + selectedIndex + "] " +
		 * selectedFile); System.out.println("   cb_bucketname.getValue(): " +
		 * cb_bucketname.getValue()); //
		 * System.out.println("cb_bucketname.getSelectionModel().getSelectedItem(): " //
		 * + cb_bucketname.getSelectionModel().getSelectedItem());
		 * lb_file.setText(selectedFile);
		 * 
		 * });
		 */
		
		///////////show all rows in the table of documentation_tab/////////
		bt_doc.setOnAction(e -> {
			tv_showdata.getItems().clear();
			tv_showdata.getColumns().clear();

			try {
		
				Connection conn = getDBConnection();
		
				Statement statement = conn.createStatement();
				
				ResultSet queryResult = statement.executeQuery(queryStatement);
				System.out.println("after executeQuery");
				System.out.println("-----------------------------------------------------------");
				System.out.println("\nCongratulations! MyVectorSearch You have successfully used Oracle ADB\n");

				var metadata = queryResult.getMetaData();
				int columnCount = metadata.getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					System.out.println("metadata.getColumnLabel(i) " + i + " " + metadata.getColumnName(i));
				}

				for (int i = 1; i <= columnCount; i++) {

					final int idx = i;
					var column = new TableColumn<Object[], Object>(metadata.getColumnLabel(idx));
					column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()[idx - 1]));
					tv_showdata.getColumns().add(column);
					System.out.println("column is :" + column);
				}

				var items = tv_showdata.getItems();
				while (queryResult.next()) {
					var row = new Object[columnCount];
					for (int i = 1; i <= columnCount; i++) {
						row[i - 1] = queryResult.getObject(i);
					}
					items.add(row);
					System.out.println("row is :" + row);
				}

				queryResult.close();
				statement.close();
				conn.close();

			} catch (Exception ee) {
				System.out.println(ee);
			}

		});
		
		
		/////copy all files from bucket to documentation_tab/////////////////
		bt_copydoc.setOnAction(e -> {
			String queryStatement = "select * from documentation_tab";
			//if(cb_bucketname.get)
			CallableStatement callableStatement = null;
			Connection conn = null;
			// Prepare PL/SQL block
			String plsql = "declare\r\n" + "   l_blob blob := null;\r\n"
					+ "   l_bucket varchar2(4000) := 'https://objectstorage.uk-london-1.oraclecloud.com/n/lrgpkhlte873/b/"
					+ selectedBucket + "/o/';\r\n" + "begin\r\n"
					+ "for i in (select * from dbms_cloud.list_objects('OCI_CRED_VECTOR',l_bucket))\r\n"
					+ "loop\r\n" + "   l_blob := dbms_cloud.get_object(\r\n"
					+ "     credential_name => 'OCI_CRED_VECTOR',\r\n"
					+ "     object_uri => l_bucket||i.object_name);\r\n"
					+ "insert into documentation_tab (file_name, file_size, file_type, file_content)\r\n"
					+ "            values(i.object_name, i.bytes, 'mime/image',l_blob);\r\n" + "commit;\r\n"
					+ "end loop;\r\n" + "end;";
			try {
				conn = getDBConnection();
				String truncateTablesql = "truncate table documentation_tab";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(truncateTablesql);
				System.out.println("documentation_tab table truncated!...");

				callableStatement = conn.prepareCall(plsql);

				// Execute the PL/SQL block
				callableStatement.execute();
				showTable(conn,queryStatement);
				// If you need to fetch results, handle here
				System.out.println("PL/SQL block executed successfully");
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				// Close resources
				try {
					if (callableStatement != null)
						callableStatement.close();
				} catch (SQLException ex1) {
				}
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException ex2) {
				}
			}
		});
		
////show all rows in the DOCUMENTATION_VECTOR_TAB////////////	        	
		
		bt_vectortable.setOnAction(e -> {
			System.out.println("You are in bt_vector");
			tv_showdata.getItems().clear();
			tv_showdata.getColumns().clear();

			try {
				String  queryStatement = "select * from documentation_tab";
				//Connection conn = pds.getConnection();
				
				Connection conn= getDBConnection();
				
				showTable(conn,queryStatement);
				// procin.close();
				conn.close();

			} catch (Exception ee) {
				System.out.println(ee);
			}

		});
		
/*
 * 1. truncate the DOCUMENTATION_VECTOR_TAB
 * 2. utl_to_text: transform the file_content column in the DOCUMENTATION_TAB into text
 * 3. utl_to_chunks chunk the text into 200 word segments.
 * 4. insert into chunk_data of DOCUMENTATION_VECTOR_TAB
 */
		
		bt_chunk.setOnAction(e -> {
			
			System.out.println("You are in bt_chunk");
			tv_showdata.getItems().clear();
			tv_showdata.getColumns().clear();

			String  queryStatement = "select * from documentation_tab";
			try {
			
				Connection conn = getDBConnection();
				
				String truncateTablesql = "truncate table documentation_tab";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(truncateTablesql);
				System.out.println("DOCUMENTATION_VECTOR_TAB table truncated!...");

				String plsql = "insert into documentation_vector_tab\r\n"
						+ "select dt.id\r\n"
						+ "        , json_value(c.column_value, '$.chunk_id' returning number) as chunk_id\r\n"
						+ "        , json_value(c.column_value, '$.chunk_offset' returning number) as chunk_pos\r\n"
						+ "        , json_value(c.column_value, '$.chunk_length' returning number) as chunk_size\r\n"
						+ "        , replace(json_value(c.column_value, '$.chunk_data'),chr(10),'') as chunk_txt\r\n"
						+ "        , null -- this is the vector column, we'll populate it later\r\n"
						+ "from \r\n"
						+ "------- base table ---------\r\n"
						+ "documentation_tab dt,\r\n"
						+ "------- doc to text query ---------\r\n"
						+ "(select id\r\n"
						+ "        , dbms_vector_chain.utl_to_text (dt.file_content, json('{\"plaintext\":\"true\",\"charset\":\"utf8\"}')) file_text\r\n"
						+ "    from documentation_tab dt) t,\r\n"
						+ "------- chunking ---------\r\n"
						+ "dbms_vector_chain.utl_to_chunks(t.file_text,\r\n"
						+ "   json('{ \"by\":\"words\",\r\n"
						+ "           \"max\":\"200\",\r\n"
						+ "           \"overlap\":\"0\",\r\n"
						+ "           \"split\":\"sentence\",\r\n"
						+ "           \"language\":\"american\",\r\n"
						+ "           \"normalize\":\"all\" }')) c\r\n"
						+ "------- joins ---------\r\n"
						+ "where dt.id = t.id\r\n";

				
				stmt.executeUpdate(plsql);
				showTable(conn,queryStatement);

				
				conn.close();

			} catch (Exception ee) {
				System.out.println(ee);
			}
		});
		
		/*
		 * 
		 */
		
		bt_vectorize.setOnAction(e -> {
			
			System.out.println("You are in bt_vectorize");
			tv_showdata.getItems().clear();
			tv_showdata.getColumns().clear();
			
			try {
			
				Connection conn = getDBConnection();
				
				Statement stmt = conn.createStatement();
				
				//String truncateTablesql = "truncate table DOCUMENTATION_VECTOR_TAB";
				//Statement stmt = conn.createStatement();
				//stmt.executeUpdate(truncateTablesql);
				//System.out.println("DOCUMENTATION_VECTOR_TAB table truncated!...");

				String plsql = "begin\r\n"
						+ "for c in 1..100 loop\r\n"
						+ "for i in (select * from documentation_vector_tab where rownum<100 and embed_vector is null)\r\n"
						+ "loop\r\n"
						+ "update documentation_vector_tab\r\n"
						+ "set embed_vector = dbms_vector.utl_to_embedding(chunk_txt, json('{\r\n"
						+ "  \"provider\": \"database\",\r\n"
						+ "  \"model\": \"all_MiniLM_L12_v2\"\r\n"
						+ "}'))\r\n"
						+ "where i.id = id\r\n"
						+ "and i.chunk_id = chunk_id;\r\n"
						+ "commit;\r\n"
						+ "end loop;\r\n"
						+ "end loop;\r\n"
						+ "end;";
				
				stmt.executeUpdate(plsql);
				showTable(conn,queryStatement);

				stmt.close();
				conn.close();
			} catch (Exception ee) {
				System.out.println(ee);
			}		
		});
		
		
		/*
		 * 
		 */
		
		bt_embedding_search.setOnAction(e -> {

			//tv_showdata.getItems().clear();
			//tv_showdata.getColumns().clear();
			
			String search=ta_prompt.getText();
			
			String  queryStatement = "select dbms_vector.utl_to_embedding('"+search+"', json('{\r\n"
					+ "          \"provider\": \"database\",\r\n"
					+ " 	  \"model\": \"all_MiniLM_L12_v2\"\r\n"
					+ "}'))";
			try {
			Connection conn = getDBConnection();
						
			showTable(conn,queryStatement);
			
			showVector(conn,queryStatement);
			
			conn.close();
			}catch (Exception ee) {}
		});
		
		//////////////////////
		
		bt_vectormatch.setOnAction(e -> {
			
			System.out.println("------------- In the  bt_vectormatch");
			ta_display.setText("");
			//ta_prompt.setText("");

			tv_showdata.getItems().clear();
			tv_showdata.getColumns().clear();
			
			String vectorSearchStatement ="{ call vector_match(?,?) }";

			try {
			Connection conn = getDBConnection();
				
			CallableStatement callableStatement = conn.prepareCall(vectorSearchStatement);
			System.out.println("ta_prompt.getText() is: "+ ta_prompt.getText());
			callableStatement.setString(1, ta_prompt.getText());		
			callableStatement.registerOutParameter(2, java.sql.Types.VARCHAR);
			 callableStatement.executeUpdate();
			 matchResult = callableStatement.getString(2);
			 ta_display.setText(matchResult);
			
			conn.close();
			}catch (Exception ee) {
				System.out.println("Exception in bt_vectormatch");
			}
		});
		
		bt_submit.setOnAction(e -> {
			
        	String seperator ="\n\n----------------------- returning Message from LLM: meta.llama-3.2-90b-vision-instruct ------------------------\n\n";
        	
        	String seperator_Prompt ="\n\n----------------------- A New Prompt for LLM: meta.llama-3.2-90b-vision-instruct ------------------------\n\n";
        	
        	
        	String prompt = ta_display.getText();
        	
        	ChatLlama.getChatLlamaInstance().setPrompt(prompt);
        	ta_display.setText("");	
        	ta_display.appendText(seperator_Prompt);
        	ta_display.appendText(prompt);
        	
                	
        	try {
        	String chatResult= ChatLlama.getChatLlamaInstance().chat();
        	ta_display.appendText(seperator);
        	ta_display.appendText(chatResult);
        	}catch (Exception e1) {}     	
		});
		
		bt_clear.setOnAction(e -> {
			ta_display.clear();
			ta_prompt.clear();
			
		});
		

	}
	
	public void showVector(Connection conn,String queryStatement) throws Exception  {
		
		System.out.println("in the showVector");

		Statement statement = conn.createStatement();
		
		ResultSet rs = statement.executeQuery(queryStatement);
		System.out.println("after executeQuery" + queryStatement);
		System.out.println("-----------------------------------------------------------");
		System.out.println("\nCongratulations! MyVectorSearch You have successfully used Oracle ADB\n");

		searchVector=rs.getString(1);
		//while (rs.next()) {
			System.out.println("rs :"+ rs.getString(1));
			//ta_display.appendText("------ below is Generated Vector from Prompt:------");
			ta_display.appendText(searchVector);
		//	}
		rs.close();
		statement.close();

//        	    procin.close();

	}

	public void vectorMatch(Connection conn,String queryStatement) throws Exception  {
		
		System.out.println("in the vectorMatch");
		Statement statement = conn.createStatement();
		
		ResultSet rs = statement.executeQuery(queryStatement);
		System.out.println("after executeQuery" + queryStatement);
		System.out.println("-----------------------------------------------------------");
		System.out.println("\nCongratulations! MyVectorSearch You have successfully used Oracle ADB\n");

		searchVector=rs.getString(1);
		//while (rs.next()) {
			System.out.println("rs :"+ rs.getString(1));
			//ta_display.appendText("------ below is Generated Vector from Prompt:------");
			ta_display.appendText(searchVector);
		//	}
		rs.close();
		statement.close();

		
	}
	
}

