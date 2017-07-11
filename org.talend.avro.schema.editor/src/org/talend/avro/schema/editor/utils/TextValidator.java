package org.talend.avro.schema.editor.utils;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;

public class TextValidator implements ModifyListener, KeyListener, FocusListener {

	private ControlDecoration deco;

    private IInputValidator validator;

    private IValueChangeListener valueChangeListener;
    
    private Image errorImg;
	
    private String validValue;
    
    public TextValidator(Text control, int position, IInputValidator validator, IValueChangeListener valueChangeListener) {
        this.validator = validator;
        this.valueChangeListener = valueChangeListener;
        this.deco = new ControlDecoration(control, position, control.getParent());
        this.deco.setMarginWidth(2);
        this.errorImg = AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.ERROR_OVERLAY);
        control.addModifyListener(this);
        control.addFocusListener(this);
        control.addKeyListener(this);
    }
    
    protected void validate() {
        Text text = (Text) deco.getControl();
        String value = text.getText();
        String errorMsg = validator.isValid(value);
        if (errorMsg == null) {
        	deco.hide();            
        }
        else {
        	deco.show();
            deco.setImage(errorImg);
            deco.setDescriptionText(errorMsg);
        }
    }
    
    protected boolean isValid() {
    	Text text = (Text) deco.getControl();
        String value = text.getText();
        return validator.isValid(value) == null;
    }
    
    protected String getCurrentValue() {
    	Text text = (Text) deco.getControl();
    	return text.getText();
    }
    
    protected void reset() {
    	Text text = (Text) deco.getControl();
    	text.setText(validValue);
    	deco.hide();
    }
    
	@Override
	public void focusGained(FocusEvent e) {
		validValue = getCurrentValue();
	}

	@Override
	public void focusLost(FocusEvent e) {
		applyChangeOrReset();
	}
	
	protected void applyChangeOrReset() {
		String currentValue = getCurrentValue();
		if (isValid() && !validValue.equals(currentValue)) {			
			valueChangeListener.onValueChange(validValue, currentValue);			
		} else {
			reset();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.character == '\r') { // Return key
			applyChangeOrReset();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// nothing to do
	}

	@Override
	public void modifyText(ModifyEvent e) {
		validate();
	}

}
