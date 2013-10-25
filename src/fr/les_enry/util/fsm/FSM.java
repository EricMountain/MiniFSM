package fr.les_enry.util.fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSM {

	private List<Rule> rules = new ArrayList<Rule>();

	private State state = null;

	private final Map<String, Event> allEvents = new HashMap<String, Event>();
	private final Map<String, State> allStates = new HashMap<String, State>();
	
	public class Rule {
		private State initialState = null;
		private State endState = null;
		private State failState = null;
		private Event event = null;
		private Action action = null;

		public Rule initial(State state) {
			initialState = state;

			return this;
		}

		public Rule ok(State state) {
			endState = state;

			return this;
		}

		public Rule event(Event event) {
			this.event = event;

			return this;
		}

		public Rule action(Action action) {
			this.action = action;

			return this;
		}

		public Rule fail(State state) {
			failState = state;

			return this;
		}

		/**
		 * Process the associated action. If it returns true (success), then
		 * return the end state, else return the failure state. If no failure
		 * state is defined, always returns the end state.
		 * 
		 * @param args Optional arguments to pass to the action
		 * @return final state
		 */
		State apply(Object... args) {
			if (action != null && !action.act(args) && failState != null)
				return failState;
			else
				return endState;
		}
	}

	public class NoApplicableRuleException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8807461947595834489L;

		public NoApplicableRuleException(String message) {
			super(message);
		}
	}

	public class UnknownBaseTypeException extends RuntimeException {
		/**
		 * Serialisation ID.
		 */
		private static final long serialVersionUID = 2026589136585844252L;

		public UnknownBaseTypeException(String message) {
			super(message);
		}
	}

	public class NoMapForBaseTypeException extends RuntimeException {
		/**
		 * Serialisation ID.
		 */
		private static final long serialVersionUID = -8428840864735412207L;

		public NoMapForBaseTypeException(String message) {
			super(message);
		}
	}

	// public FSM() {
	// super();
	// }

	private BaseType factory(String name, final Class<? extends BaseType> type) {
		BaseType obj;

		if (name == null)
			obj = null;
		else
			obj = (BaseType) get(name, type);

		if (obj == null) {
			try {
				obj = (BaseType) (type.newInstance().setName(name));
			} catch (InstantiationException e) {
				// TODO Handle these exceptions better
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}

		if (name != null)
			put(obj);

		return obj;
	}

	public Event event(String name) {
		return (Event) factory(name, Event.class);
	}

	public State state(String name) {
		return (State) factory(name, State.class);
	}

	/**
	 * Creates an Action object from a class. Not made public as this doesn't
	 * look the best way to go.
	 * 
	 * @param action
	 *            Class to instantiate.
	 * @return Action object.
	 */
	Action action(Class<? extends Action> action) {
		return (Action) factory(null, action);
	}

	private void put(BaseType object) {
		if (Event.class.isAssignableFrom(object.getClass())) {
			allEvents.put(object.getName(), (Event) object);
		} else if (State.class.isAssignableFrom(object.getClass())) {
			allStates.put(object.getName(), (State) object);
		} else if (Action.class.isAssignableFrom(object.getClass())) {
			throw new NoMapForBaseTypeException("No map for subclasses of Action: " + object.getClass());
		} else {
			throw new UnknownBaseTypeException("Unknown class: "
					+ object.getClass().getName());
		}
	}

	private Object get(String name, Class<?> type) {
		if (Event.class.isAssignableFrom(type)) {
			return allEvents.get(name);
		} else if (State.class.isAssignableFrom(type)) {
			return allStates.get(name);
		} else if (Action.class.isAssignableFrom(type)) {
			throw new NoMapForBaseTypeException("No map for subclasses of Action: " + type);
		} else {
			throw new UnknownBaseTypeException("Unknown class: "
					+ type.getName());
		}
	}

	public void start(State state) {
		this.state = state;
	}

	State getState() {
		return state;
	}

	public Rule rule() {
		Rule rule = new Rule();
		rules.add(rule);
		return rule;
	}

	public State event(Event event) {
		return event(event, (Object) null);
	}

	
	/**
	 * Processes occurrence of an event. Searches for an applicable rule, and
	 * runs the associated action.
	 * 
	 * @param event
	 * @param args Optional objects to pass to the action 
	 * @return state entered after applying the rule
	 */
	public State event(Event event, Object... args) {
		// TODO Replace this full-scan with a Map lookup
		for (Rule rule : rules) {
			if (rule.initialState == state && rule.event == event) {
				state = rule.apply(args);
				return state;
			}
		}

		throw new NoApplicableRuleException(
				"No applicable state/event combination: " + state + ", "
						+ event);
	}
}
