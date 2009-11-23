package tests;

import java.util.LinkedList;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.NEW;

import friends.crawler.CrawlerWorker;
import friends.crawler.FetchedDoc;
import friends.crawler.StreamFetcher;
import friends.datacollect.LinkExtractor;

import friends.database.*;

public class LinkExtractorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void processTest() throws Exception {
		
		StreamFetcher sf = new StreamFetcher(new CrawlerWorker());
		FetchedDoc doc = sf.Fetch("http://www.yelp.com/user_details?userid=ei8X5pyCur3d0CGb5EbnFA");
		
		LinkExtractor le = new LinkExtractor(1);
		le.Process(2L, doc);
	}

}
