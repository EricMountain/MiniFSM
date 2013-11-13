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

		private static final long serialVersionUID = -1742651398108383124L;

		StaticAction() {
			super();
		}

		public boolean act() {
			return true;
		}
	}

	@SuppressWarnings("serial")
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

	@SuppressWarnings("serial")
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
	
	@SuppressWarnings("serial")
	@Test
	public void testActionWithVararg() {
		class IntegerWrapper {
			int val;

			void put(int x) {
				val = x;
			}
			
			int get() {
				return val;
			}
		};
		
		final IntegerWrapper val = new IntegerWrapper();

		Action action = new Action() {
			public boolean act(Object... args) {
				int sum = 0;
				for (Object i : args) {
					sum += (Integer) i;
				}
				
				val.put(sum);
				
				return true;
			}
		};
		

		assertNotNull(action);

		assertFalse(action.act());
		assertTrue(action.act(1, 2, 3, 4, 5));

		assertTrue(val.get() == 1+2+3+4+5);
		
	}

	@SuppressWarnings("serial")
	@Test
	public void testActionWithNullVararg() {
		Action action = new Action() {
			public boolean act(Object... args) {
				if (args.length == 1 && args[0] == null)
					return true;
				else
					return false;
			}
		};
		

		assertNotNull(action);

		assertFalse(action.act());
		assertTrue(action.act((Object) null));
		
	}
}
