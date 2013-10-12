package fr.les_enry.util.fsm;

import java.util.HashMap;
import java.util.Map;

public class State {

	private static int lastId = 0;
	private final int id;
	private final String name;
	
	private static final Map<String, State> allStates = new HashMap<String, State>();
	
	private State(String name) {
		id = ++lastId;
		this.name = name;

		allStates.put(name, this);
	}

	public static State build(String name) {
		State state = allStates.get(name);
		return state == null ? new State(name) : state;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((State) obj).id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return name + "[" + id + "]";
	}
}
