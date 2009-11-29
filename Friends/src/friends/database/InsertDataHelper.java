package friends.database;

import java.sql.Connection;
import java.sql.Statement;

import algorithm.GetConnection;

public class InsertDataHelper {
	
	//This class is mainly used for filling out two 
	//database tables namely reviews_table and the user_friend_table
	private int review_unique_id=0;
	private int userfriend_unique_id=0;
	private Connection conn=null;;
	private Statement dbSt=null;
	public InsertDataHelper()
	{
		try{
	
			conn = GetConnection.getSimpleConnection();
	        dbSt = conn.createStatement();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void insertReviewData(int rating, String userName, String businessName)
	{
		try{
			dbSt.executeUpdate("INSERT INTO reviews_table " + "VALUES ("+review_unique_id+",'"+userName+"','"+businessName+"',"+rating+")");
			review_unique_id++;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public void insertUserFriendData(String userName,String friendName)
	{
		try
		{
			dbSt.executeUpdate("INSERT INTO user_friend_table " + "VALUES ( "+"'"+ userName +"',"+"'" + friendName + "')");
			userfriend_unique_id++;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
