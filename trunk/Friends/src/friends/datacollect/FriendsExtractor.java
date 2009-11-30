package friends.datacollect;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import friends.crawler.AbsUrlConstructor;
import friends.crawler.Crawler;
import friends.crawler.CrawlerWorker;
import friends.crawler.FetchedDoc;
import friends.crawler.StreamFetcher;
import friends.database.Friends;
import friends.database.Link;

public class FriendsExtractor {

	public FriendsExtractor() throws Exception
	{
		
	}
	
	public void Process() throws Exception {

		List<Link> links = GetDatabaseLinks();

		for (int i = 0; i < links.size()-1; i++) {
			String dbLink = links.get(i).getLink();
			StreamFetcher sf = new StreamFetcher(new CrawlerWorker());
			FetchedDoc doc = sf.Fetch(dbLink);
			List<String> userFriendsList = ExtractFriends(doc);
			
			FetchedDoc newF = CrawlFriends(userFriendsList);
			ExtractFriendships(dbLink, newF);
			
		}
	}
	
	public List GetDatabaseLinks() {

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(
				"testdbproj", System.getProperties());
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Query q = em.createQuery("SELECT l FROM Link l");
		List<Link> dbLinks = q.getResultList();
		em.getTransaction().commit();
		em.close();

		return dbLinks;
	}

	public List ExtractFriends(FetchedDoc fetchedDoc) throws Exception
	{
		LinkedList<String> links = new LinkedList<String>();

		Page page = new Page(fetchedDoc.getMemoryStream(), fetchedDoc.getEncoding());
		Lexer lexer = new Lexer(page);
		Pattern p = Pattern
				.compile("^(http://www.yelp.com/user_details_friends\\?)(\\S)*");
		
		while (true) {

			Node node = lexer.nextNode();
			if (node == null) {
				break;
			}
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;

				if (tagNode.getTagName().equals("A")) {
					String href = tagNode.getAttribute("href");
					if (href != null) {
						String absUrl = AbsUrlConstructor.construct(fetchedDoc
								.getUrlString(), href);
						Crawler.urlFilter.Process(absUrl);
						if (p.matcher(absUrl).matches()) {
							links.add(absUrl);
							break;
						}
					}
				}
			}
		}

		return links;

	}
	
	public void ExtractFriendships(String link, FetchedDoc fetchedDoc) throws Exception
	{
	
		LinkedList<String> links = new LinkedList<String>();

		Page page2 = new Page(fetchedDoc.getMemoryStream(), fetchedDoc.getEncoding());
		Lexer lexer2 = new Lexer(page2);
		
		Pattern p2 = Pattern
				.compile("^(http://www.yelp.com/user_details\\?)(\\S)*");
		
		while (true) {

			Node node = lexer2.nextNode();
			if (node == null) {
				break;
			}
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;

				if (tagNode.getTagName().equals("A")) {
					String href = tagNode.getAttribute("href");
					if (href != null) {
						String absUrl = AbsUrlConstructor.construct(fetchedDoc.getUrlString(), href);
						Crawler.urlFilter.Process(absUrl);
						if (p2.matcher(absUrl).matches()) {
							links.add(absUrl);
						}
					}
				}
			}
		}
		
		String[] uID = link.split("=");
		String userID = uID[1];
		
		for (int x = 0; x < links.size(); x++) {
			String currentLink = links.get(x);
			String[] currentLinkage = links.get(x).split("=");
			String friendID = currentLinkage[1];
			
			UpdateDatabase(userID, friendID);
		}
	}
	
	public FetchedDoc CrawlFriends(List<String> userFriendsList)
	{
		String userFriend = userFriendsList.get(0);
		
		StreamFetcher sf2 = new StreamFetcher(new CrawlerWorker());
		FetchedDoc f2 = sf2.Fetch(userFriend);
		
		return f2;
		
	}
	
	public void UpdateDatabase(String userLink, String friendID)
	{
		
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(
				"testdbproj", System.getProperties());
		EntityManager em = factory.createEntityManager();
		
		Friends f = new Friends();
		
		if (!userLink.matches(friendID) && userLink != null && friendID != null)
		{
			f.setUserID(userLink);
			f.setFriendID(friendID);
			try {
				
				em.getTransaction().begin();
				em.persist(f);
				em.getTransaction().commit();

			} catch (Exception e) {
			if (e instanceof MySQLIntegrityConstraintViolationException) {
				em.getTransaction().rollback();
				System.out.println("error.");
			}
			}
		}
		
		
		em.close();
	}
}

