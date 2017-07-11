package org.talend.avro.schema.editor.viewer.attribute.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.commands.ICommandExecutor;
import org.talend.avro.schema.editor.commands.IEditCommand;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.Notifications;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.StringList;
import org.talend.avro.schema.editor.model.attributes.StringList.Item;
import org.talend.avro.schema.editor.model.attributes.cmd.IAttributeCommandFactory;
import org.talend.avro.schema.editor.utils.StringUtils;
import org.talend.avro.schema.editor.utils.TextCellEditorWithValidation;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;
import org.talend.avro.schema.editor.viewer.attribute.config.DefaultTableConfigurationImpl;
import org.talend.avro.schema.editor.viewer.attribute.config.TableConfiguration;

/**
 * Build controls for an attribute of type {@link StringList}.
 * 
 * @author timbault
 *
 */
public class StringListAttributeControl extends BaseAttributeControl<StringList> {

	private Composite leftComposite;
	
	private Composite rightComposite;
	
	private Label label;
	
	private TableViewer tableViewer;

	private ToolBarManager toolBarMgr;
	
	private StringListAction[] actions;
		
	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<StringList> element, AvroContext context) {
		
		initialize(element, context);
		
		leftComposite = toolkit.createComposite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		leftComposite.setLayout(layout);
		
		label = toolkit.createLabel(leftComposite, "", SWT.NONE);
		
		rightComposite = toolkit.createComposite(parent, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		rightComposite.setLayout(layout);
		
		Composite tableCompo = toolkit.createComposite(rightComposite, SWT.NONE);
		TableColumnLayout tableCompoLayout = new TableColumnLayout();
		tableCompo.setLayout(tableCompoLayout);
		tableCompo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableConfiguration tableConfig = new DefaultTableConfigurationImpl();
		if (hasConfiguration(AttributeControlConfigurations.TABLE_CONFIG)) {
			tableConfig = getConfiguration(AttributeControlConfigurations.TABLE_CONFIG, TableConfiguration.class);
		}
		
		tableViewer = new TableViewer(tableCompo, tableConfig.getStyle());
		
		Table table = tableViewer.getTable();
		
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		table.setHeaderVisible(tableConfig.isHeaderVisible());
		table.setLinesVisible(tableConfig.areLinesVisible());
		
		String attributeName = getAttribute().getName();
		
		TableViewerColumn valuesColumn = createTableViewerColumn(attributeName);
		tableCompoLayout.setColumnData(valuesColumn.getColumn(), new ColumnWeightData(100, true));
		
		valuesColumn.setLabelProvider(tableConfig.getCellLabelProvider(attributeName));
		valuesColumn.setEditingSupport(getEditingSupport(attributeName, tableViewer));
		
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
				return ((StringList) inputElement).getValuesAsItems();
			}
			
		});
						
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {				
				for (StringListAction action : actions) {
					action.setSelection((IStructuredSelection) event.getSelection());
				}
			}
			
		});
		
		tableViewer.setInput(getAttribute().getValue());
		
		toolBarMgr = new ToolBarManager(SWT.VERTICAL | SWT.FLAT | SWT.BEGINNING);
		
		actions = new StringListAction[4];
		String actionLabel = getLabel(AttributeControlConfigurations.ACTION_LABEL);
		actions[0] = new AddValueAction(actionLabel);
		actions[1] = new RemoveValueAction(actionLabel);
		actions[2] = new MoveUpAction(actionLabel);
		actions[3] = new MoveDownAction(actionLabel);
		
		for (StringListAction action : actions) {
			toolBarMgr.add(action);
		}
		
        ToolBar toolbar = toolBarMgr.createControl(rightComposite);
        toolkit.adapt(toolbar);
        GridData toolbarLayoutdata = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        toolbar.setLayoutData(toolbarLayoutdata);             
		
		update();
		
	}
	
	protected EditingSupport getEditingSupport(String columnKey, final ColumnViewer viewer) {
		return new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				StringList.Item item = (StringList.Item) element;
				String name = (String) value;
				if (!item.getValue().equals(name.trim())) {
					StringList owner = item.getOwner();
					StringList copy = owner.getACopy();
					copy.changeValue(item.getIndex(), name);
					IAttributeCommandFactory attributeCommandFactory = getContext().getService(IAttributeCommandFactory.class);
					IEditCommand cmd = attributeCommandFactory.createChangeAttributeCommand(getAttribute(), copy, Notifications.NOT_REF);
					getContext().getService(ICommandExecutor.class).execute(cmd);
				}
			}
			
			@Override
			protected Object getValue(Object element) {
				return ((StringList.Item) element).getValue();
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				final StringList.Item item = (StringList.Item) element;
				TextCellEditor textCellEditor = new TextCellEditorWithValidation(
						(Composite) viewer.getControl());
				textCellEditor.setValidator(new ICellEditorValidator() {
					@Override
					public String isValid(Object value) {	                    	
						String name = (String) value;
						if (!item.getValue().equals(name.trim())) {
							// check that the new name is not already used in the string list
							if (item.getOwner().contains(name.trim())) {
								return "Value already used";
							}
						}
						return null;
					}
				});
				return textCellEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
	}	

	private TableViewerColumn createTableViewerColumn(String name) {
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.CENTER);
		column.getColumn().setText(name);			
		column.getColumn().setResizable(true);
		return column;
	}

	@Override
	public void setLayoutData(Object layoutData) {
		doLayoutData(layoutData, leftComposite, rightComposite);
	}

	@Override
	public void setEnabled(boolean enabled) {
		tableViewer.getControl().setEnabled(enabled);
	}
	
	@Override
	protected void doUpdate() {
		updateLabel(label);
		updateTable();
		updateActions();
	}
	
	protected void updateActions() {
		for (StringListAction action : actions) {
			action.setValues(getAttribute().getValue());
		}
	}
	
	protected void updateTable() {
		//ISelection selection = tableViewer.getSelection();
		StringList input = (StringList) tableViewer.getInput();
		input.apply(getAttribute().getValue());
		tableViewer.refresh();
		//tableViewer.setSelection(selection);
	}
	
	@Override
	public void dispose() {
		// nothing to dispose
	}

	private interface StringListAction extends IAction {
		
		void setValues(StringList values);
		
		void setSelection(IStructuredSelection selection);
		
	}
	
	private abstract class AbstractStringListAction extends Action implements StringListAction {
		
		private String label;
		
		private IStructuredSelection selection;			
		
		private StringList values;
		
		protected AbstractStringListAction(String text, int style, String label) {
			super(text, style);
			this.label = label;
		}
		
		protected String getLabel() {
			return label;
		}

		@Override
		public void setValues(StringList values) {
			this.values = values;
		}
		
		protected StringList getValues() {
			return values;
		}

		@Override
		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
		}
		
		protected boolean isSingleSelection() {
			return selection != null && selection.size() == 1;
		}
		
		protected Object getSingleSelectedObject() {
			if (selection != null) {
				return selection.getFirstElement();
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		protected List<Object> getSelectedObjects() {
			if (selection != null) {
				return selection.toList();
			}
			return Collections.emptyList();
		}
		
	}

	private class MoveDownAction extends AbstractStringListAction {
		
		public MoveDownAction(String label) {
			super("Move down " + label, IAction.AS_PUSH_BUTTON, label);		
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.MOVE_DOWN);
		}

		@Override
		public String getToolTipText() {
			return "Move down " + getLabel();
		}
		
		@Override
		public void setSelection(IStructuredSelection selection) {
			super.setSelection(selection);
			setEnabled(!selection.isEmpty());
		}
		
		@Override
		public void run() {
			List<Object> selectedObjects = getSelectedObjects();
			StringList values = getValues().getACopy();
			for (int i = selectedObjects.size() - 1; i >= 0; i--) {				
				StringList.Item item = (Item) selectedObjects.get(i);
				values.moveDown(item.getValue());
			}
			changeAttribute(getValues(), values);
			// update here since the notification service will not do the job
			doUpdate();
		}
		
	}
	
	private class MoveUpAction extends AbstractStringListAction {
		
		public MoveUpAction(String label) {
			super("Move up " + label, IAction.AS_PUSH_BUTTON, label);		
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.MOVE_UP);
		}

		@Override
		public String getToolTipText() {
			return "Move up " + getLabel();
		}
		
		@Override
		public void setSelection(IStructuredSelection selection) {
			super.setSelection(selection);
			setEnabled(!selection.isEmpty());
		}
		
		@Override
		public void run() {
			List<Object> selectedObjects = getSelectedObjects();
			StringList values = getValues().getACopy();
			for (Object selectedObject : selectedObjects) {
				StringList.Item item = (Item) selectedObject;
				values.moveUp(item.getValue());
			}
			changeAttribute(getValues(), values);
			// update here since the notification service will not do the job
			doUpdate();
		}
		
	}
	
	private class RemoveValueAction extends AbstractStringListAction {
			
		public RemoveValueAction(String label) {
			super("Remove " + label, IAction.AS_PUSH_BUTTON, label);		
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.REMOVE_ELEMENT);
		}

		@Override
		public String getToolTipText() {
			return "Remove " + getLabel();
		}
		
		@Override
		public void setSelection(IStructuredSelection selection) {
			super.setSelection(selection);
			setEnabled(!selection.isEmpty());
		}
		
		@Override
		public void run() {
			List<Object> selectedObjects = getSelectedObjects();
			StringList values = getValues().getACopy();
			for (Object selectedObject : selectedObjects) {
				StringList.Item item = (Item) selectedObject;
				values.removeValue(item.getValue());
			}
			changeAttribute(getValues(), values);
			// update here since the notification service will not do the job
			doUpdate();
		}
		
	}
	
	private class AddValueAction extends AbstractStringListAction {
				
		public AddValueAction(String defaultName) {
			super("Add " + defaultName, IAction.AS_PUSH_BUTTON, defaultName);
		}

		@Override
		public ImageDescriptor getImageDescriptor() {
			return AvroSchemaEditorActivator.getImageDescriptor(AvroSchemaEditorImages.ADD_ELEMENT);
		}

		@Override
		public String getToolTipText() {
			return "Add " + getLabel();
		}
		
		@Override
		public void setSelection(IStructuredSelection selection) {
			super.setSelection(selection);
			setEnabled(selection.isEmpty() || selection.size() == 1);
		}

		@Override
		public void run() {
			StringList values = getValues().getACopy();
			if (isSingleSelection()) {
				StringList.Item selectedItem = (Item) getSingleSelectedObject();
				String name = selectedItem.getValue();
				String availableName = StringUtils.getAvailableName(name, "_", values.getValues());
				values.insertValue(availableName, selectedItem.getIndex());
			} else {				
				String availableName = StringUtils.getAvailableName(getLabel(), "_", values.getValues());
				values.addValue(availableName);								
			}
			changeAttribute(getValues(), values);
			// update here since the notification service will not do the job
			doUpdate();
		}
		
	}
	
}
