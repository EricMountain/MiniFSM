package fr.les_enry.util.fsm;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FSMTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFSM() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final Event INSERT_COIN = fsm.event("insert coin");

		final String hi = "hello";
		final StringBuffer out = new StringBuffer();
		fsm.rule().state(INIT).event(INSERT_COIN).action(new Action(){public boolean act(){ out.append(hi).append(" world…"); return true;}}).state(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);
		
		assertTrue(fsm.getState().equals(TERM));
		assertTrue(out.toString().equals("hello world…"));
	}

	@Test
	public void testFail() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final Event INSERT_COIN = fsm.event("insert coin");

		fsm.rule().state(INIT).event(INSERT_COIN).action(new Action(){public boolean act(){return false;}}).state(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);
		
		assertTrue(fsm.getState().equals(TERM));
	
	}

	@Test
	public void testFail2() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final State FAIL = fsm.state("fail");
		final Event INSERT_COIN = fsm.event("insert coin");

		fsm.rule().state(INIT).event(INSERT_COIN).action(new Action(){public boolean act(){return false;}}).state(TERM).fail(FAIL);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);
		
		assertTrue(fsm.getState().equals(FAIL));
	
	}

}
