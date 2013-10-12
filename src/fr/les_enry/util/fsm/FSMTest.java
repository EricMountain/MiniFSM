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
	public void test() {
		FSM fsm = new FSM();
		fsm.rule().state(State.build("init")).event(Event.build("insert coin")).state(State.build("term"));
		fsm.start(State.build("init"));
		fsm.event(Event.build("insert coin"));
		
		assertTrue(fsm.getState().equals(State.build("term")));
	}

}
