package algorithm;
//package mysqldb;

import java.sql.*;
import java.text.*;
import java.util.*;
import javax.naming.*;
import javax.sql.*;

public class GetConnection {

	public static Connection getSimpleConnection() 
	{
		Connection result = null;
		try 
		{
			Class.forName(fDriverName).newInstance();
		}
		catch (Exception ex)
		{
			System.err.println("Check classpath. Cannot load db driver: " + fDriverName);
		}
		try 
		{
			result = DriverManager.getConnection(fDbName, fUserName, fPassword);
		}
		catch (SQLException e)
		{
			System.err.println( "Driver loaded, but cannot connect to db: " + fDbName);
		}
		return result;
	}



public static void main(String [] args){ 
	System.out.println("entering main");
	Connection conn=null;;
	Statement dbSt=null;
	ResultSet dbRs=null;
	try{
         conn = GetConnection.getSimpleConnection();
         dbSt = conn.createStatement();
         String sql = "select * from lookup";
         dbRs = dbSt.executeQuery(sql);

         System.out.println("result set "+ dbRs);
	 if(dbRs.first() == true){
           System.out.println("result from db "+ dbRs.getInt(dbRs.findColumn("id")));
	 }
	}
	catch(Exception ex){
	  ex.printStackTrace();
	}
	finally{
	System.out.println("leaving main");
		try{
	  conn.close();
	  dbSt.close();
	  dbRs.close();
		}
		catch(Exception ex){
	            ex.printStackTrace();
		}
	}
}

// PRIVATE //

/**
* Provided by driver documentation. In this case, a MySql driver is
* used.
*/

private static final String fDriverName = "org.gjt.mm.mysql.Driver";

/**
* See driver documentation for the proper format of this string.
*/
private static final String fDbName = "jdbc:mysql://localhost/ai2009";
private static final String fUserName = "root";
private static final String fPassword = "10ZM!)zm";

private static final String fDATASOURCE_CONTEXT = "java:comp/env/jdbc/blah";
} 
