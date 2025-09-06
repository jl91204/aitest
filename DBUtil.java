package com.ai.aitest.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

public class DBUtil {

	public String dbUserName ="SELECTAIAPP_USER";;
	public String DB_PASSWORD = "Liuxin@dyy123456";
	final String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";
	public static List<String> list_tableNames;

	final String DB_URL = "jdbc:oracle:thin:@zooqdb2b4qoa2k79_medium?TNS_ADMIN=/home/opc/Wallet_ZOOQDB2B4QOA2K79";

	public PoolDataSource pds;
	
	private static DBUtil instance;
	
	public static synchronized DBUtil getDBUtil() {
	if (instance == null) {
	instance = new DBUtil();
	}
	return instance;
	}
	
	public void setDbUserName(String dbUserName) {
		this.dbUserName=dbUserName;
		
	}
	

	private DBUtil() {myInit();}
	
	public void myInit() {
		
		
		final String CONN_FACTORY_CLASS_NAME = "oracle.jdbc.pool.OracleDataSource";
		//this.dbUserName=dbUserName;

		try {
			pds = PoolDataSourceFactory.getPoolDataSource();

			// Set the connection factory first before all other properties
			pds.setConnectionFactoryClassName(CONN_FACTORY_CLASS_NAME);
			pds.setURL(DB_URL);
			pds.setUser(dbUserName);
			pds.setPassword(DB_PASSWORD);
			pds.setConnectionPoolName("JDBC_UCP_POOL");

			// Default is 0. Set the initial number of connections to be created
			// when UCP is started.
			pds.setInitialPoolSize(5);

			// Default is 0. Set the minimum number of connections
			// that is maintained by UCP at runtime.
			pds.setMinPoolSize(5);

			// Default is Integer.MAX_VALUE (2147483647). Set the maximum number of
			// connections allowed on the connection pool.
			
			System.out.println("you are in myInit()");
			pds.setMaxPoolSize(5);
		} catch (Exception e) {
		}

	}

	public List<String> listAllTableNames() {

		list_tableNames = new ArrayList<String>();
		try (Connection conn = pds.getConnection()) {
			System.out.println("Available connections after checkout: " + pds.getAvailableConnectionsCount());
			System.out.println("Borrowed connections after checkout: " + pds.getBorrowedConnectionsCount());
			// Perform a database operation
			String queryStatement = "SELECT table_name FROM user_tables order by table_name ASC";

			Statement statement = conn.createStatement();

			ResultSet resultSet = statement.executeQuery(queryStatement);

			// System.out.println(String.join(" ", "\nCUST_ID", "CUST_FIRST_NAME",
			// "CUST_LAST_NAME", "CUST_CITY", "CUST_CREDIT_LIMIT"));
			System.out.println("-----------------------------------------------------------");
			while (resultSet.next()) {
				list_tableNames.add(resultSet.getString(1));
				System.out.println(resultSet.getString(1));
			}
			System.out.println("\nCongratulations! You have successfully used Oracle Autonomous Database\n");

			// ObservableList<String> items
			// =FXCollections.observableArrayList(list_tableNames);

		} catch (SQLException e) {
			System.out.println("ADBQuickStart - " + "listAllTableNames()- SQLException occurred : " + e.getMessage());
		}

		return list_tableNames;
	}

	public ResultSet doQuery(String queryStatement) throws Exception {
		// String queryStatement = "SELECT CUST_ID, CUST_FIRST_NAME, CUST_LAST_NAME,
		// CUST_CITY,"
		// + "CUST_CREDIT_LIMIT FROM SH.CUSTOMERS WHERE ROWNUM < 20 order by CUST_ID";

		System.out.println("\n Query is : " + queryStatement);

		Connection conn = pds.getConnection();
				// conn.setAutoCommit(false);
				// Prepare a statement to execute the SQL Queries.
				Statement statement = conn.createStatement();
				// Select 20 rows from the CUSTOMERS table from SH schema.
				ResultSet resultSet = statement.executeQuery(queryStatement);

			System.out.println("-----------------------------------------------------------");
			System.out.println("\nCongratulations! You have successfully used Oracle ADB\n");
			//System.out.println(resultSet);
	    /*    while (resultSet.next()) {
	            System.out.println(" " + resultSet.getString(2) );
	          }*/
			return resultSet;
	
		
	} // End of doSQLWork

	
	public TableView<?> toTableView(ResultSet queryResult) throws SQLException {
		  var table = new TableView<Object[]>();

		  var metadata = queryResult.getMetaData();
		  int columnCount = metadata.getColumnCount();
		  System.out.println("columncount is :"+columnCount);
		  for (int i = 1; i <= columnCount; i++) {
			  
		    final int idx = i;
		    var column = new TableColumn<Object[], Object>(metadata.getColumnLabel(idx));
		    column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()[idx - 1]));
		    table.getColumns().add(column);
		    System.out.println("column is :"+column);
		   
		  }

		  var items = table.getItems();
		  while (queryResult.next()) {
		    var row = new Object[columnCount];
		    for (int i = 1; i <= columnCount; i++) {
		      row[i - 1] = queryResult.getObject(i);
		    }
		    items.add(row);
		    System.out.println("row is :"+row);
		  }

		  return table;
		}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DBUtil dbUtil = new DBUtil();
		//dbUtil.listAllTableNames();
		ResultSet resultSet=dbUtil.doQuery("select * from WF_LANGUAGES");
		System.out.println("----------------- one more again ----------------------------:");
		dbUtil.toTableView(resultSet);

		// Wallet_ZOOQDB2B4QOA2K79

	}

	/*
	 * public void createTable() throws SQLException {
	 * 
	 * try (Connection conn = pds.getConnection()) {
	 * System.out.println("Available connections after checkout: " +
	 * pds.getAvailableConnectionsCount());
	 * System.out.println("Borrowed connections after checkout: " +
	 * pds.getBorrowedConnectionsCount()); // Perform a database operation
	 * doSQLWork(conn); } catch (SQLException e) {
	 * System.out.println("ADBQuickStart - " +
	 * "doSQLWork()- SQLException occurred : " + e.getMessage()); } String
	 * createString = "create table COFFEES " + "(COF_NAME varchar(32) NOT NULL, " +
	 * "SUP_ID int NOT NULL, " + "PRICE numeric(10,2) NOT NULL, " +
	 * "SALES integer NOT NULL, " + "TOTAL integer NOT NULL, " +
	 * "PRIMARY KEY (COF_NAME), " +
	 * "FOREIGN KEY (SUP_ID) REFERENCES SUPPLIERS (SUP_ID))"; try (Statement stmt =
	 * conn.createStatement()) { stmt.executeUpdate(createString); } catch
	 * (SQLException e) { JDBCTutorialUtilities.printSQLException(e); } }
	 * 
	 * 
	 * public void deleteAllTables(String dbUserName) {
	 * 
	 * /*BEGIN FOR TNAME IN (SELECT table_name FROM user_tables) LOOP EXECUTE
	 * IMMEDIATE ('DROP TABLE ' || TNAME.table_name || ' CASCADE CONSTRAINTS
	 * PURGE'); END LOOP; END;
	 * 
	 * 
	 * 
	 * }
	 */

}
