package friends.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Disk
{

	public static Object load(String objFile) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(objFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = null;
		obj = ois.readObject();
		ois.close();
		fis.close();
		return obj;
	}

	public static void save(Serializable obj, String objFile) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(objFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.close();
		fos.close();
	}
	
	public static void save(Serializable obj, String dir,String fileName) throws IOException
	{
		Util.createDir(dir);
		save(obj, (dir.endsWith("/")||dir.endsWith("\\")?dir:dir+'/')+fileName);
	}

}
