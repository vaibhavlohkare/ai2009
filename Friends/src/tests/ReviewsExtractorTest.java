package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import friends.datacollect.ReviewsExtractor;




public class ReviewsExtractorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void processTest() throws Exception {
		
		ReviewsExtractor review = new ReviewsExtractor();
		review.Process();
		
	}
}
