package fr.les_enry.util.fsm;

import java.io.Serializable;

/**
 * Represents an FSM action.
 */
public abstract class Action extends BaseType implements Serializable {
	
	private static final long serialVersionUID = -7362524484682125752L;

	public Action() {
		super();
	}
	
	public boolean act() {
		return false;
	}
	
	public boolean act(Object... args) {
		return act();
	}
}
