package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.CustomProperties;
import org.talend.avro.schema.editor.preferences.AvroSchemaEditorPreferences;
import org.talend.avro.schema.editor.preferences.IEditPreferencesService;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;
import org.talend.avro.schema.editor.viewer.attribute.config.CustomPropertiesTableConfigurationImpl;
import org.talend.avro.schema.editor.viewer.attribute.config.TableConfiguration;

/**
 * Build controls for an attribute of type {@link CustomProperties}.
 * 
 * @author timbault
 *
 */
public class CustomPropertiesAttributeControl extends BaseAttributeControl<CustomProperties> {
	
	private Composite leftComposite;
	
	private Composite rightComposite;
	
	private Label label;
	
	private Button button;	
	
	private TableViewer tableViewer;	
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<CustomProperties> element, AvroContext context) {
		
		initialize(element, context);
		
		leftComposite = toolkit.createComposite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		leftComposite.setLayout(layout);
		
		button = toolkit.createButton(leftComposite, "", SWT.PUSH);
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean expanded = getExpandedState();
				setExpandedState(!expanded);
				getContext().getService(NotificationService.class).refresh(getAttribute().getHolder());
			}			
			
		});
		
		label = toolkit.createLabel(leftComposite, getLabel(getAttribute()), SWT.NONE);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		rightComposite = toolkit.createComposite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		rightComposite.setLayout(layout);
		
		TableConfiguration tableConfig = new CustomPropertiesTableConfigurationImpl();
		if (hasConfiguration(AttributeControlConfigurations.TABLE_CONFIG)) {
			tableConfig = getConfiguration(AttributeControlConfigurations.TABLE_CONFIG, TableConfiguration.class);
		}
		
		tableViewer = new TableViewer(rightComposite, tableConfig.getStyle());
		
		Table table = tableViewer.getTable();
		
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		table.setHeaderVisible(tableConfig.isHeaderVisible());
		table.setLinesVisible(tableConfig.areLinesVisible());
		
		TableViewerColumn keyColumn = createTableViewerColumn(CustomProperties.KEY, 200);
		keyColumn.setLabelProvider(tableConfig.getCellLabelProvider(CustomProperties.KEY));
		
		TableViewerColumn valueColumn = createTableViewerColumn(CustomProperties.VALUE, 400);
		valueColumn.setLabelProvider(tableConfig.getCellLabelProvider(CustomProperties.VALUE));
			
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// 
			}
			
			@Override
			public void dispose() {
				// 
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((CustomProperties) inputElement).getKeyValues().toArray();
			}
		});
		
		tableViewer.setComparator(tableConfig.getComparator());
		
		tableViewer.setInput(getAttribute().getValue());
				
	}
	
	protected void setExpandedState(boolean expanded) {
		IEditPreferencesService preferencesService = getContext().getService(IEditPreferencesService.class);
		preferencesService.store(AvroSchemaEditorPreferences.getPropertiesExpandedKey(getAttribute().getName()),
				expanded);
	}
	
	protected boolean getExpandedState() {
		IEditPreferencesService preferencesService = getContext().getService(IEditPreferencesService.class);
		return preferencesService.getBoolean(AvroSchemaEditorPreferences.getPropertiesExpandedKey(getAttribute().getName()));
	}
	
	protected void updateButton() {
		if (getExpandedState()) {
			button.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.BUTTON_COLLAPSE));
			button.setToolTipText("Hide properties");
		} else {
			button.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.BUTTON_EXPAND));
			button.setToolTipText("Show properties");
		}
	}

	private TableViewerColumn createTableViewerColumn(String name, int width) {
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
		column.getColumn().setText(name);			
		column.getColumn().setResizable(true);
		column.getColumn().setWidth(width);
		return column;
	}
	
	@Override
	public void setLayoutData(Object layoutData) {
		doLayoutData(layoutData, leftComposite, rightComposite);
		update();
	}

	@Override
	public void setEnabled(boolean enabled) {
		tableViewer.getTable().setEnabled(enabled);
	}
	
	@Override
	protected void doUpdate() {
		updateButton();
		boolean expanded = getExpandedState();
		if (expanded) {
			// show table			
			rightComposite.setVisible(true);
			GridData layoutData = (GridData) rightComposite.getLayoutData();
			layoutData.exclude = false;
		} else {
			// hide table
			rightComposite.setVisible(false);
			GridData layoutData = (GridData) rightComposite.getLayoutData();
			layoutData.exclude = true;
		}
		label.setText(getLabel(getAttribute()));
		tableViewer.refresh();
		rightComposite.getParent().layout(true, true);
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}
	
}
