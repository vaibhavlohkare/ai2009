package friends.crawler;

import java.net.MalformedURLException;
import java.net.URL;

import myGoogle.Common.Cache.LRUCache;

public class RobotChecker
{
	private  LRUCache<String, Robot> cache = new LRUCache<String, Robot>(500);
	public  boolean   process(String urlString)
	{
		URL url;
		try
		{
			url = new URL(urlString);
		}
		catch (MalformedURLException e)
		{
			return false;
		}
		
		String host = url.getHost();
		Robot robot = cache.get(host);
		if(robot == null)
		{
			robot = new Robot(host);
			cache.put(host, robot);
		}
		String path = url.getPath().length() == 0 ? "/":url.getPath();
		if(robot.violate(path))
		{
			if(Crawler.showLog)
				System.out.println("Robot Match:" + urlString);
			return false;
		}
		else
		{
			return true;
		}
	}
}
