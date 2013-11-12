package fr.les_enry.util.fsm;

import java.io.Serializable;


abstract class BaseType implements Serializable {

	private static final long serialVersionUID = -5901944619472256851L;

	private static int lastId = 0;
	
	private final int id;
	private String name;
	
	BaseType() {
		id = ++lastId;
		name = "not set";
	}
	
	protected BaseType(String name) {
		this();
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((BaseType) obj).id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return name + "[" + id + "]";
	}

	public String getName() {
		return name;
	}
	
	BaseType setName(String name) {
		this.name = name;
		return this;
	}
}
