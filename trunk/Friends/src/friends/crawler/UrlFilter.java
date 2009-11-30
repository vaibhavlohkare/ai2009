package friends.crawler;
import java.net.*;
import java.util.regex.Pattern;
public class UrlFilter 
{
	public static final int  MAX_URL_LENGTH = 512;
	public void Process(String urlString)
	{
		if(urlString == null)
			return;
		if(urlString.length()> MAX_URL_LENGTH)
			return;
		if(urlString.contains("script:"))//filter script links
			return;
		URL url;
		try
		{
			url = new URL(urlString);
			String tempURL = urlString;
			int index = urlString.indexOf('#');
			tempURL = (index==-1?tempURL:tempURL.substring(0,index));//purge anchor
			if(url.getPath()=="")
			{
				int indexOfQuesition = urlString.indexOf('?');
				if(indexOfQuesition > 0)
				{
					String[] strings = tempURL.split("?");
					tempURL = strings[0]+"/?"+strings[1];
				}
				else
				{
					tempURL +="/";
				}
			}
			if (isAllowed(tempURL))
			{
				Crawler.urlDispatcher.process(url.getHost(),tempURL);
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private boolean isAllowed(String url)
	{
		Pattern p1 = Pattern.compile("^(http://www.yelp.com/user_details\\?)(\\S)*");
		if (p1.matcher(url).matches()) 
		{
			return true;
		}
		Pattern p2 = Pattern.compile("^(http://www.yelp.com/user_details_friends\\?)(\\S)*");
		if (p2.matcher(url).matches()) 
		{
			return true;
		}
		return false;
	}
}
