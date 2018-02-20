package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.services.NotificationService;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.DefaultValue;
import org.talend.avro.schema.editor.utils.IValueChangeListener;
import org.talend.avro.schema.editor.utils.TextValidator;

/**
 * This build controls for an attribute of type DefaultValue. 
 * 
 * @author timbault
 *
 */
public class DefaultValueAttributeControl extends BaseAttributeControl<DefaultValue> {

	private Button button;
	
	private Composite textCompo;
	
	private Text text;

	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<DefaultValue> attribute,
			AvroContext context) {

		initialize(attribute, context);
		
		button = toolkit.createButton(parent, "", SWT.CHECK);
		
		textCompo = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		textCompo.setLayout(layout);
		
		text = toolkit.createText(textCompo, "", getTextStyle(SWT.BORDER));
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				changeAttribute(getAttribute().getValue(), new DefaultValue(button.getSelection(), getDefaultValueAsString()));
				getContext().getService(NotificationService.class).refresh(getAttribute().getHolder());
			}			
			
		});
		
		IInputValidator validator = getInputValidator();
		
		IValueChangeListener valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void onValueChange(String oldValue, String newValue) {
				changeAttribute(getDefaultValue(oldValue), getDefaultValue(newValue));
			}
			
		};
		
		new TextValidator(text, SWT.TOP | SWT.LEFT, validator, valueChangeListener);
		
	}
	
	@Override
	public void setLayoutData(Object layoutData) {
		doLayoutData(layoutData, button, textCompo);
		update();
	}

	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
		text.setEnabled(enabled);
	}
	
	protected DefaultValue getDefaultValue(String val) {
		return new DefaultValue(getAttribute().getValue().isDefined(), val);
	}
	
	protected String getDefaultValueAsString() {
		return getAttribute().getValue().getValue();
	}
	
	@Override
	protected void doUpdate() {
		button.setText(getLabel(getAttribute()));
		button.setSelection(getAttribute().getValue().isDefined());
		text.setText(getDefaultValueAsString());
		setControlVisible(textCompo, getAttribute().getValue().isDefined());
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}
	
}
