package fr.les_enry.util.fsm;

import java.util.ArrayList;
import java.util.List;

public class FSM {

	private List<Rule> rules = new ArrayList<Rule>();
	
	private State state = null;
	
	class Rule {
		State initialState = null;
		State endState = null;
		Event event = null;
		
		
		Rule state(State state) {
			if (initialState == null)
				initialState = state;
			else
				endState = state;
			
			return this;
		}
		
		Rule event(Event event) {
			this.event = event;
			
			return this;
		}
		
	}
	
	FSM() {
		
	}
	
	void start(State state) {
		this.state = state;
	}
	
	State getState() {
		return state;
	}
	
	Rule rule() {
		Rule rule = new Rule();
		rules.add(rule);
		return rule;
	}
	
	State event(Event event) {
		for (Rule rule : rules) {
			if (rule.initialState == state && rule.event == event) {
				state = rule.endState;
				return state;
			}
				
		}
		
		// TODO Proper exception
		throw new RuntimeException("No state/event combo");
	}
}
