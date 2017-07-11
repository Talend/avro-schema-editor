package org.talend.avro.schema.editor.viewer.attribute.column;

import org.eclipse.swt.SWT;

/**
 * Base abstract implementation of an attribute column configuration.
 * <p>
 * Provides some convenient methods.
 * 
 * @author timbault
 *
 */
public abstract class AbstractAttributeColumnConfigurationImpl implements AttributeColumnConfiguration {

	public static final int DEFAULT_COLUMN_WIDTH = 100;
	
	public static final boolean DEFAULT_COLUMN_RESIZABLE = true;

	public static final int DEFAULT_COLUMN_STYLE = SWT.CENTER;
	
	private int defaultColumnWidth;
	
	private boolean defaultColumnResizable;
	
	private int defaultColumnStyle;
	
	protected static AttributeColumnConfig config(String attrName, int width, boolean resizable, int style) {
		return new AttributeColumnConfig(attrName, width, resizable, style);
	}

	protected static AttributeColumnConfig config(String attrName, boolean main, int width, boolean resizable, int style) {
		return new AttributeColumnConfig(attrName, main, width, resizable, style);
	}
	
	protected static AttributeColumnConfig config(String attrName, String label, int width, boolean resizable, int style) {
		return new AttributeColumnConfig(attrName, label, false, width, resizable, style);
	}
	
	protected static AttributeColumnConfig config(String attrName, String label, boolean main, int width, boolean resizable, int style) {
		return new AttributeColumnConfig(attrName, label, main, width, resizable, style);
	}
	
	protected AbstractAttributeColumnConfigurationImpl(int defaultColumnWidth, boolean defaultColumnResizable, int defaultColumnStyle) {
		super();
		this.defaultColumnWidth = defaultColumnWidth;
		this.defaultColumnResizable = defaultColumnResizable;
		this.defaultColumnStyle = defaultColumnStyle;
	}

	protected abstract AttributeColumnConfig[] getConfigs();
	
	@Override
	public boolean isMainColumn(String attributeName) {
		AttributeColumnConfig config = getConfig(attributeName, getConfigs());
		if (config != null) {
			return config.isMain();
		}
		return false;
	}
	
	@Override
	public String getColumnTitle(String attributeName) {
		AttributeColumnConfig config = getConfig(attributeName, getConfigs());
		if (config != null) {
			return config.getLabel();
		}
		return attributeName;
	}
	
	@Override
	public String[] getColumnAttributeNames() {
		return asStringArray(getConfigs());
	}
	
	@Override
	public int getColumnWidth(String attributeName) {
		AttributeColumnConfig config = getConfig(attributeName, getConfigs());
		if (config != null) {
			return config.getWidth();
		}
		return defaultColumnWidth;
	}
	
	@Override
	public int getColumnStyle(String attributeName) {
		AttributeColumnConfig config = getConfig(attributeName, getConfigs());
		if (config != null) {
			return config.getStyle();
		}
		return defaultColumnStyle;
	}

	@Override
	public boolean isResizable(String attributeName) {
		AttributeColumnConfig config = getConfig(attributeName, getConfigs());
		if (config != null) {
			return config.isResizable();
		}
		return defaultColumnResizable;
	}
	
	protected String[] asStringArray(AttributeColumnConfig[] configs) {
		String[] result = new String[configs.length];
		for (int i = 0; i < configs.length; i++) {
			result[i] = configs[i].getAttributeName();
		}
		return result;
	}
	
	protected AttributeColumnConfig getConfig(String attributeName, AttributeColumnConfig[] configs) {
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getAttributeName().equals(attributeName)) {
				return configs[i];
			}
		}
		return null;
	}
	
}
