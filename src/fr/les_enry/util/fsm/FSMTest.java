package fr.les_enry.util.fsm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FSMTest {
	private final State INIT = State.build("init");
	private final State INIT2 = State.build("init");
	private final State TERM = State.build("term");
	private final Event INSERT_COIN = Event.build("insert coin");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFSM() {
		FSM fsm = new FSM();
		final String hi = "hello";
		fsm.rule().state(INIT).event(INSERT_COIN).action(new Action(hi){public boolean act(){System.out.println(hi); return true;}}).state(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);
		
		assertTrue(fsm.getState().equals(State.build("term")));
	}

	@Test
	public void testEquality() {
		assertTrue(INIT == INIT2);
	}

	@Test
	public void testInequality() {
		assertTrue(INIT != TERM);
	}
}
