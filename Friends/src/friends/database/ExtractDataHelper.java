package friends.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class ExtractDataHelper {
	
	// Sets up the entity that accesses the database.
	// Runs on 'localhost'
	EntityManagerFactory factory = Persistence.createEntityManagerFactory(
			"testdbproj", System.getProperties());
	EntityManager em = factory.createEntityManager();
	
	public ExtractDataHelper()
	{
		
	}
	
	// Queries the database to get all the friends objects and returns 
	// a list of them.
	public List GetFriends()
	{
		List<String> friendsList;
		Query q = em.createQuery("SELECT f FROM Friends f");
		friendsList = q.getResultList();
		return friendsList;
	}
	// Queries the database to get all the reviews objects and returns 
	// a list of them. Includes the name of the business and the rating.
	public List GetReviews()
	{
		List<String> reviewsList;
		Query q = em.createQuery("SELECT r FROM Reviews r");
		reviewsList = q.getResultList();
		return reviewsList;
	}

}
