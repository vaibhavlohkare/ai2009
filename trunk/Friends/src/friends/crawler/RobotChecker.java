package friends.crawler;

import friends.database.*;
import java.net.*;

//import myGoogle.Common.Cache.LRUCache;

public class RobotChecker
{
	//private  LRUCache<String, Robot> cache = new LRUCache<String, Robot>(500);
	private Link cache = new Link();
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
		//Robot robot = (Robot)cache.getLink(host);
		Robot robot = new Robot(cache.getLink());
		
		if(robot == null)
		{
			robot = new Robot(host);
			//cache.put(host, robot);
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
