package friends.crawler;

import java.util.LinkedList;
import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;

import com.sleepycat.je.DatabaseException;
import friends.util.BerkeleyDBAccess;



public class LinkExtractor 
{
	ReviewsCollector reviews = new ReviewsCollector();
	FriendshipCollector friends = new FriendshipCollector();
	
	//private int crawlerID;
	private BerkeleyDBAccess<Long, LinkedList<String>> docToLinksMappingDb;
	public LinkExtractor(int crawlerID) throws Exception
	{
		//this.crawlerID = crawlerID;
		docToLinksMappingDb= new BerkeleyDBAccess<Long, LinkedList<String>>(Long.class,LinkedList.class);
		docToLinksMappingDb.createEnv("Data/LinkMapping"+crawlerID);
		docToLinksMappingDb.openDB("LinkMapping", false);
	}
	
	public  void Process(/*long docID,*/FetchedDoc fetchedDoc) throws Exception
	{
		System.out.println("Page is processed: " + fetchedDoc);
//		LinkedList<String> links = new LinkedList<String>();
		String docUrl = fetchedDoc.urlString;
		Page page = new Page(fetchedDoc.memoryStream,fetchedDoc.encoding);
		
		try{
			boolean isReviews = reviews.process(docUrl,page);
			boolean isFriends = friends.process(docUrl, page);

			if (!isReviews && !isFriends)
			{
				extractLinks(docUrl, page);
			}	
		}
		catch (Exception e){
			e.printStackTrace();
		}

	//	if(links.size()>0)
			//docToLinksMappingDb.add(docID, links);
		fetchedDoc.memoryStream.close();
	}
	
	private void extractLinks(String docUrl, Page page) throws Exception 
	{
		Lexer lexer = new Lexer(page);
		while(true)
		{
			Node node = lexer.nextNode();
			if(node == null)
			{
				break;
			}
			if(node instanceof TagNode)
			{
				TagNode tagNode = (TagNode)node;
				if(tagNode.getTagName().equals("A"))
				{
					String href = tagNode.getAttribute("href");
					if(href != null)
					{
						String absUrl = AbsUrlConstructor.construct(docUrl, href);
						Crawler.dispatchUrl(absUrl);
					}
				}
			}
		}
	}
	public void close() throws DatabaseException 
	{
		this.docToLinksMappingDb.closeDatabase();
		this.docToLinksMappingDb.closeEnv();
	}

}
