package friends.datacollect;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import friends.database.Link;
import friends.database.Review;

public class ReviewsExtractor {

	public Map<Long, LinkedList<String>> docToReviewsMappingDb;

	public ReviewsExtractor() throws Exception {
		docToReviewsMappingDb = new HashMap<Long, LinkedList<String>>();
	}

	public void Process() throws Exception {
		// LinkedList<String> links = new LinkedList<String>();

		List<Link> links = GetDatabaseLinks();

		for (int i = 0; i < links.size(); i++) {
			String dbLink = links.get(i).getLink();
			StreamFetcher sf = new StreamFetcher(new CrawlerWorker());
			FetchedDoc doc = sf.Fetch(dbLink);
			ExtractReviews(doc, links.get(i).getLinkID());
		}
	}

	public List GetDatabaseLinks() {

		Link l = new Link();

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

	public void ExtractReviews(FetchedDoc fetchedDoc, int linkID)
			throws Exception {
		LinkedList<String> links = new LinkedList<String>();

		Page page = new Page(fetchedDoc.getMemoryStream(), fetchedDoc.getEncoding());
		Lexer lexer = new Lexer(page);
		Pattern p = Pattern
				.compile("^(http://www.yelp.com/user_details_reviews_self\\?)(\\S)*");

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

		for (int x = 0; x < links.size(); x++) {
			String currentLink = links.get(x);
			String[] currentLinkage = links.get(x).split("=");
			String userID = currentLinkage[1];
			StreamFetcher sf2 = new StreamFetcher(new CrawlerWorker());
			FetchedDoc doc2 = sf2.Fetch(currentLink);
			Page p2 = new Page(doc2.getMemoryStream(), doc2.getEncoding());
			Lexer lexer2 = new Lexer(p2);
			String business = "";
			while (true) {
				Node node = lexer.nextNode();
				if (node == null) {
					break;
				}
				if (node instanceof TagNode) {
					String tempBusiness = "";
					String rating = "";
					TagNode tagNode = (TagNode) node;
					if (tagNode.getTagName().equals("A")) {
						String c1ass = tagNode.getAttribute("href");
						if (c1ass != null) {
							String absUrl = AbsUrlConstructor.construct(
									fetchedDoc.getUrlString(), c1ass);
							Crawler.urlFilter.Process(absUrl);
							Pattern p3 = Pattern
									.compile("^(http://www.yelp.com/biz/)(\\S)+");
							if (p3.matcher(absUrl).matches()) {
								String[] split;
								split = c1ass.split("-");
								for (int i = 0; i < split.length; i++) {

									if (i == 0) {
										String[] s2 = split[i].split("/");
										int num = s2.length - 1;
										tempBusiness += s2[num];
									} else if (split[i].contains("#")) {
										break;
									} else
										tempBusiness += " " + split[i];

									business = tempBusiness;
								}
								//rating = "4";
								//UpdateDatabase(linkID, business, rating, userID);
								//System.out.println(business + " added.");
							}
						}

					} else if (tagNode.getTagName().equals("IMG")) {
						String c1ass2 = tagNode.getAttribute("class");
						if (c1ass2 != null) {
							String[] rate = c1ass2.split("_");
							int num = rate.length - 1;
							if (!rate[num].equals("loader"))
								rating = rate[num];
							//System.out.println(linkID + " " + business + " " + rating + " " + userID;
							UpdateDatabase(linkID,business,rating,userID);
						}
						 
					}
				}
			}
		}
	}

	public void UpdateDatabase(int linkID, String business, String rating,
			String userID) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(
				"testdbproj", System.getProperties());
		EntityManager em = factory.createEntityManager();

		Review r = new Review();
		r.setReviews(business);
		r.setReviewRatings(rating);
		r.setUserID(userID);
		r.setLinkID(linkID);

		try {
				em.getTransaction().begin();
				em.persist(r);
				em.getTransaction().commit();

		} catch (Exception e) {
			if (e instanceof MySQLIntegrityConstraintViolationException) {
				em.getTransaction().rollback();
			}
		}

		em.close();
	}
}
