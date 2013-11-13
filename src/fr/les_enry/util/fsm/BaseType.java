package fr.les_enry.util.fsm;

import java.io.Serializable;


abstract class BaseType implements Serializable {

	private static final long serialVersionUID = -5901944619472256851L;

	private String name;
	
	BaseType() {
		name = "not set";
	}
	
	protected BaseType(String name) {
		this();
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(((BaseType) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	BaseType setName(String name) {
		this.name = name;
		return this;
	}
}
