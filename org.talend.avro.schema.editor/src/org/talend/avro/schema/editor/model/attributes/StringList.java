package org.talend.avro.schema.editor.model.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringList {

	private List<String> values = new ArrayList<>();	
	
	public StringList() {
		super();
	}
	
	public StringList(List<String> values) {
		super();
		this.values.addAll(values);
	}

	public void apply(StringList stringList) {
		values.clear();
		values.addAll(stringList.getValues());
	}
	
	public StringList getACopy() {
		return new StringList(values);
	}
	
	public void addValue(String value) {
		values.add(value);
	}
	
	public void insertValue(String value, int index) {
		values.add(index, value);
	}
	
	public void changeValue(int index, String value) {
		values.remove(index);
		values.add(index, value);
	}
	
	public void moveUp(String value) {
		int index = values.indexOf(value);
		if (index > 0) {
			index--;
			values.remove(value);
			values.add(index, value);
		}
	}
	
	public void moveDown(String value) {
		int index = values.indexOf(value);
		if (index < values.size() - 1) {
			index++;
			values.remove(value);
			values.add(index, value);
		}
	}
	
	public void removeValue(String value) {
		values.remove(value);
	}
	
	public boolean contains(String value) {
		return values.contains(value);
	}
	
	public void setValues(String values, String delimiter) {
		clear();
		String[] valuesArray = values.split(delimiter);
		for (String value : valuesArray) {
			addValue(value);
		}
	}
	
	public void setValues(Collection<String> values) {
		clear();
		this.values.addAll(values);
	}
	
	public List<String> getValues() {
		return Collections.unmodifiableList(values);
	}
	
	public Set<String> getValuesAsSet() {
		return new HashSet<>(values);
	}
	
	public String[] getValuesAsArray() {
		return values.toArray(new String[values.size()]);
	}
	
	public Item[] getValuesAsItems() {
		Item[] items = new Item[values.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = new Item(this, i, values.get(i));
		}
		return items;
	}
	
	public int getSize() {
		return values.size();
	}
	
	public void clear() {
		values.clear();
	}
	
	public static class Item {
		
		private StringList owner;
		
		private int index;
		
		private String value;

		public Item(StringList owner, int index, String value) {
			super();
			this.owner = owner;
			this.index = index;
			this.value = value;
		}
		
		public StringList getOwner() {
			return owner;
		}

		public int getIndex() {
			return index;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}
	
}
