package org.talend.avro.schema.editor.viewer.attribute;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;

/**
 * This interface is responsible for creation of the controls representing an {@link AvroAttribute}.
 * 
 * @author timbault
 *
 * @param <T> the type of the attribute's value.
 * @see AvroAttribute
 */
public interface AttributeControl<T> {

	/**
	 * Create the control representing the specified attribute.
	 * 
	 * @param parent
	 * @param attribute
	 * @param context
	 */
	void createControl(Composite parent, FormToolkit toolkit, AvroAttribute<T> attribute, AvroContext context);
	
	/**
	 * Set the configuration of the control.
	 * 
	 * @param configuration
	 */
	void setConfiguration(AttributeControlConfiguration configuration);
	
	/**
	 * Set layoutData of the underlying controls.
	 * 
	 * @param layoutData
	 */
	void setLayoutData(Object layoutData);
	
	/**
	 * Update/refresh the underlying controls. This method is called by {@link AttributeViewer} when refresh is needed.
	 */
	void update();
	
	/**
	 * This method allows to enable/disable the underlying controls.
	 * 
	 * @param enabled
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * Dispose any resources.
	 */
	void dispose();
	
}
