package friends.crawler;

public class AbsUrlConstructor 
{
	public static String construct(String urlString, String href)
	{

		String tempURL = href;
		if(!href.contains("://"))//relative address
		{
		
			if(href.startsWith("//")) //illegal ,skip;
				return null;
			if(href.toLowerCase().startsWith("mailto:"))
				return null;
			if(href.startsWith("/"))//not very legal,treat as ../
			{
			//	href = href.substring(1);
				href = ".." + href;
			}
			if(urlString.endsWith("/"))
			{
				tempURL = urlString + href;
			}
			else
			{
				int indexOfSlash = urlString.lastIndexOf('/');
				int indexofDSlash = urlString.indexOf("//");
				if(indexOfSlash <= indexofDSlash+1)
				{
					System.out.println("Illegal Link.Skip...");
					return null;
				}
				else
				{
					tempURL = urlString.substring(0,urlString.lastIndexOf('/')+1) + href;
				}
			}
		}
		//Now let's collapse  ../
		int size = -1;
		if(tempURL.contains("../"))
		{
			while(tempURL.length() != size)
			{
				size = tempURL.length();
				tempURL = tempURL.replaceFirst("(?!/)/\\.\\./", "/");
			}
			while(tempURL.contains("../"))
			{
				tempURL = tempURL.replaceFirst("\\.\\./", "");
			}
		}
		
		tempURL.replaceAll("/\\./", "/");
		return tempURL;
	}
}
