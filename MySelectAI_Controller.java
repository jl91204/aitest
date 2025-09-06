package com.ai.aitest.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.ai.aitest.util.*;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.*;
import com.ai.aitest.*;
import javafx.scene.control.*;


public class MySelectAI_Controller {


    @FXML
    private ComboBox<String> cb_tablename;
    
    @FXML
    private TextArea ta_prompt;
    
    @FXML
    private ComboBox<String>    myComboBox_example;
    
    @FXML
    private Button bt_showtablenames;
    
    @FXML
    private Button  bt_submit;
    
    @FXML
    private Button  bt_clear;
    
    @FXML
    private Label lb_tableno;
    
    @FXML
    private TableView tv_showdata;
    
    String currentTable;
    
    DBUtil dbUtil=DBUtil.getDBUtil();
    
    
    ObservableList<String> items;
   
    String dbUserName = "SELECTAIAPP_USER";
    
	final String DB_URL = "jdbc:oracle:thin:@zooqdb2b4qoa2k79_medium?TNS_ADMIN=/home/opc/Wallet_ZOOQDB2B4QOA2K79";

	public String DB_PASSWORD = "Liuxin@dyy123456";
	
	

	public Connection getDBConnection() throws Exception{
		
		Connection connection = DriverManager.getConnection(DB_URL, dbUserName, DB_PASSWORD);
		System.out.println("Connected to Oracle Autonomous Database successfully!");
		return connection;

	
	
}

	
    public void initialize() {
    	
    	tv_showdata.setEditable(true);
    	tv_showdata.getSelectionModel().cellSelectionEnabledProperty().set(true);
		
		tv_showdata.getSelectionModel().setCellSelectionEnabled(true);
		tv_showdata.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
        myComboBox_example.getItems().addAll("SELCT AI RUNSQL", "SELCT AI SHOWSQL", 
        		"SELCT AI EXPLAINSQL", "SELECT AI NARRATE","SELECT AI CHAT",
        		"SELCT AI RUNSQL1","SELCT AI RUNSQL2");
        
        ////////////
    	//imageView.fitHeightProperty().bind( imageView.getParent().layoutBoundsProperty().get()).heightProperty());
    	bt_showtablenames.setOnAction(e -> {
    		dbUtil.setDbUserName(dbUserName);
        	String seperator ="\n\n-------------------button show db names  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
            items= 
            		FXCollections.observableArrayList (dbUtil.listAllTableNames());   
            cb_tablename.getItems().addAll(items);  	
        });
    	////////////////
    	cb_tablename.setOnAction((event) -> {
    		dbUtil.setDbUserName(dbUserName);
            int selectedIndex = cb_tablename.getSelectionModel().getSelectedIndex();
            Object selectedItem = cb_tablename.getSelectionModel().getSelectedItem();

            System.out.println("selectedItem.toString() " + selectedIndex + "] " + selectedItem.toString());
            System.out.println("   ComboBox.getValue(): " + cb_tablename.getValue());
            int displaySelectedIndex = selectedIndex+1;
            lb_tableno.setText(displaySelectedIndex+"/"+items.size());
            
            tv_showdata.getItems().clear();
            tv_showdata.getColumns().clear();
            
            String queryStatement = "select * from "+selectedItem.toString();
            
            try {
        		System.out.println("\n Query is : " + queryStatement);
        		Connection conn = getDBConnection();
        		//Connection conn = dbUtil.pds.getConnection();
        				// conn.setAutoCommit(false);
        				// Prepare a statement to execute the SQL Queries.
        		Statement statement = conn.createStatement();
        				// Select 20 rows from the CUSTOMERS table from SH schema.
        		ResultSet queryResult = statement.executeQuery(queryStatement);

        		System.out.println("-----------------------------------------------------------");
        		System.out.println("\nCongratulations! You have successfully used Oracle ADB\n");
           // ResultSet queryResult= dbUtil.doQuery("select * from "+selectedItem.toString());
            //tv_showdata=dbUtil.toTableView(resultSet);
        		var metadata = queryResult.getMetaData();
        		int columnCount = metadata.getColumnCount();
        		for (int i=1;i<=columnCount;i++) {
        			System.out.println("metadata.getColumnLabel(i) "+i+" "+metadata.getColumnName(i));
        		}
            
  		  for (int i = 1; i <= columnCount; i++) {
			  
  		    final int idx = i;
  		    var column = new TableColumn<Object[], Object>(metadata.getColumnLabel(idx));
  		    column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()[idx - 1]));
  		    tv_showdata.getColumns().add(column);
  		    System.out.println("column is :"+column);
  		  }
  		  
  		  var items = tv_showdata.getItems();
  		  while (queryResult.next()) {
  		    var row = new Object[columnCount];
  		    for (int i = 1; i <= columnCount; i++) {
  		      row[i - 1] = queryResult.getObject(i);
  		    }
  		    items.add(row);
  		    System.out.println("row is :"+row);
  		  }
  		   
  		  conn.close();
  		  queryResult.close();
  		  
  		  
            }catch(Exception e) {System.out.println(e);}
        });
    	///////////////////////////
    	bt_submit.setOnAction(e -> {
    		dbUtil.setDbUserName(dbUserName);
        	String seperator ="\n\n-------------------button display select ai  ------------------------\n\n";
        	//ta_display.appendText(rag_LangChain.textSegments.toString());
            tv_showdata.getItems().clear();
            tv_showdata.getColumns().clear();
  	
        	try {
           		Connection conn = getDBConnection();
        		//Connection conn = dbUtil.pds.getConnection();
        	    CallableStatement procin = conn.prepareCall ("begin DBMS_CLOUD_AI.SET_PROFILE (?); end;");
        	    procin.setString (1, "MYGENAI_LLAMA");
        	    procin.execute ();
        	    
        	    String queryStatement = ta_prompt.getText();
        	    
        		Statement statement = conn.createStatement();
				// Select 20 rows from the CUSTOMERS table from SH schema.
		ResultSet queryResult = statement.executeQuery(queryStatement);

		System.out.println("-----------------------------------------------------------");
		System.out.println("\nCongratulations! You have successfully used Oracle ADB\n");
   // ResultSet queryResult= dbUtil.doQuery("select * from "+selectedItem.toString());
    //tv_showdata=dbUtil.toTableView(resultSet);
		var metadata = queryResult.getMetaData();
		int columnCount = metadata.getColumnCount();
		for (int i=1;i<=columnCount;i++) {
			System.out.println("metadata.getColumnLabel(i) "+i+" "+metadata.getColumnName(i));
		}
    
	  for (int i = 1; i <= columnCount; i++) {
	  
	    final int idx = i;
	    var column = new TableColumn<Object[], Object>(metadata.getColumnLabel(idx));
	    column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()[idx - 1]));
	    tv_showdata.getColumns().add(column);
	    System.out.println("column is :"+column);
	  }
	  
	  var items = tv_showdata.getItems();
	  while (queryResult.next()) {
	    var row = new Object[columnCount];
	    for (int i = 1; i <= columnCount; i++) {
	      //row[i - 1] = queryResult.getObject(i);
	    	row[i - 1] = queryResult.getString(i);
	    }
	    items.add(row);
	    System.out.println("row is :"+row);
	  }
	   
		  queryResult.close();
        	    
        	    procin.close();
        	    conn.close();
        		
        	}catch (Exception ee) {}
        	
        });
    	
       bt_clear.setOnAction(e -> {  
        	
        	//vb_generative.getChildren().clear();
        
           tv_showdata.getItems().clear();
           tv_showdata.getColumns().clear();
           ta_prompt.setText("");
   	
        });
       
       myComboBox_example.setOnAction((event) -> {
           int selectedIndex = myComboBox_example.getSelectionModel().getSelectedIndex();
           Object selectedItem = myComboBox_example.getSelectionModel().getSelectedItem();

           System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
           System.out.println("   ComboBox.getValue(): " + myComboBox_example.getValue());
           
           String runsql="SELECT AI the three people who earn the most.";
           String showsql="SELECT AI showsql the three people who earn the most.";   
           String explainsql = "SELECT AI explainsql the three people who earn the most.";
           String chat = "SELECT AI chat the three people who earn the most.";
           String narrate = "SELECT AI narrate the three people who earn the most.";
           String runsql1 = "SELECT AI who worked for IT_PROG.";
           String runsql2 = "SELECT AI who worked for IT_PROG in the history.";
           
           switch(selectedIndex) {
			case 0: 
				ta_prompt.setText("");
				ta_prompt.setText(runsql);
				break;
			case 1:
				ta_prompt.setText("");
				ta_prompt.setText(showsql);
				break;
			case 2:
				ta_prompt.setText("");
				ta_prompt.setText(explainsql);
				break;
			case 3:
				ta_prompt.setText("");
				ta_prompt.setText(narrate);
				break;
			case 4:
				ta_prompt.setText("");
				ta_prompt.setText(chat);
				break;
			case 5:
				ta_prompt.setText("");
				ta_prompt.setText(runsql1);
				break;
			case 6:
				ta_prompt.setText("");
				ta_prompt.setText(runsql2);
				break;
           }
           
       });
       
       
       tv_showdata.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {       
 
           @Override    
           public void changed(ObservableValue observable,Object oldValue, Object newValue) {    
           System.out.println("selection change"+observable.getValue())      ;
           copyText();
         //  System.out.println("oldValue"+oldValue);   
         //  System.out.println("newValue"+newValue.getClass().getCanonicalName());   
         //  copyText();
          /* String elementTableSelected=null;
           //elementTableSelected=tv_showdata.getSelectionModel().getSelectedCells().get(0).toString();
           Iterator it=tv_showdata.getColumns().iterator();
           Iterator i=tv_showdata.getSelectionModel().getSelectedCells().iterator();
           
           //TableColumn t=(TableColumn) i.next();
           while (it.hasNext()) { 
        	   TableColumn t=(TableColumn) it.next();
        	   System.out.println("it.next().toString()"+t.getText());}
           //System.out.println(t.getText());
           
           
           while (i.hasNext()) { 
        	   TablePosition t=(TablePosition) i.next();
        	   System.out.println("i.next().getRow()"+t.getRow());
        	   //System.out.println("i.next().getColumn()"+t.getColumn());
        	   Object person = tv_showdata.getItems().get(t.getRow());
        	   System.out.println("person.toString()"+person.toString());
        	   }
*/
           }
    
    });
    
       
    
       
     /*  ListChangeListener indicesListener = new   ListChangeListener() {    
           @Override public void onChanged(Change c) {    
               while (c.next()) {    
                    
                 //  selectionUpdated(c.getAddedSubList(), c.getRemoved());    
               }    
           }    
       }; 
       */
       
      
    }
       
    public void copyText() {
     //  String elementTableSelected = tv_showdata.getSelectionModel().getSelectedCells().iterator();
        Iterator i=tv_showdata.getSelectionModel().getSelectedCells().iterator();
    //	Iterate i=tv_showdata.getColumns().iterator();
        String elementTableSelected=null;
        while (i.hasNext()) {       elementTableSelected=i.next().toString();}
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(elementTableSelected);
        clipboard.setContent(content);
        System.out.println(elementTableSelected);
    }
    
    
}
