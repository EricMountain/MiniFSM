package fr.les_enry.util.fsm;

import java.util.HashMap;
import java.util.Map;

public class Event extends BaseType {
	private static final Map<String, Event> allEvents = new HashMap<String, Event>();
	
	private Event(String name) {
		super(name);

		allEvents.put(name, this);
	}

	public static Event build(String name) {
		Event Event = allEvents.get(name);
		return Event == null ? new Event(name) : Event;
	}

}
