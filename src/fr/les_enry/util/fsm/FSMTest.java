package fr.les_enry.util.fsm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
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

	@SuppressWarnings("serial")
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

		State byName = fsm.findStateByName("init");
		assertTrue(byName == INIT);
		fsm.start(byName);
		assertTrue(fsm.getState() == INIT);
	}

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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

		fsm.start(INIT);
		try {
			fsm.event(INSERT_COIN);

			fail("FSM incorrectly cleared");
		} catch (FSM.NoApplicableRuleException e) {
			// Good, FSM was reset including clearing rules
		}

	}

	@Test
	public void testNavyBattle() {
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

		for (int i = 0; i < 10; i++) {
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

	@Test
	public void testNavyBattleSerialisation() {
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

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(fsm);

			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			FSM deserialised = (FSM) ois.readObject();

			assertTrue(deserialised.isState(deserialised
					.findStateByName("Game over")));
		} catch (Exception e) {
			e.printStackTrace();
			fail("FSM (de)serialisation failed");
		}
	}
	
	@Test
	public void testDagOutput() {
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
		fsm.rule().initial(GAME_OVER).event(RESET).ok(BOAT_TO_PLACE).fail(GAME_OVER);

		fsm.start(BOAT_TO_PLACE);

		String dag = fsm.toDag();
		System.out.println(dag);
		
		String refFile = "ref/test1.gv";
		try {
			assertThat(new StringReader(dag), FileMatcher.fileTextMatches(new FileReader(new File(refFile))));
		} catch (FileNotFoundException e) {
			fail("File not found: " + refFile);
		}
	}
	
}

/**
 * Checks the content of 2 Graphviz sources represent identical graphs. 
 */
class FileMatcher {
	public static <T> Matcher<Reader> fileTextMatches(final Reader expectedFile) {
		return new BaseMatcher<Reader>() {
		
			@Override
			public boolean matches(Object testReaderObj) {
				BufferedReader testReader = new BufferedReader((Reader) testReaderObj);
				
				try {
					BufferedReader expectedReader = new BufferedReader(expectedFile);
					String expectedLine;
					Map<String, Integer> string2Count = new HashMap<String, Integer>();
					while ((expectedLine = expectedReader.readLine()) != null) {
						final String edgeLinePrefix = "  \"";
						String testLine = testReader.readLine();
						
						// System.out.println("expt: " + expectedLine);
						// System.out.println("test: " + testLine);
						
						Matcher<?> nodeLineMatcher = CoreMatchers.equalTo(expectedLine);
						Matcher<?> firstLineMatcher = CoreMatchers.equalTo("digraph \"{unnamed_FSM}\" {");
						Matcher<?> lastLineMatcher = CoreMatchers.equalTo("}");
						
						// Rules aren't ordered, so edges don't come out in the same order every time.
						// So check each edge occurs in each file.
						if (firstLineMatcher.matches(testLine)) {
							continue;
						} else if (nodeLineMatcher.matches(testLine)) {
							continue;
						} else if (lastLineMatcher.matches(testLine)) {
							continue;
						} else if (testLine.startsWith(edgeLinePrefix) && expectedLine.startsWith(edgeLinePrefix)) {
							// park
							int c1 = string2Count.containsKey(testLine) ? string2Count.get(testLine) : 0;
							string2Count.put(testLine, c1 + 1);
							
							int c2 = string2Count.containsKey(expectedLine) ? string2Count.get(expectedLine) : 0;
							string2Count.put(expectedLine, c2 + 1);
						} else {
							return false;
						}
					}
					
					for (String str : string2Count.keySet()) {
						if (string2Count.get(str) != 2) {
							// TODO In more recent Hamcrest, use the describe failure feature
							return false;
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("content from each Reader to match");
			}
			
		};
	}	
}
