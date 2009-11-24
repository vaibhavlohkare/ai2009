package algorithm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class ExploreReviews {
	
	private Connection conn=null;;
	private Statement dbSt=null;
	//private ResultSet dbRs=null;
	public ExploreReviews()
	{
		try{
	         conn = GetConnection.getSimpleConnection();
	         dbSt = conn.createStatement();
	         
	         
		}
		catch(Exception ex){
			  ex.printStackTrace();
		}
	}
	
	public ResultSet getReviewData(String userName)
	{
		String sqlQuery = "select * from reviews_table where username='"+userName+"'";
		ResultSet dbRs = null;
		try
		{
			dbRs = dbSt.executeQuery(sqlQuery);
		}
		catch(Exception ex)
		{
			
		}
		finally{
			try{

				  dbRs.close();
			}
			catch(Exception ex){
			      ex.printStackTrace();
			}
		}
		return dbRs;
	}
	
	//for each of the review made by the user
	//find all the other user who have commented on it
	//and then extract the rating made by the user 
	//and also the rating made by all the other
	//users 
	public void getCompanionReviews(String userName)
	{
		ResultSet set = getReviewData(userName);
		
		
	}

}
