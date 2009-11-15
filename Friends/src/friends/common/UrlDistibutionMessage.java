package friends.common;

import java.util.Vector;

import friends.distributed.GenericMessage;

public class UrlDistibutionMessage extends GenericMessage
{
	private static final long serialVersionUID = 5501701512618594857L;
	public Vector<String> urls ;

	public UrlDistibutionMessage()
	{
		this.urls = new Vector<String>();
	}
	public void addUrl(String url)
	{
		this.urls.add(url);
	}
}
