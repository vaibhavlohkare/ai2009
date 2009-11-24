package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import friends.datacollect.FriendsExtractor;


public class FriendsTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void processTest() throws Exception {
		
		FriendsExtractor f = new FriendsExtractor();
		f.Process();
	}
}
