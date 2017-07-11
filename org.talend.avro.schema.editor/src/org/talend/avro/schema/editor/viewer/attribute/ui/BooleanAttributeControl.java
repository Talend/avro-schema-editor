package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

/**
 * Build controls for an attribute of type Boolean. 
 * 
 * @author timbault
 *
 */
public class BooleanAttributeControl extends BaseAttributeControl<Boolean> {

	private Button button;
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<Boolean> element, AvroContext context) {
		
		initialize(element, context);
		
		button = toolkit.createButton(parent, "", SWT.CHECK);	
		
		update();
		
		button.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeAttribute(getAttribute().getValue(), button.getSelection());
			}
		});
		
	}

	@Override
	public void setLayoutData(Object layoutData) {
		doLayoutData(layoutData, button);
	}

	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
	}

	@Override
	protected void doUpdate() {
		button.setText(getLabel(getAttribute()));
		button.setSelection(getAttribute().getValue());
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}
	
}
