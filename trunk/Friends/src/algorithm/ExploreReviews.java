package algorithm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

public class ExploreReviews {
	
	private Connection conn=null;;
	private Statement dbSt=null;
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
	
	//for each of the review made by the user
	//find all the other user who have commented on it
	//and then extract the rating made by the user 
	//and also the rating made by all the other
	//users 
	
	private ResultSet getReviewsInCommon()throws SQLException
	{
		String sqlQuery = "SELECT R1.userID AS user1, R2.userID AS user2, R1.reviews, R1.reviewRatings as rate1, R2.reviewRatings AS rate2 FROM reviews R1 JOIN reviews R2 ON R1.reviews=R2.reviews WHERE NOT R1.reviews='' AND NOT R2.reviews='' AND NOT R1.reviewRatings='' AND NOT R2.reviewRatings='' AND NOT R1.userID=R2.userID;";
		return dbSt.executeQuery(sqlQuery);
	}
	
	private double convertRatingToFloat( int rating)
	{
		switch ( rating)
		{
		case 1:
			return 0.0;
		case 2: 
			return 0.25;
		case 3:
			return 0.50;
		case 4:
			return 0.75;
		case 5:
			return 1.00;
		}
		return -1.0;
	    	
	}
	
	public void runRecommendationAlgorithm()
	{
		String sqlQuery = "select * from friendship";
		ResultSet businessReviews = null;
		boolean doOnce = false;
		try
		{
			businessReviews = dbSt.executeQuery(sqlQuery);
			while(businessReviews.next())
			{
				
				String friendName = businessReviews.getString("friendid");
				String userName = businessReviews.getString("userid");
				
				
				if(friendMap.containsKey(userName))
				{
					Vector<String> friendList = friendMap.get(userName);
					friendList.add(friendName);
				}
				else
				{
					Vector<String> friendList = new Vector<String>();
					friendList.add(friendName);
					friendMap.put(userName,friendList);
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		try
		{
			ResultSet ReviewsInCommonReviews = getReviewsInCommon();
			UserData user = null;
			while(ReviewsInCommonReviews.next()) {
				try
				{
					user = new UserData(ReviewsInCommonReviews);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				addSubtraction(user.getkey(), user.getSubtraction(), subtractionMap);
				addUserProfile(user.getkey(), user, useProfiles);
			}
			
			Iterator iterator = subtractionMap.keySet().iterator();
			while( iterator. hasNext() )
			{
				String userKey = (String)iterator.next();
				String userInfo = getUserInfo(userKey, useProfiles);
				UserData userInf = useProfiles.get(userKey);
				double weight = CalculateUsersWeight(userKey,subtractionMap);
				System.out.printf("%s has the weight %f\n", userInfo, weight);
				if(weight > 0.5)
				{
					Vector<String> friendList = friendMap.get(userInf.user1);
					if(friendList.contains(userInf.user2))
					{
						System.out.println("User is already friend with recommended user. !!");
					}
					Vector<String> friendList1 = friendMap.get(userInf.user2);
					if(friendList.contains(userInf.user1))
					{
						System.out.println("User is already friend with recommended user. !!");
					}
				}
			} 
		
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private String getUserInfo(String user, HashMap<String,UserData> useprofile)
	{
		if (useprofile.containsKey(user))
		{
			UserData data = useprofile.get(user);
			return data.toString();
		}
		else
			return "none";
	}
	
	private double CalculateUsersWeight(String user, HashMap<String,Vector<Double>> subtractMap)
	{
		if (subtractMap.containsKey(user))
		{
			Vector<Double> substracts = subtractMap.get(user);
			int size = substracts.size();
			double sum = 0.0;
			for (int i = 0; i < size; i++)
			{
				sum += substracts.get(i);
			}
			return (1-sum/size);
		}
		else
			return 0.0;
	}
	
	private void addUserProfile(String user, UserData data, HashMap<String,UserData> useprofile)
	{
		if (!useprofile.containsKey(user))
		{
			useprofile.put(user, data);
		}
	}
	
	private void addSubtraction(String user, double subtract, 
			HashMap<String,Vector<Double>> subtractMap)
	{
		if (subtractMap.containsKey(user))
		{
			Vector<Double> stracts = subtractMap.get(user);
			stracts.add(subtract);
		}
		else
		{
			Vector<Double> stracts = new Vector<Double>();
			stracts.add(subtract);
			subtractMap.put(user, stracts);
		}
	}
	
	public static void main(String [] args)
	{
		ExploreReviews review = new ExploreReviews();
		review.runRecommendationAlgorithm();	         
	}
	
	HashMap<String,Vector<Double>> 	subtractionMap = new HashMap<String,Vector<Double>>(); 
	HashMap<String,UserData> useProfiles = new HashMap<String,UserData>();
	
    private class UserData
    {
    	String user1 = null;
    	String user2 = null;
    	String business = null;
    	int rate1 = 0;
    	int rate2 = 0;
    	
    	public UserData(ResultSet oneRow) throws SQLException
    	{
			user1 = oneRow.getString("user1");
			user2 = oneRow.getString("user2");
			business = oneRow.getString("reviews");
			rate1 = oneRow.getInt("rate1");
			rate2 = oneRow.getInt("rate2");
    	}
    	
    	public boolean equalto(UserData other)
    	{
    		if (this.user1 == other.user1 
    				&& this.user2 == other.user2 
    				&& this.business == other.business)
    			return true;
    		if (this.user1 == other.user2 
    				&& this.user2 == other.user1 
    				&& this.business == other.business)
    			return true;
    		return false;
    	}
    	
    	public String getkey()
    	{
    		return this.user1+this.user2;
    	}
    	
    	public double getSubtraction()
    	{
    		return Math.abs(convertRatingToFloat(rate1) - convertRatingToFloat(rate2));
    	}
    	
    	public String toString()
    	{
    		return "[user1="+user1+", user2="+user2+"]";
    	}
    	
    }
    private HashMap<String,Vector<String>> friendMap = new HashMap<String,Vector<String>>();
}
