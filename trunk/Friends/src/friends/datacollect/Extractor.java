package friends.datacollect;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import friends.crawler.CrawlerWorker;
import friends.crawler.FetchedDoc;
import friends.crawler.StreamFetcher;
import friends.database.*;
import friends.datacollect.LinkExtractor;

public class Extractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(
				"testdbproj", System.getProperties());
		EntityManager em = factory.createEntityManager();
		
		FriendsExtractor fe1 = new FriendsExtractor();
		em.getTransaction().begin();
		Query q = em.createQuery("SELECT f.friendID from Friends f");
		List<String> friendsList = q.getResultList();
		em.getTransaction().commit();
		em.close();
		
		if (friendsList.size() == 0)
		{
			StreamFetcher sf = new StreamFetcher(new CrawlerWorker());
			FetchedDoc doc = sf.Fetch("http://www.yelp.com/user_details?userid=oXHd9HUOGd9Lk_3nUNDYVA");
			System.out.println(doc);
			
			LinkExtractor le = new LinkExtractor(1);
			le.Process(2L, doc);
			
			FriendsExtractor fe = new FriendsExtractor();
			fe.Process();
				
			ReviewsExtractor re = new ReviewsExtractor();
			re.Process();
		}
		
		for (int x=0; x<friendsList.size(); x++)
		{
			try{
			StreamFetcher sf = new StreamFetcher(new CrawlerWorker());
			String dbLink = "http://www.yelp.com/user_details?userid="+friendsList.get(x);
			FetchedDoc doc = sf.Fetch(dbLink);
			
			LinkExtractor le = new LinkExtractor(1);
			le.Process(2L, doc);
			
			FriendsExtractor fe = new FriendsExtractor();
			fe.Process();
				
			ReviewsExtractor re = new ReviewsExtractor();
			re.Process();
			}
			catch (NullPointerException ne)
			{
				System.out.println("NullPointer");
			}
		}
		
	}

}
