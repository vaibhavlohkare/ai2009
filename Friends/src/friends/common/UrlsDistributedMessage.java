package friends.common;

import java.util.Vector;

public class UrlsDistributedMessage {
	
	private static final long serialVersionUID = 5501701512618594001L;
	public Vector<String> urls ;

	public UrlsDistributedMessage()
	{
		this.urls = new Vector<String>();
	}
	public void addUrl(String url)
	{
		this.urls.add(url);
	}
}
