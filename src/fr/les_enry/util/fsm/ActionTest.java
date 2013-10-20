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

	static class StaticAction extends Action {
		StaticAction() {
			super();
		}

		public boolean act() {
			return true;
		}
	}

	@Test
	public void testCreateActionByPassingClass() {
		FSM fsm = new FSM();

		Action first = fsm.action(StaticAction.class);

		assertNotNull(first);
	}

	@Test
	public void testCreateAnonAction() {
		Action first = new Action() {
			public boolean act() {
				return false;
			}
		};

		assertNotNull(first);

		Action second = new Action() {
			public boolean act() {
				return false;
			}
		};

		assertNotNull(second);

		assertNotSame(first, second);
	}

	@Test
	public void testActionSideEffect() {
		class IntegerWrapper {
			int val;

			void put(int x) {
				val = x;
			}
			
			int get() {
				return val;
			}
		};
		
		final int x = 9;
		final IntegerWrapper val = new IntegerWrapper();
		
		Action first = new Action() {
			public boolean act() {
				val.put(x * x);
				return false;
			}
		};

		assertNotNull(first);

		assertFalse(first.act());

		assertTrue(val.get() == x * x);
	}
}
