package org.talend.avro.schema.editor.utils;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class TextCellEditorWithValidation extends TextCellEditor {

    private ControlDecoration deco;

    private int decoStyle = SWT.LEFT | SWT.BOTTOM; 
    
    public TextCellEditorWithValidation(Composite parent) {
        super(parent);
    }
    
    public void setDecoStyle(int decoStyle) {
		this.decoStyle = decoStyle;
	}

	@Override
    protected Control createControl(Composite parent) {
        super.createControl(parent);
        deco = new ControlDecoration(text, decoStyle);      
        deco.setImage(getErrorImage());
        deco.hide();
        return text;
    }

    protected Image getErrorImage() {
    	ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        return sharedImages.getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
    }
    
    @Override
    public void deactivate() {
    	deco.hide();
        super.deactivate();        
    }

    @Override
    public void activate() {
        super.activate();
        isCorrect(getValue());
    }

    @Override
    protected boolean isCorrect(Object value) {
        boolean isCorrect = super.isCorrect(value);
        if (isCorrect) {
            deco.hide();
            deco.setDescriptionText(null);
        }
        else {
            deco.show();
            deco.setDescriptionText(getErrorMessage());
        }
        return isCorrect;
    }

    @Override
    public void dispose() {
    	deco.dispose();
        super.dispose();        
    }
    
}
