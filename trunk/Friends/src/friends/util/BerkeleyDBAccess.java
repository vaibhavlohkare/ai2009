package friends.util;
/**
 * [MyDatabase usage]
 * 1) add "je.jar" lib
 * 
 * 2) define Database:
 * 		MyDatabase<key, value> UserDB = new MyDatabase<String, User>(key.class, value.class);
 * 
 * 3) get Enviroment:
 * 		MyDatabase.createEnv(DatabaseRootPath);
 * 
 * 4) open Database:
 * 		UserDB.openDB("DBname",isDuplicate);
 * 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;


public class BerkeleyDBAccess<K,V> {

	/**
	 * @param args
	 */
	public static final String ENCODING = "UTF-8";
	public static final boolean DEBUG = true;
	
	private Environment myEnv = null;
	private EnvironmentConfig myEnvDBconfig = null;
	
	//DB handler	we have only one database in the system!
	private String dbName = null;
	private String classdbName = null;
	private boolean isOpen = false;
	
	private Database myDB = null; 
	private Database myClassDB = null;
	
	private DatabaseConfig myDBconfig = null;
	private StoredClassCatalog myClassCatolog = null; 
	
	private Class KeyClass = null;
	private Class ValueClass = null;
	private EntryBinding KeyEntryBinding = null;
	private EntryBinding ValueEntryBinding = null;
	
	//private static final int NUMTHREADS = 1;  // only one thread in the access thread pool
	
	public BerkeleyDBAccess(Class KeyC,Class ValueC) throws DatabaseException {
		
		KeyClass = KeyC;
		ValueClass = ValueC;
	
	}
	
	/*
	 *  Open & Close Database
	 */
	public void openDB(String TableName,boolean isDuplicate) throws DatabaseException {
		
		if(isOpen == true) return;
		
		dbName = TableName;
		classdbName = TableName + "Class";

		myDBconfig = new DatabaseConfig();
		myDBconfig.setReadOnly(false);
		myDBconfig.setAllowCreate(true);
		//myDBconfig.setTransactional(true);
		
		myClassDB = myEnv.openDatabase(null, classdbName, myDBconfig);
		myClassCatolog = new StoredClassCatalog(myClassDB);
		
		myDBconfig.setSortedDuplicates(isDuplicate);
		myDB = myEnv.openDatabase(null, dbName, myDBconfig);
		
		if (!(KeyClass == null || ValueClass == null)) {
			KeyEntryBinding = new SerialBinding(myClassCatolog, KeyClass);
			ValueEntryBinding = new SerialBinding(myClassCatolog, ValueClass);
		}
		// any Exception, not reach here
		isOpen = true;	
	}

	public void closeDatabase() throws DatabaseException {
		if(myDB != null)
		{
			myDB.close();
			myDB = null;
		}
		if(myClassDB != null)
		{
			myClassDB.close();
			myClassDB = null;
		}
	}

	/*
	 *  Basic Operations
	 */
	public synchronized void replace(K key, V newvalue) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		//Transaction currTxn = null;
		Cursor cursor = null;
		try{
			//delete(key);
			//add(key,newvalue);
			//currTxn = myEnv.beginTransaction(null, null);
			cursor = getCursor(null);
			
			DatabaseEntry theKey = new DatabaseEntry();
			KeyEntryBinding.objectToEntry(key, theKey);
			DatabaseEntry theValue = new DatabaseEntry();
			
			DatabaseEntry replacementValue = new DatabaseEntry();
			ValueEntryBinding.objectToEntry(newvalue, replacementValue);
			
			if (cursor.getSearchKey(theKey, theValue, LockMode.DEFAULT) 
											== OperationStatus.SUCCESS) {
				cursor.putCurrent(replacementValue);
			}
		}
		catch(DatabaseException e){
			printError("Database Add Error! ");
			printError(e);
			throw e;
		}
		finally {
			cursor.close();
			//currTxn.commit();
		}
	}
	
	public synchronized boolean add(K key, V value) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");

		//Transaction currTxn = null;
		try{
			//currTxn = myEnv.beginTransaction(null, null);
					
			DatabaseEntry theKey = new DatabaseEntry();
			KeyEntryBinding.objectToEntry(key, theKey);
			
			DatabaseEntry theValue = new DatabaseEntry();
			ValueEntryBinding.objectToEntry(value, theValue);
			
			myDB.put(null, theKey, theValue);
			//currTxn.commit();
			return true;
		}
		catch(DatabaseException e){
			printError("Database Add Error! ");
			printError(e);
			/*
			if(currTxn != null){
					currTxn.abort();
					currTxn = null;
				}
				*/
			throw e;
		}
	}
	
	public synchronized boolean add(K key, Iterator<V> values) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		if (values == null)
			return false;

		//Transaction currTxn = null;
		Cursor cursor = null;
		try{
			//currTxn = myEnv.beginTransaction(null, null);
			cursor = getCursor(null);
			
			DatabaseEntry theKey = new DatabaseEntry();
			KeyEntryBinding.objectToEntry(key, theKey);
			while(values.hasNext())
			{
				V savedValue = values.next();
				DatabaseEntry theValue = new DatabaseEntry();
				ValueEntryBinding.objectToEntry(savedValue, theValue);
				cursor.put(theKey, theValue);
			}
			cursor.close();
			//currTxn.commit();
			return true;
		}
		catch(DatabaseException e){
			printError("Database Add Error! ");
			printError(e);
			cursor.close();
			/*
			if(currTxn != null){
					currTxn.abort();
					currTxn = null;
				}
				*/
			throw e;
		}
	
	}
	
	@SuppressWarnings("unchecked")
	public synchronized V get(K key) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		V retData = null;
		Cursor cursor = getCursor(null);
	
		DatabaseEntry theKey = new DatabaseEntry();
		KeyEntryBinding.objectToEntry(key, theKey);
		
		DatabaseEntry theValue = new DatabaseEntry();
		OperationStatus retStatus = 
				cursor.getSearchKey(theKey, theValue, LockMode.DEFAULT);
		if (retStatus == OperationStatus.SUCCESS) {
			retData = (V)ValueEntryBinding.entryToObject(theValue);
		}
		else {
			retData = null;
		}
		cursor.close();
		return retData;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized V getFirst() throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		V retData = null;
		Cursor cursor = getCursor(null);
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theValue = new DatabaseEntry();
		OperationStatus retStatus = 
				cursor.getFirst(theKey, theValue, LockMode.DEFAULT);
		if (retStatus == OperationStatus.SUCCESS) {
			retData = (V)ValueEntryBinding.entryToObject(theValue);
		}
		else {
			retData = null;
		}
		cursor.close();
		return retData;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized V getNext(K key) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		V retData = null;
		Cursor cursor = getCursor(null);
		DatabaseEntry theKey = new DatabaseEntry();
		KeyEntryBinding.objectToEntry(key, theKey);
		DatabaseEntry theValue = new DatabaseEntry();
		if (cursor.getSearchKey(theKey, theValue, LockMode.DEFAULT) 
				== OperationStatus.SUCCESS)
		{
			OperationStatus retStatus = 
					cursor.getNext(theKey, theValue, LockMode.DEFAULT);
			if (retStatus == OperationStatus.SUCCESS) {
				retData = (V)ValueEntryBinding.entryToObject(theValue);
			}
		}
		else {
			retData = null;
		}
		
		cursor.close();
		return retData;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Collection<V> getNFromKey(K key, int Num) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		ArrayList<V> retList = new ArrayList<V>();
		Cursor cursor = getCursor(null);
		
		try {
	        DatabaseEntry theKey = new DatabaseEntry();
	        KeyEntryBinding.objectToEntry(key, theKey);
	        DatabaseEntry FoundValue = new DatabaseEntry();
	        if (OperationStatus.SUCCESS == cursor.getSearchKey(theKey, FoundValue, LockMode.DEFAULT)) {
	        	int i = 0;
	        	do {
		        	V retData = (V)ValueEntryBinding.entryToObject(FoundValue);
		        	retList.add(retData);
		        	 if (OperationStatus.NOTFOUND == cursor.getNext(theKey, FoundValue, LockMode.DEFAULT))
		        		 break;
		        	 i++;
		        }
		        while (i<Num);
	        }
	        else {
	        	new DatabaseException("Data Not Exist");
	        }
	        
	        cursor.close();
	        
	        return retList;

	    }
		catch(DatabaseException e){
			printError("MyDatabase GetAll Error! ");
			printError(e);
			cursor.close();
			throw e;
		}
	}
	
	public synchronized V delete(K key) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		DatabaseEntry theKey = new DatabaseEntry();
		KeyEntryBinding.objectToEntry(key, theKey);
		
		V retValues = get(key);
		if (myDB.delete(null, theKey) == OperationStatus.SUCCESS)
		{
			return retValues;
		}
		return null;

	}
	
	public synchronized boolean contains(K key) throws DatabaseException
	{
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		try{
			DatabaseEntry theKey = new DatabaseEntry();
			KeyEntryBinding.objectToEntry(key, theKey);
			
			DatabaseEntry theValue = new DatabaseEntry();
			OperationStatus retStatus = 
					myDB.get(null,theKey, theValue, LockMode.DEFAULT);
			return (retStatus == OperationStatus.SUCCESS)? true:false;
		}
		catch(DatabaseException dbe)
		{
			printError("Database Add Error! " + dbe);
			return false;
		}
		catch(Exception e)
		{
			printError("Database Key Convert Data Error! " + e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Iterator<K> getAllKeys() throws DatabaseException {
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		ArrayList<K> queue = new ArrayList<K>();
		Cursor cursor = null;
		cursor = getCursor(null);
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theValue = new DatabaseEntry();
		try {
			while(cursor.getNext(theKey, theValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) 
			{
				K retkey = (K)ValueEntryBinding.entryToObject(theKey);
				queue.add(retkey);
			}
			cursor.close();
		}
		catch(DatabaseException e) {
			printError(e);
			cursor.close();
			throw e;
		}
		return queue.iterator();
			
		}
	
	@SuppressWarnings("unchecked")
	public synchronized Iterator<V> getAllValues() throws DatabaseException {
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		ArrayList<V> queue = new ArrayList<V>();
		Cursor cursor = null;
		cursor = getCursor(null);
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theValue = new DatabaseEntry();
		try {
			while(cursor.getNext(theKey, theValue, LockMode.DEFAULT) == OperationStatus.SUCCESS) 
			{
				V retValue = (V)ValueEntryBinding.entryToObject(theValue);
				queue.add(retValue);
			}
			cursor.close();
		}
		catch(DatabaseException e) {
			printError(e);
			cursor.close();
			throw e;
		}
		
		return queue.iterator();
		
	}
	
	@SuppressWarnings("unchecked")
	public synchronized Collection<V> getAllValues(K key) throws DatabaseException{
		
		if (!isOpen) throw new DatabaseException("Database Not Open");
		
		ArrayList<V> retList = new ArrayList<V>();
		Cursor cursor = getCursor(null);
		
		try {
	        DatabaseEntry theKey = new DatabaseEntry();
	        KeyEntryBinding.objectToEntry(key, theKey);
	        DatabaseEntry FoundValue = new DatabaseEntry();
	        if (OperationStatus.SUCCESS == cursor.getSearchKey(theKey, FoundValue, LockMode.DEFAULT)) {
	        	do {
		        	V retData = (V)ValueEntryBinding.entryToObject(FoundValue);
		        	retList.add(retData);
		        }
		        while (OperationStatus.SUCCESS == cursor.getNextDup(theKey, FoundValue, LockMode.DEFAULT));
	        }
	        else {
	        	new DatabaseException("Data Not Exist");
	        }
	        
	        cursor.close();
	        
	        return retList;

	    }
		catch(DatabaseException e){
			printError("MyDatabase GetAll Error! ");
			printError(e);
			cursor.close();
			throw e;
		}

	}

	private Cursor getCursor(Transaction txn)
	{
		try{
			return myDB.openCursor(txn, null);
		}
		catch(DatabaseException dbe)
		{
			System.out.println("Cursor close Error! " + dbe);
			return null;
		}
	}
	
	public void Sync() throws DatabaseException{
		this.myEnv.sync();
	}
	
	// DataBase Environment related
	
	public void setDBPath(String DBpath)  // change environment
	{
		if (myEnv != null) {
			closeEnv();
			myEnv=null;
		}
		createEnv(DBpath);
	}
	
	public void createEnv(String EnvPath){
		if (EnvPath == null) throw new IllegalArgumentException("Path not exist!"); 

		if (myEnv == null) {
			File dir = new File(EnvPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			myEnvDBconfig = new EnvironmentConfig();
			myEnvDBconfig.setReadOnly(false);
			myEnvDBconfig.setAllowCreate(true);
			try{
				myEnv = new Environment(new File(EnvPath), myEnvDBconfig);
			}
			catch (DatabaseException dbe) {
				printError(dbe);
			}
		}
	}
	
	public Environment getMyEnv() {
		return this.myEnv;
	}
	
	public void setMyEnv(Environment myEnv) {
		this.myEnv = myEnv;
	}

	public void closeEnv(){
		try{
			if (myEnv != null) {
				myEnv.close();
			}
		}
		catch (DatabaseException dbe) {
			printError(dbe);
		}
	}
	
	/**
	 * util
	 */
	private void printError(String error)
	{
		if (DEBUG == true)
		{
			System.out.println(error);
		}
	}
	
	private void printError(Exception e)
	{
		if (DEBUG == true)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{

		BerkeleyDBAccess<Long,String> DB = new BerkeleyDBAccess<Long,String>(Long.class,String.class);
		DB.createEnv("./pagerank/pagerankDb");
		DB.openDB("PageRank",false);
		//BerkeleyDBAccess<Long,Long> DB2 = new BerkeleyDBAccess<Long,Long>(Long.class,Long.class);
		//DB2.setMyEnv(DB.getMyEnv());
		//DB2.openDB("Test",true);
		
		DB.add(1l, "1 good");
//		DB.add(1l, "ww");
		DB.add(5l, "5 bad");
		DB.add(4l, "4 not bad");
		DB.add(3l, "3 not well");
		DB.add(6l, "6 perfect");
		DB.add(8l, "8 not good");
		
		//DB2.add(1l, 1l);
		//System.out.println(DB.getFirst());
		//DB.delete(1l);
		Collection<String> list = DB.getNFromKey(1l,4);
		LinkedList<String> link = new LinkedList<String>();
		for(String s:list) {
			System.out.println(s);
			link.add(s);
		}
		System.out.println(DB.getNext(3l));
//		link.remove("good");
//		int size=link.size();
//		for(int i=0;i<size;i++) {
//			System.out.println(link.get(i));
//		}

//		do {
//			String retValue = DB.getNextNonDup();
//			if (retValue != null)
//				System.out.println(retValue);
//			else
//				break;
//		}
//		while(true);
		DB.Sync();
		DB.closeDatabase();
		//DB2.closeDatabase();
		//DB2.closeEnv();

	}
}
