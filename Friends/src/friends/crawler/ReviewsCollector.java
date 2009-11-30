package friends.crawler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;


public class ReviewsCollector {
	
	public boolean process(String docUrl, Page page)
	{
		Pattern p = Pattern.compile("^(http://www.yelp.com/user_details\\?)(\\S)*");
		if (p.matcher(docUrl).matches()) 
		{
			String user  = extractUserID(docUrl);	
			try
			{
				Hashtable<String,Integer> businessRating = extractData(docUrl,page);
				UpdateDataBase(user, businessRating);
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	private Hashtable<String,Integer> extractData(String docUrl, Page page) throws Exception
	{
		Hashtable<String,Integer> businessTable = new Hashtable<String,Integer>();
		String currentBusiness = "";
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
					if (href != null) 
					{
						String absUrl = AbsUrlConstructor.construct(docUrl, href);
						
						Crawler.dispatchUrl(absUrl);
						
						Pattern pBusiness = Pattern	.compile("^(http://www.yelp.com/biz/)(\\S)+");
						if (pBusiness.matcher(absUrl).matches()) 
						{
							currentBusiness = extractBusinessName(href);
							if (!businessTable.containsKey(currentBusiness))
							{
								businessTable.put(currentBusiness, -1);
							}
//							System.out.println("currentBusiness = "+currentBusiness);
							//rating = "4";
							//UpdateDatabase(linkID, business, rating, userID);
							//System.out.println(business + " added.");
						}
					}
				}
				
				else if (tagNode.getTagName().equals("IMG")) 
				{
					String c1ass2 = tagNode.getAttribute("class");
					if (c1ass2 != null) {
						String rating = "";
						
						String[] rate = c1ass2.split("_");
						int num = rate.length - 1;
						if (!rate[num].equals("loader"))
						{
							rating = rate[num].trim();
							if (businessTable.get(currentBusiness) == -1){
								businessTable.put(currentBusiness, Integer.parseInt(rating));
							}
						}
						//System.out.println(linkID + " " + business + " " + rating + " " + userID;

						
					}	 
				}
			}
		}
		
		return businessTable;
	}	
	
	/*
	 * extract user id from url link
	 */
	private String extractUserID(String url)
	{
		String[] currentLinkage = url.split("=");
		return currentLinkage[1];
	}
	
	private String extractBusinessName(String url)
	{
		String[] split = url.split("-");
		String ret = "";
		for (int i = 0; i < split.length; i++) 
		{
			if (i == 0) {
				String[] s2 = split[i].split("/");
				int num = s2.length - 1;
				ret += s2[num];
			} else if (split[i].contains("#")) {
				break;
			} else
				ret += " " + split[i];
		}
		return ret;
	}
	
	private void UpdateDataBase(String user, Hashtable<String,Integer> businessRating)
	{
		System.out.println("Current user is " + user);
		
		Enumeration e = businessRating.keys();
		while (e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			int value = businessRating.get(key);
			System.out.printf("@@@ %s = %s\n", key, value);
		}
	}
	
}
