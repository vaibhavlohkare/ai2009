package friends.crawler;
import java.io.*;

public class FetchedDoc 
{
	String urlString;
	String encoding;
	ByteArrayInputStream memoryStream;
	byte[] data;
	FetchedDoc(String urlString,String encoding, byte[] data)
	{
		this.memoryStream = new ByteArrayInputStream(data);
		this.data = data;
		this.urlString = urlString;
		this.encoding = encoding;
	}
	
	protected void finalize()
	{
		try
		{
			data =null;
			memoryStream.close();
		}
		catch(Exception e)
		{
			
		}
	}
}
