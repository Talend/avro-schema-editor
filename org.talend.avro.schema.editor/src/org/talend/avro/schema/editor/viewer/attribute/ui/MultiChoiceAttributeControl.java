package org.talend.avro.schema.editor.viewer.attribute.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.MultiChoiceValue;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurationConstants;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfigurations;
import org.talend.avro.schema.editor.viewer.attribute.config.MultiChoiceValueContentProvider;

/**
 * Build controls for an attribute of type {@link MultiChoiceValue}.
 * 
 * @author timbault
 *
 * @param <T>
 */
public class MultiChoiceAttributeControl<T> extends BaseAttributeControl<MultiChoiceValue<T>>{

	private Label label;
	
	private Composite buttonsComposite;
	
	private Button[] buttons;
	
	private Combo combo;
	
	@Override
	public void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<MultiChoiceValue<T>> element,
			AvroContext context) {
		
		initialize(element, context);
		
		label = toolkit.createLabel(parent, "", SWT.NONE);
		
		if (useCombo()) {
			createAsCombo(parent, toolkit);
		} else {
			createAsRadioButtons(parent, toolkit);
		}				
		
	}
	
	protected boolean useCombo() {
		if (hasConfiguration(AttributeControlConfigurations.MULTI_CHOICE_VALUE_STYLE)) {
			String multiChoiceValueStyle = getConfiguration(AttributeControlConfigurations.MULTI_CHOICE_VALUE_STYLE, String.class);
			if (AttributeControlConfigurations.COMBO.equals(multiChoiceValueStyle)) {
				return true;
			} else if (AttributeControlConfigurations.RADIO_BUTTONS.equals(multiChoiceValueStyle)) {
				return false;
			} else {
				throw new IllegalArgumentException("Invalid multi choice value style");
			}
		}
		return true;
	}
	
	protected boolean isCombo() {
		return combo != null;
	}
	
	protected void createAsCombo(Composite parent, FormToolkit toolkit) {
		
		T[] displayedValues = getDisplayedValues();
		String[] valuesAsString = getValuesAsString(displayedValues);
		
		combo = new Combo(parent, SWT.READ_ONLY);
		toolkit.adapt(combo);
		combo.setItems(valuesAsString);
		
		update();
		
		combo.addSelectionListener(new SelectionAdapter() {		
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				T selectedValue = getSelectedValueFromCombo(combo.getSelectionIndex());
				changeAttribute(getCurrentValue(), getNewValue(selectedValue));				
			}
			
		});
		
	}
	
	protected void createAsRadioButtons(Composite parent, FormToolkit toolkit) {
		
		T[] allValues = getAllValues();
		String[] valuesAsString = getValuesAsString(allValues);
		
		buttonsComposite = toolkit.createComposite(parent, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(valuesAsString.length, false));
		
		buttons = new Button[valuesAsString.length];
		
		for (int i = 0; i < valuesAsString.length; i++) {
			buttons[i] = toolkit.createButton(buttonsComposite, valuesAsString[i], SWT.RADIO);
			buttons[i].setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		}
		
		update();				
		
		for (int i = 0; i < buttons.length; i++) {
			final int index = i;
			buttons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					T selectedValue = getSelectedValueFromRadioButtons(index);
					changeAttribute(getCurrentValue(), getNewValue(selectedValue));	
				}
			});
		}
		
	}
	
	protected T[] getAllValues() {
		return getAttribute().getValue().getValues();
	}
	
	protected T[] getDisplayedValues() {
		T[] values = getAllValues();
		if (hasConfiguration(AttributeControlConfigurationConstants.MULTI_CHOICE_VALUE_CONTENT_PROVIDER)) {
			@SuppressWarnings("unchecked")
			MultiChoiceValueContentProvider<T> multiChoiceValueContentProvider = 
					getConfiguration(AttributeControlConfigurationConstants.MULTI_CHOICE_VALUE_CONTENT_PROVIDER, MultiChoiceValueContentProvider.class);
			values = multiChoiceValueContentProvider.getContent(getAttribute());
		}
		return values;
	}
	
	protected String[] getValuesAsString(T[] values) {	
				
		ILabelProvider labelProvider = getConfiguration(AttributeControlConfigurations.LABEL_PROVIDER, ILabelProvider.class);
		String[] valuesAsString = new String[values.length];
		for (int i = 0; i < valuesAsString.length; i++) {
			T val = values[i];
			valuesAsString[i] = labelProvider.getText(val);
		}
		
		return valuesAsString;
	}
	
	protected MultiChoiceValue<T> getCurrentValue() {
		return getAttribute().getValue();
	}
	
	protected T getSelectedValueFromCombo(int selectedIndex) {
		T[] displayedValues = getDisplayedValues();
		return displayedValues[selectedIndex];
	}
	
	protected T getSelectedValueFromRadioButtons(int selectedButton) {
		T[] allValues = getAllValues();
		return allValues[selectedButton];
	}
	
	protected MultiChoiceValue<T> getNewValue(T selectedValue) {
		MultiChoiceValue<T> multiChoiceValue = getAttribute().getValue();
		MultiChoiceValue<T> newValue = multiChoiceValue.getACopy();
		newValue.setValue(selectedValue);
		return newValue;
	}
	
	protected int getSelectedIndex() {
		if (isCombo()) {
			return combo.getSelectionIndex();
		} else {
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i].getSelection()) {
					return i;
				}
			}
			return -1;
		}
	}

	@Override
	public void setLayoutData(Object layoutData) {
		if (isCombo()) {
			doLayoutData(layoutData, label, combo);
		} else {
			doLayoutData(layoutData, label, buttonsComposite);
		}
	}

	@Override
	public void doUpdate() {
		updateLabel(label);			
		if (isCombo()) {
			updateCombo();
		} else {
			updateRadioButtonsSelection();
			updateRadioButtonsEnableState(true);
		}
	}	
	
	protected void updateRadioButtonsSelection() {
		MultiChoiceValue<T> multiChoiceValue = getAttribute().getValue();
		T value = multiChoiceValue.getValue();	
		int index = multiChoiceValue.getIndexOf(value);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setSelection(index == i);
		}		
	}
	
	protected void updateRadioButtonsEnableState(boolean enabled) {
		if (enabled) {
			T[] allValues = getAllValues();
			T[] displayedValues = getDisplayedValues();
			for (int i = 0; i < allValues.length; i++) {
				buttons[i].setEnabled(isContained(allValues[i], displayedValues));
			}
		} else {
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setEnabled(false);
			}
		}
	}
	
	protected boolean isContained(T value, T[] values) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	protected void updateCombo() {
		MultiChoiceValue<T> multiChoiceValue = getAttribute().getValue();
		T value = multiChoiceValue.getValue();	
		T[] values = getDisplayedValues();
		String[] valuesAsString = getValuesAsString(values);
		combo.setItems(valuesAsString);
		int index = -1;
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				index = i;
				break;
			}
		}
		combo.select(index);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (isCombo()) {
			combo.setEnabled(enabled);
		} else {
			updateRadioButtonsEnableState(enabled);
		}
	}

	@Override
	public void dispose() {
		// nothing do dispose
	}

}
