package fr.les_enry.util.fsm;


abstract class BaseType {

	private static int lastId = 0;
	private final int id;
	private final String name;
	
	protected BaseType(String name) {
		id = ++lastId;
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

}
