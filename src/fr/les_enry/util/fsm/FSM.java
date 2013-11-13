package fr.les_enry.util.fsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Flying Spaghetti Monster.
 */
public class FSM implements Serializable {

	private static final long serialVersionUID = 5664588699774045358L;

	private transient Logger LOGGER = Logger.getLogger(this.getClass()
			.getName());

	private String name = "{unnamed_FSM}";

	/** List of rules that make up this FSM. */
	private List<Rule> rules = new ArrayList<Rule>();

	/** Current FSM state. */
	private State state = null;

	/** Map event names to objects. */
	private final Map<String, Event> allEvents = new HashMap<String, Event>();

	/** Map state names to objects. */
	private final Map<String, State> allStates = new HashMap<String, State>();

	/**
	 * Represents a rule. Composed of an initial state, target state, event that
	 * triggers the rule, an action to perform (optional), and an alternative
	 * target state which is entered if the action "fails".
	 */
	public class Rule implements Serializable {

		private static final long serialVersionUID = 3292473755494756309L;

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
		 * @param args
		 *            Optional arguments to pass to the action
		 * @return final state
		 */
		State apply(Object... args) {
			if (action != null && !action.act(args) && failState != null)
				return failState;
			else
				return endState;
		}
	}

	/**
	 * Thrown if no rule applies to the current state when an event occurs.
	 */
	public class NoApplicableRuleException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8807461947595834489L;

		public NoApplicableRuleException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown if we try to create a derived class of BaseType that we don't know
	 * of.
	 */
	public class UnknownBaseTypeException extends RuntimeException {
		/**
		 * Serialisation ID.
		 */
		private static final long serialVersionUID = 2026589136585844252L;

		public UnknownBaseTypeException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown if we try to look up an action in the name/object maps.
	 */
	public class NoMapForBaseTypeException extends RuntimeException {
		/**
		 * Serialisation ID.
		 */
		private static final long serialVersionUID = -8428840864735412207L;

		public NoMapForBaseTypeException(String message) {
			super(message);
		}
	}

	public FSM() {
		super();
	}

	public FSM(String name) {
		this.name = name;
	}

	/**
	 * Creates State, Event and Action objects. If an object of the same name
	 * and type already exists it is returned, except for actions.
	 * 
	 * @param name
	 *            Object name.
	 * @param type
	 *            Class of the object we want.
	 * @return existing or new instance of the requested object.
	 */
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
	 * @deprecated
	 */
	Action action(Class<? extends Action> action) {
		return (Action) factory(null, action);
	}

	/**
	 * Stores an Event or State object in the appropriate map.
	 * 
	 * @param object
	 */
	private void put(BaseType object) {
		if (Event.class.isAssignableFrom(object.getClass())) {
			allEvents.put(object.getName(), (Event) object);
		} else if (State.class.isAssignableFrom(object.getClass())) {
			allStates.put(object.getName(), (State) object);
		} else if (Action.class.isAssignableFrom(object.getClass())) {
			throw new NoMapForBaseTypeException(
					"No map for subclasses of Action: " + object.getClass());
		} else {
			throw new UnknownBaseTypeException("Unknown class: "
					+ object.getClass().getName());
		}
	}

	/**
	 * Looks for an Event or State of the given name in the maps.
	 * 
	 * @param name
	 * @param type
	 *            Event or State
	 * @return object if it is found in the maps, or null.
	 */
	private Object get(String name, Class<?> type) {
		if (Event.class.isAssignableFrom(type)) {
			return allEvents.get(name);
		} else if (State.class.isAssignableFrom(type)) {
			return allStates.get(name);
		} else if (Action.class.isAssignableFrom(type)) {
			throw new NoMapForBaseTypeException(
					"No map for subclasses of Action: " + type);
		} else {
			throw new UnknownBaseTypeException("Unknown class: "
					+ type.getName());
		}
	}

	/**
	 * Gets the FSM's current state.
	 * 
	 * @return current state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Checks if the FSM is in the given state.
	 * 
	 * @param state
	 * @return true if the FSM is in this state.
	 */
	public boolean isState(State state) {
		return this.state == state;
	}

	/**
	 * Checks if the FSM is in one of the given states.
	 * 
	 * @param states
	 * @return true if the FSM is in one of the given states.
	 */
	public boolean isStateIn(State... states) {
		for (State state : states)
			if (isState(state))
				return true;

		return false;
	}

	/**
	 * Creates a new rule.
	 * 
	 * @return new rule.
	 */
	public Rule rule() {
		Rule rule = new Rule();
		rules.add(rule);
		return rule;
	}

	/**
	 * Clears all rules and resets current state to null.
	 */
	public void reset() {
		rules.clear();
		state = null;
	}

	/**
	 * Processes occurrence of an event. Searches for an applicable rule, and
	 * runs the associated action.
	 * 
	 * @param isSoftEvent
	 *            If true, do not throw exception if no applicable rule is
	 *            found.
	 * @param event
	 * @param args
	 *            Optional objects to pass to the action
	 * @return state entered after applying the rule, or null if no applicable
	 *         rule found and isSoftEvent is true.
	 */
	public State event(boolean isSoftEvent, Event event, Object... args) {
		LOGGER.info(toString() + ">> Received event: " + event);

		// TODO Replace this full-scan with a Map lookup
		for (Rule rule : rules) {
			if (rule.initialState == state && rule.event == event) {
				LOGGER.info(toString() + ">> Match: (" + rule.initialState
						+ "," + rule.event + ") <> (" + state + "," + event
						+ ")");

				state = rule.apply(args);

				LOGGER.info(toString() + ">> Resulting state: " + state);

				return state;
			} else {
				LOGGER.info(toString() + ">> Don't match: ("
						+ rule.initialState + "," + rule.event + ") <> ("
						+ state + "," + event + ")");
			}
		}

		if (isSoftEvent)
			return null;
		else
			throw new NoApplicableRuleException(
					"No applicable state/event combination: " + state + ", "
							+ event);
	}

	/**
	 * Processes occurrence of an event. Searches for an applicable rule, and
	 * runs the associated action.
	 * 
	 * @param event
	 * @return state entered after applying the rule
	 */
	public State event(Event event) {
		return event(event, (Object) null);
	}

	/**
	 * Processes occurrence of an event. Searches for an applicable rule, and
	 * runs the associated action.
	 * 
	 * @param event
	 * @param args
	 *            Optional objects to pass to the action
	 * @return state entered after applying the rule
	 */
	public State event(Event event, Object... args) {
		return event(false, event, args);
	}

	/**
	 * Processes occurrence of an event. Searches for an applicable rule, and
	 * runs the associated action. If no rule applies, returns null.
	 * 
	 * @param event
	 * @param args
	 *            Optional objects to pass to the action
	 * @return state entered after applying the rule
	 */
	public State softEvent(Event event, Object... args) {
		return event(true, event, args);
	}

	/**
	 * Forces the FSM's state. Normally only used when initially setting up the
	 * FSM. No rules checked and no actions triggered.
	 * 
	 * @param state
	 */
	public void start(State state) {
		this.state = state;
	}

	/**
	 * Gets a state object by its name.
	 * 
	 * @param name
	 *            state identifier
	 * @return matching state object or null
	 */
	public State findStateByName(String name) {
		return allStates.get(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
