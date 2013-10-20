package fr.les_enry.util.fsm;

public abstract class Action extends BaseType {
	public Action() {
		super();
	}
	
	abstract public boolean act();
}
