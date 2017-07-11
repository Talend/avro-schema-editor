package org.talend.avro.schema.editor.viewer.attribute.column;

public class AttributeColumnConfig {

	private String attributeName;
	
	private String label;

	private boolean main;
	
	private int width;
	
	private boolean resizable;

	private int style;
	
	public AttributeColumnConfig(String attributeName, int width, boolean resizable, int style) {
		this(attributeName, attributeName, false, width, resizable, style);
	}
	
	public AttributeColumnConfig(String attributeName, boolean main, int width, boolean resizable, int style) {
		this(attributeName, attributeName, main, width, resizable, style);
	}
	
	public AttributeColumnConfig(String attributeName, String label, boolean main, int width, boolean resizable, int style) {
		super();
		this.attributeName = attributeName;
		this.label = label;
		this.main = main;
		this.width = width;
		this.resizable = resizable;
		this.style = style;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getLabel() {
		return label;
	}
	
	public boolean isMain() {
		return main;
	}

	public int getWidth() {
		return width;
	}

	public boolean isResizable() {
		return resizable;
	}

	public int getStyle() {
		return style;
	}
	
}
