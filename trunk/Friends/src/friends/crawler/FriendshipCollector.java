package friends.crawler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TagNode;


public class FriendshipCollector {
	
	public boolean process(String docUrl, Page page)
	{
		Pattern p = Pattern.compile("^(http://www.yelp.com/user_details_friends\\?)(\\S)*");
		if (p.matcher(docUrl).matches()) 
		{
			String user  = extractUserID(docUrl);	
			try
			{
				Hashtable<String,String> friends = extractData(docUrl,page);
				UpdateDataBase(user, friends);
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
	
	/*
	 * return hashtable (key: unique id, value: true name)
	 */
	private Hashtable<String,String> extractData(String docUrl, Page page) throws Exception
	{
		Hashtable<String,String> friends = new Hashtable<String,String>();
		Lexer lexer2 = new Lexer(page);
		
		Pattern p2 = Pattern.compile("^(http://www.yelp.com/user_details\\?)(\\S)*");
		
		int occurrence = 0;
		boolean checkNextNode = false;
		String currentFriendId = "";
		while (true) 
		{

			Node node = lexer2.nextNode();
			if (node == null) {
				break;
			}
			
			if (checkNextNode)
			{
				String trueName = (node != null)? node.getText():"";
				friends.put(currentFriendId, trueName);
				
				currentFriendId = "";
				checkNextNode = false;
				continue;
			}
			
			if (node instanceof TagNode) {
				TagNode tagNode = (TagNode) node;

				if (tagNode.getTagName().equals("A")) {
					String href = tagNode.getAttribute("href");
					if (href != null) {
						String absUrl = AbsUrlConstructor.construct(docUrl, href);
						
						Crawler.dispatchUrl(absUrl);
						
						if (p2.matcher(absUrl).matches()) {
							//links.add(absUrl);
							occurrence++;
							
							if (occurrence == 1)
							{
								String[] currentLinkage = absUrl.split("=");
								//String trueName = getTagValue(tagNode);
								currentFriendId = currentLinkage[1];
								checkNextNode = true;
								occurrence = 0;
								//System.out.printf("%s ++ %s\n", currentLinkage[1], trueName);
							}
						}
					}
				}
			}
		}
		
		return friends;
		
	}
	
	private String extractUserID(String url)
	{
		String[] currentLinkage = url.split("=");
		return currentLinkage[1];
	}
	
	private void UpdateDataBase(String user, Hashtable<String,String> friends)
	{
		System.out.println("Current user is " + user);
		
		Enumeration e = friends.keys();
		while (e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			String trueName = friends.get(key);
			System.out.printf("@@@ %s -- %s\n", key, trueName);
		}
	}
	
}
