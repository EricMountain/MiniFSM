package fr.les_enry.util.fsm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unused")
	@Test
	public void testDuplicateAction() {
		Action first = new Action("dupthis") { public boolean act() { return true; }};
		try {
			Action second = new Action("dupthis") { public boolean act() { return true; }};
			fail("Duplicate action detection failed.");
		} catch (RuntimeException e) {
			assertTrue("Exception type for duplicate action", e instanceof Action.AlreadyExistsException);
		}

	}

}
