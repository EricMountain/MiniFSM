package fr.les_enry.util.fsm;

public abstract class Action extends BaseType {
	Action() {
		super();
	}
	
	abstract boolean act();
}
