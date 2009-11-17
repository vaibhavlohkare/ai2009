package tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import friends.crawler.CrawlerWorker;
import friends.crawler.FetchedDoc;
import friends.crawler.StreamFetcher;
import friends.datacollect.LinkExtractor;

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
		FetchedDoc doc = sf.Fetch("https://dl.dropbox.com/u/14862/test.html");
		
		LinkExtractor le = new LinkExtractor(1);
		le.Process(2, doc);
		
		Assert.assertTrue(le.docToLinksMappingDb.get(2).size() == 4);
		
	}

}
