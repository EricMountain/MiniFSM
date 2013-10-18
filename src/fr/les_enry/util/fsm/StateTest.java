package fr.les_enry.util.fsm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StateTest {
	private State init;
	private State init2;
	private State term;
	
	@Before
	public void setUp() throws Exception {
		FSM fsm = new FSM();
		init = fsm.state("init");
		term = fsm.state("term");
		init2 = fsm.state("init");
	}

	@After
	public void tearDown() throws Exception {
		init = term = init2 = null;
	}

	@Test
	public void testHashCode() {
		assertTrue(init.hashCode() == init2.hashCode());
		assertTrue(init.hashCode() != term.hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertTrue(init == init2);
		assertTrue(init != term);
	}

	@Test
	public void testToString() {
		assertTrue(init.toString().startsWith("init"));
		assertTrue(init2.toString().startsWith("init"));
		assertTrue(term.toString().startsWith("term"));
	}

}
