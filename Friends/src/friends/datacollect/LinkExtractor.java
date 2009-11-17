package friends.datacollect;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//import myGoogle.Util.BerkeleyDBAccess;
import java.lang.Object;

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;

import friends.crawler.*;
//import com.sleepycat.je.DatabaseException;




public class LinkExtractor 
{
	//private int crawlerID;
	public Map<Long, LinkedList<String>> docToLinksMappingDb;
	
	public LinkExtractor(int crawlerID) throws Exception
	{
		//this.crawlerID = crawlerID;
		docToLinksMappingDb = new HashMap<Long, LinkedList<String>>();
		//TODO(alexis): Fill in database info
		//docToLinksMappingDb.createEnv("Data/LinkMapping"+crawlerID);
		//docToLinksMappingDb.openDB("LinkMapping", false);
	}
	
	public  void Process(long docID, FetchedDoc fetchedDoc) throws Exception
	{
		LinkedList<String> links = new LinkedList<String>();
		
		Page page = new Page(fetchedDoc.getMemoryStream(),fetchedDoc.getEncoding());
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
				
						String absUrl = AbsUrlConstructor.construct(fetchedDoc.getUrlString(), href);
						Crawler.urlFilter.Process(absUrl);
						links.add(absUrl);
					}
				}
				
					// <span class="highlight2"></span>
					// Location, Things I Love (links), My Hometown, The Last Great Book I Read, My First Concert, My Favorite Movie
				
			}
			// Include other attributes that the algorithm will be analyzing
			// May include: Hometown, Current Location, Interests, Favorite Movie, etc.
			
			
		}
	//	if(links.size()>0)
		docToLinksMappingDb.put(docID, links);
		fetchedDoc.getMemoryStream().close();
	}
	public void close() //throws DatabaseException 
	{
		// this.docToLinksMappingDb.closeDatabase();
		//this.docToLinksMappingDb.closeEnv();
	}

}
