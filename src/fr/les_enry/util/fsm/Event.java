package fr.les_enry.util.fsm;

import java.util.HashMap;
import java.util.Map;

public class Event {

	private static int lastId = 0;
	private final int id;
	private final String name;
	
	private static final Map<String, Event> allEvents = new HashMap<String, Event>();
	
	private Event(String name) {
		id = ++lastId;
		this.name = name;

		allEvents.put(name, this);
	}

	public static Event build(String name) {
		Event Event = allEvents.get(name);
		return Event == null ? new Event(name) : Event;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Event) obj).id;
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
