

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import java.util.Iterator;

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
			ex.printStackTrace();
		}
		finally{
			//try{

				  //dbRs.close();
			//}
			//catch(Exception ex){
			 //     ex.printStackTrace();
			//}
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
		//ResultSet set = getReviewData(userName);
		ResultSet competingReview = null;
		int currentRating= 0;
		String reviewId=null;
		//try
		//{
			//while (set.next()) {
			//	try
			//	{
					// retrieve the values for the current row
			//		 reviewId = set.getString("business");
			//		 currentRating = set.getInt("rating");
			//		 System.out.println("business and review "+reviewId+ " "+currentRating);
			//	}
			//	catch(Exception ex)
			//	{
			//		ex.printStackTrace();
			//	}
				//now with the review id obtained find all the users 
				//who commented on the same review and their rating
				//String sqlQuery = "select * from reviews_table where business='"+reviewId+"'";
			      String sqlQuery = "select * from reviews_table where business in (select business from reviews_table where username='"+userName+"')";
				
				try
				{
					competingReview = dbSt.executeQuery(sqlQuery);
					
					//now what we have to do is that we need to search for
					//the users who have made comments on the same review
					//compare it current user of interest
					while( competingReview.next())
					{
						String competingUserName = competingReview.getString("username");
						
						if(!competingUserName.equals(userName))
						{
							System.out.println("competing user name "+competingUserName);
							int competingRating = competingReview.getInt("rating");
							double dRating = convertRatingToFloat(competingRating);
							String user1User2 = competingUserName+":"+userName;
							if(userListRating.containsKey(user1User2))
							{
								double previousRating = userListRating.get(user1User2);
								previousRating += dRating;
								userListRating.put(user1User2, previousRating);
								int count = userListCount.get(user1User2);
								count += 1;
								userListCount.put(user1User2, count);
							}
							else
							{
								userListRating.put(user1User2,dRating);
								userListCount.put(user1User2,1);
							}
						}
					}
				}
				catch(Exception ex)
				{
					System.out.println("Failed to execute the competing review query");
					ex.printStackTrace();
				}
				
			//}
		//}
		//catch(Exception ex)
		//{
			//ex.printStackTrace();
		//}
		finally
		{
			try
			{
			//set.close();
			competingReview.close();
			}
			catch(Exception ex){}
		}
		
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
		String sqlQuery = "select * from user_friend_table";
		ResultSet businessReviews = null;
		boolean doOnce = false;
		try
		{
			businessReviews = dbSt.executeQuery(sqlQuery);
			while(businessReviews.next())
			{
				
				String friendName = businessReviews.getString("friendName");
				String userName = businessReviews.getString("userName");
				if(!users.contains(userName))
				{
					users.add(userName);
					//getCompanionReviews(userName);
				}
				//each friend is sepearted by a colon
				
				if(friendMap.containsKey(userName))
				{
					Vector<String> friendList = friendMap.get(userName);
					friendList.add(friendName);
				}
				else
				{
					Vector<String> friendList = new Vector<String>();
					friendMap.put(userName,friendList);
				}
				
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		for(int i=0; i < users.size(); ++i)
		{
			String userName = users.get(i);
			getCompanionReviews(userName);
			Iterator it = (Iterator) userListRating.keySet().iterator();
			while(it.hasNext()) {	
				String key = (String)it.next();
				if(key.startsWith(userName))
				{
					double rating = userListRating.get(key);
					double count = (double)userListCount.get(key);
					double connection = 1- (rating / count);
					System.out.println("conection between user1 "+userName+" user2 "+getSecondUser(key)+ " connection "+connection);
					//right now I am saying 80% chance we can tweak this number
					if( connection > 0.8 )  
					{
					   System.out.println("User is more likely to be friends with "+getSecondUser(key));
					   Vector friendList = friendMap.get(userName);
					   if (friendList.contains(getSecondUser(key)))
					   {
						  System.out.println("User is already friend with recommended user. !!"); 
					   }
					}
			    }
			}
		}
			
	}
	
	public String getSecondUser(String user1User2)
	{
		int index = user1User2.indexOf(':');
		String user2 = user1User2.substring(index+1,user1User2.length());
		return user2;
	}
	
	 public static void main(String [] args)
	 {
	         ExploreReviews review = new ExploreReviews();
	         review.runRecommendationAlgorithm();	         
	 }
	
	HashMap<String,Double> userListRating = new HashMap<String,Double>();
	HashMap<String,Integer> userListCount = new HashMap<String,Integer>();
    Vector<String> users = new Vector<String>();
    HashMap<String,Vector<String>> friendMap = new HashMap<String,Vector<String>>();
    
   
}
