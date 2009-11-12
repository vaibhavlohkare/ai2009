package friends.crawler;

import java.util.LinkedList;

import myGoogle.Util.BerkeleyDBAccess;

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;

import com.sleepycat.je.DatabaseException;



public class LinkExtractor 
{
	//private int crawlerID;
	private BerkeleyDBAccess<Long, LinkedList<String>> docToLinksMappingDb;
	public LinkExtractor(int crawlerID) throws Exception
	{
		//this.crawlerID = crawlerID;
		docToLinksMappingDb= new BerkeleyDBAccess<Long, LinkedList<String>>(Long.class,LinkedList.class);
		docToLinksMappingDb.createEnv("Data/LinkMapping"+crawlerID);
		docToLinksMappingDb.openDB("LinkMapping", false);
	}
	
	public  void Process(long docID,FetchedDoc fetchedDoc) throws Exception
	{
		LinkedList<String> links = new LinkedList<String>();
		Page page = new Page(fetchedDoc.memoryStream,fetchedDoc.encoding);
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
						String absUrl = AbsUrlConstructor.construct(fetchedDoc.urlString, href);
						Crawler.urlFilter.Process(absUrl);
						links.add(absUrl);
					}
				}
			}
		}
	//	if(links.size()>0)
			docToLinksMappingDb.add(docID, links);
		fetchedDoc.memoryStream.close();
	}
	public void close() throws DatabaseException 
	{
		this.docToLinksMappingDb.closeDatabase();
		this.docToLinksMappingDb.closeEnv();
	}

}
