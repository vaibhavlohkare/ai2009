package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

public class Robot
{

	private LinkedList<String> disallow = null;
	private boolean norobotstxt;

	public Robot(String host)
	{
		try
		{
			URL u = new URL("http://" + host + "/robots.txt");
			InputStreamReader reader = new InputStreamReader(u.openStream());
			BufferedReader br = new BufferedReader(reader);
			String newline;
			disallow = new LinkedList<String>();
			while ((newline = br.readLine()) != null)
			{
				if (newline.startsWith("Disallow"))
				{
					String[] strings = newline.split("\\s");
					disallow.add(strings[1].trim());
				}
			}
			br.close();
			reader.close();
			norobotstxt = false;
			u = null;

		}
		catch (Exception ex)
		{
			disallow = null;
			norobotstxt = true;
		}

	}

	public boolean violate(String path)
	{
		if (norobotstxt)
			return false;
		else
		{
			Iterator<String> it = disallow.iterator();
			while (it.hasNext())
			{
				if (path.startsWith(it.next()))
					return true;
			}
			return false;
		}
	}
}
