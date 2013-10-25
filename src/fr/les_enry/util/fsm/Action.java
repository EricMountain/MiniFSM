package fr.les_enry.util.fsm;

public abstract class Action extends BaseType {
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
