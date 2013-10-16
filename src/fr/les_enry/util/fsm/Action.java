package fr.les_enry.util.fsm;

import java.util.HashMap;
import java.util.Map;

public abstract class Action extends BaseType {
	private static final Map<String, Action> allActions = new HashMap<String, Action>();
	
	class AlreadyExistsException extends RuntimeException {
		public AlreadyExistsException(String message) {
			super(message);
		}

		/**
		 * Serialisation ID.
		 */
		private static final long serialVersionUID = 1L;
	}

	public Action(String name) {
		super(name);
		
		Action action = allActions.get(name);
		if (action == null) {
			allActions.put(name, this);
			
		} else {
			throw new AlreadyExistsException("Action already defined with this name: " + name);
		}
		
		
	}
	
	abstract boolean act();
}
