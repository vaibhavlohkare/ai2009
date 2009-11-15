package friends.crawler;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import friends.common.UrlDistibutionMessage;
import friends.distributed.PastryAppWrapper;

public class DistributeChannel extends PastryAppWrapper
{

	protected void process(Id id, Message message) throws Exception
	{
		if(message instanceof UrlDistibutionMessage)
		{
			UrlDistibutionMessage m = (UrlDistibutionMessage)message;
			int size = m.urls.size();
			if(size >0)
			{
				for(int i = 0;i<size;i++)
				{
//					try
//					{
						Crawler.due.process(m.urls.elementAt(i));
//					}
//					catch (java.util.NoSuchElementException e)
//					{
//						
//					}
				}
			}
		}
		else
		{
			System.out.println("Warning: Unhandled: " + message.getClass().getSimpleName());
		}
		
	}

}
