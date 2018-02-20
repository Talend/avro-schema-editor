package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.validator.DefaultInputValidtor;
import org.talend.avro.schema.editor.model.attributes.validator.InputValidatorProvider;
import org.talend.avro.schema.editor.utils.IValueChangeListener;
import org.talend.avro.schema.editor.utils.TextValidator;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;

/**
 * Abstract implementation of an {@link AttributeControl} which uses a Text control to display and edit attribute.
 * 
 * @author timbault
 *
 * @param <T>
 */
public abstract class TextAttributeControl<T> extends BaseAttributeControl<T> {

	private Label label;
	
	private Text text;
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<T> element, AvroContext context) {
		
		initialize(element, context);
		
		label = toolkit.createLabel(parent, "", SWT.NONE);
		
		text = toolkit.createText(parent, "", getTextStyle(SWT.BORDER));
		
		update();
	
		IInputValidator validator = getInputValidator();
		
		IValueChangeListener valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void onValueChange(String oldValue, String newValue) {
				changeAttribute(parseValue(oldValue), parseValue(newValue));
			}
			
		};
		
		new TextValidator(text, SWT.TOP | SWT.LEFT, validator, valueChangeListener);
		
	}		
	
	protected abstract T parseValue(String value);
	
	protected String getTextValue() {
		return text.getText();
	}
	
	@Override
	public void setLayoutData(Object layoutData) {
		doLayoutData(layoutData, label, text);
	}

	protected abstract String getAttributeValueAsString();
	
	@Override
	public void doUpdate() {
		updateLabel(label);
		String value = getAttributeValueAsString();
		if (value == null) {
			text.setText("");
		} else {
			text.setText(value);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

}
