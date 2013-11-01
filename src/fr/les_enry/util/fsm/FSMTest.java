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
		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act() {
				out.append(hi).append(" world…");
				return true;
			}
		}).ok(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);

		assertTrue(fsm.getState().equals(TERM));
		assertTrue(out.toString().equals("hello world…"));
		
		State byName = fsm.getStateByName("init");
		assertTrue(byName == INIT);
		fsm.forceState(byName);
		assertTrue(fsm.getState() == INIT);
	}

	@Test
	public void testFSMWithVarargAction() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final Event INSERT_COIN = fsm.event("insert coin");

		final StringBuffer out = new StringBuffer();
		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act(Object... args) {
				for (Object s : args)
					out.append(s).append(" ");
				return true;
			}
		}).ok(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN, "hello", "world", "how", "are", "you", "?");

		assertTrue(fsm.getState().equals(TERM));
		assertTrue(out.toString().equals("hello world how are you ? "));
	}

	@Test
	public void testFail() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final State NOT_USED = fsm.state("not used");
		final Event INSERT_COIN = fsm.event("insert coin");

		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act() {
				return false;
			}
		}).ok(TERM);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);

		assertTrue(fsm.getState().equals(TERM));
		
		assertTrue(fsm.isState(TERM));
		assertFalse(fsm.isState(INIT));
		
		assertTrue(fsm.isStateIn(TERM, INIT));
		assertFalse(fsm.isStateIn(INIT, NOT_USED));
	}

	@Test
	public void testFail2() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final State FAIL = fsm.state("fail");
		final Event INSERT_COIN = fsm.event("insert coin");

		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act() {
				return false;
			}
		}).ok(TERM).fail(FAIL);
		fsm.start(INIT);
		fsm.event(INSERT_COIN);

		assertTrue(fsm.getState().equals(FAIL));
	}

	@Test
	public void testNoRuleApplies() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final State FAIL = fsm.state("fail");
		final Event INSERT_COIN = fsm.event("insert coin");
		final Event WHATEVER = fsm.event("Not going to apply to any rule");

		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act() {
				return false;
			}
		}).ok(TERM).fail(FAIL);
		fsm.start(INIT);

		assertNull(fsm.softEvent(WHATEVER));
		assertTrue(fsm.getState().equals(INIT));

		try {
			fsm.event(WHATEVER);
			// Must never end up here
			assertTrue(1 == 0);
		} catch (FSM.NoApplicableRuleException e) {
			assertTrue(fsm.getState().equals(INIT));
		}
	}

	@Test
	public void testReset() {
		FSM fsm = new FSM();

		final State INIT = fsm.state("init");
		final State TERM = fsm.state("term");
		final State FAIL = fsm.state("fail");
		final Event INSERT_COIN = fsm.event("insert coin");

		fsm.rule().initial(INIT).event(INSERT_COIN).action(new Action() {
			public boolean act() {
				return false;
			}
		}).ok(TERM).fail(FAIL);
		fsm.start(INIT);

		assertTrue(fsm.getState().equals(INIT));
		
		fsm.reset();
		
		assertTrue(fsm.getState() == null);
	}
	
	@Test
	public void testNavyBatal() {
		FSM fsm = new FSM();

		final State BOAT_TO_PLACE = fsm.state("Boat to place");
		final State CHECK_BOAT_TO_PLACE = fsm.state("Check boat to place");
		final State SHOT_NEEDED = fsm.state("Shot needed");
		final State CHECK_WON = fsm.state("Check won");
		final State GAME_OVER = fsm.state("Game over");

		final Event BOAT_PLACED = fsm.event("Boat placed");
		final Event MORE_BOATS = fsm.event("More boats to place");
		final Event NO_MORE_BOATS = fsm.event("No more boats to place");
		final Event SHOT_TAKEN = fsm.event("Shot taken");
		final Event NOT_WON = fsm.event("Game not won");
		final Event WON = fsm.event("Game won");
		final Event RESET = fsm.event("Reset game");

		fsm.rule().initial(BOAT_TO_PLACE).event(BOAT_PLACED)
				.ok(CHECK_BOAT_TO_PLACE);
		fsm.rule().initial(CHECK_BOAT_TO_PLACE).event(MORE_BOATS)
				.ok(BOAT_TO_PLACE);
		fsm.rule().initial(CHECK_BOAT_TO_PLACE).event(NO_MORE_BOATS)
				.ok(SHOT_NEEDED);
		fsm.rule().initial(SHOT_NEEDED).event(SHOT_TAKEN).ok(CHECK_WON);
		fsm.rule().initial(CHECK_WON).event(NOT_WON).ok(SHOT_NEEDED);
		fsm.rule().initial(CHECK_WON).event(WON).ok(GAME_OVER);
		fsm.rule().initial(GAME_OVER).event(RESET).ok(BOAT_TO_PLACE);

		fsm.start(BOAT_TO_PLACE);

		fsm.event(BOAT_PLACED);
		fsm.event(MORE_BOATS);
		fsm.event(BOAT_PLACED);
		fsm.event(MORE_BOATS);
		fsm.event(BOAT_PLACED);
		fsm.event(MORE_BOATS);
		fsm.event(BOAT_PLACED);
		fsm.event(MORE_BOATS);
		fsm.event(BOAT_PLACED);
		fsm.event(MORE_BOATS);
		assertTrue(fsm.getState().equals(BOAT_TO_PLACE));
		fsm.event(BOAT_PLACED);
		assertTrue(fsm.getState().equals(CHECK_BOAT_TO_PLACE));
		fsm.event(NO_MORE_BOATS);
		assertTrue(fsm.getState().equals(SHOT_NEEDED));

		fsm.event(SHOT_TAKEN);
		fsm.event(NOT_WON);
		fsm.event(SHOT_TAKEN);
		fsm.event(NOT_WON);
		fsm.event(SHOT_TAKEN);
		fsm.event(NOT_WON);
		fsm.event(SHOT_TAKEN);
		fsm.event(NOT_WON);
		fsm.event(SHOT_TAKEN);
		assertTrue(fsm.getState().equals(CHECK_WON));
		fsm.event(NOT_WON);
		assertTrue(fsm.getState().equals(SHOT_NEEDED));
		fsm.event(SHOT_TAKEN);
		assertTrue(fsm.getState().equals(CHECK_WON));
		fsm.event(WON);
		assertTrue(fsm.getState().equals(GAME_OVER));

		fsm.event(RESET);
		assertTrue(fsm.getState().equals(BOAT_TO_PLACE));
	}
}
