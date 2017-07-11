package org.talend.avro.schema.editor.studio.attributes;

public enum StudioSchemaType {
	BOOLEAN("Boolean"),
	BYTE("Byte"),
	BYTE_ARRAY("byte[]"), 
	CHAR("Character"),
	DATE("Date"),
	BIG_DECIMAL("BigDecimal"),
	INT("Int"),
	LONG("Long"),
	OBJECT("Object"),
	STRING("String"),
	LIST("List"),
	VECTOR("Vector");
	
	private String label;

	private StudioSchemaType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
}
