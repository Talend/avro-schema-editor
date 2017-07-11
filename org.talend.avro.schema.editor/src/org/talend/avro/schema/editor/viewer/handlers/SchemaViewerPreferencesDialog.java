package org.talend.avro.schema.editor.viewer.handlers;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.preferences.AvroSchemaEditorPreferences;
import org.talend.avro.schema.editor.preferences.IEditPreferencesService;
import org.talend.avro.schema.editor.view.IViewService;

public class SchemaViewerPreferencesDialog extends TrayDialog {

	private IViewService viewService;
	
	private IEditPreferencesService preferencesService;

	private Button showTypeButton;
	
	private Button showDocButton;
	
	private Button fullLengthDocButton;
	
	private Button showTooltipButton;
	
	private Combo imageVersionCombo;	
	
	private Rectangle bounds;
	
	public SchemaViewerPreferencesDialog(Shell shell, Rectangle bounds, IEditorServiceProvider serviceProvider) {
		super(shell);		
		this.bounds = bounds;
		this.viewService = serviceProvider.getService(IViewService.class);
		this.preferencesService = serviceProvider.getService(IEditPreferencesService.class);		
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setText("Schema Viewer Preferences");
		shell.setImage(AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.CONFIGURE));		
		shell.setBounds(bounds);
		super.configureShell(shell);
	}

	protected void update() {
		showTypeButton.setSelection(
				preferencesService.getBoolean(AvroSchemaEditorPreferences.SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_KEY));
		showDocButton.setSelection(
				preferencesService.getBoolean(AvroSchemaEditorPreferences.SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_KEY));
		fullLengthDocButton.setSelection(
				preferencesService.getInteger(AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_KEY) <= 0);
		showTooltipButton.setSelection(
				preferencesService.getBoolean(AvroSchemaEditorPreferences.SHOW_TOOLTIP_IN_SCHEMA_VIEWER_KEY));
		int imageVersion = preferencesService.getInteger(AvroSchemaEditorPreferences.ICONS_VERSION_KEY);
		imageVersionCombo.select(getSelectedIndex(imageVersion));
	}
	
	protected void refresh(Display display) {
		BusyIndicator.showWhile(display, new Runnable() {					
			@Override
			public void run() {
				viewService.refresh();
			}
		});
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		// create only OK button
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);		
	}
	
	@Override
	protected Control createDialogArea(final Composite parent) {	
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		showTypeButton = new Button(composite, SWT.CHECK);
		showTypeButton.setText("Show Type");
		showTypeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				preferencesService.store(
						AvroSchemaEditorPreferences.SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_KEY, 
						showTypeButton.getSelection());
				refresh(parent.getDisplay());				
			}
			
		});
		
		showDocButton = new Button(composite, SWT.CHECK);
		showDocButton.setText("Show Doc");
		showDocButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				preferencesService.store(
						AvroSchemaEditorPreferences.SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_KEY, 
						showDocButton.getSelection());
				refresh(parent.getDisplay());
			}
			
		});
		
		fullLengthDocButton = new Button(composite, SWT.CHECK);
		fullLengthDocButton.setText("Full Doc");
		fullLengthDocButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (fullLengthDocButton.getSelection()) {
					preferencesService.store(AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_KEY, 
							AvroSchemaEditorPreferences.DOC_FULL_LENGTH_IN_SCHEMA_VIEWER);
				} else {
					preferencesService.store(AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_KEY, 
							AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_DEFAULT_VALUE);
				}		
				refresh(parent.getDisplay());
			}
			
		});
		
		showTooltipButton = new Button(composite, SWT.CHECK);
		showTooltipButton.setText("Show Tooltip");
		showTooltipButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				preferencesService.store(
						AvroSchemaEditorPreferences.SHOW_TOOLTIP_IN_SCHEMA_VIEWER_KEY, 
						showTooltipButton.getSelection());
			}
			
		});
		
		Composite compo = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		compo.setLayout(layout);
		
		Label label = new Label(compo, SWT.NONE);
		label.setText("Icons");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		String[] imageVersions = getImageVersionItems();
		
		imageVersionCombo = new Combo(compo, SWT.READ_ONLY);
		imageVersionCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		imageVersionCombo.setItems(imageVersions);
		imageVersionCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedVersion = getSelectedVersion(imageVersionCombo.getSelectionIndex());
				preferencesService.store(AvroSchemaEditorPreferences.ICONS_VERSION_KEY, selectedVersion);
				refresh(parent.getDisplay());
			}
			
		});
		
		update();
		
		return composite;
	}
	
	protected int getSelectedIndex(int version) {
		int[] elementImageVersions = AvroSchemaEditorActivator.ELEMENT_IMAGE_VERSIONS;
		for (int i = 0; i < elementImageVersions.length; i++) {
			if (elementImageVersions[i] == version) {
				return i;
			}
		}
		return -1;
	}
	
	protected int getSelectedVersion(int index) {
		return AvroSchemaEditorActivator.ELEMENT_IMAGE_VERSIONS[index];
	}
	
	protected String[] getImageVersionItems() {
		int[] elementImageVersions = AvroSchemaEditorActivator.ELEMENT_IMAGE_VERSIONS;
		String[] items = new String[elementImageVersions.length];
		for (int i = 0; i < elementImageVersions.length; i++) {
			items[i] = getItem(elementImageVersions[i]);
		}
		return items;
	}
	
	protected String getItem(int version) {
		if (version <= 0) {
			return "default";
		} else {
			return "version " + version;
		}
	}
	
}
