package org.talend.avro.schema.editor.viewer.attribute.config;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * Default implementation of a {@link TableConfiguration}.
 * 
 * @author timbault
 *
 */
public class DefaultTableConfigurationImpl implements TableConfiguration {
	
	public static final int DEFAULT_STYLE = SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
	
	private int style = SWT.NONE;
		
	private boolean headerVisible = true;
	
	private boolean linesVisible = true;
	
	public DefaultTableConfigurationImpl() {
		this(DEFAULT_STYLE, true, true);
	}
	
	public DefaultTableConfigurationImpl(int style) {
		this(style, true, true);
	}
	
	public DefaultTableConfigurationImpl(int style, boolean headerVisible, boolean linesVisible) {
		super();
		this.style = style;
		this.headerVisible = headerVisible;
		this.linesVisible = linesVisible;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	@Override
	public int getStyle() {
		return style;
	}
	
	public void setHeaderVisible(boolean headerVisible) {
		this.headerVisible = headerVisible;
	}

	@Override
	public boolean isHeaderVisible() {
		return headerVisible;
	}
	
	public void setLinesVisible(boolean linesVisible) {
		this.linesVisible = linesVisible;
	}

	@Override
	public boolean areLinesVisible() {
		return linesVisible;
	}

	@Override
	public CellLabelProvider getCellLabelProvider(String columnKey) {
		return new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(cell.getElement().toString());
			}
		};
	}

	@Override
	public ViewerComparator getComparator() {
		return new ViewerComparator();
	}	
	
}
