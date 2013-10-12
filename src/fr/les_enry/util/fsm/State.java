package fr.les_enry.util.fsm;

import java.util.HashMap;
import java.util.Map;

public class State extends BaseType {
	private static final Map<String, State> allStates = new HashMap<String, State>();
	
	private State(String name) {
		super(name);

		allStates.put(name, this);
	}

	public static State build(String name) {
		State State = allStates.get(name);
		return State == null ? new State(name) : State;
	}

}
