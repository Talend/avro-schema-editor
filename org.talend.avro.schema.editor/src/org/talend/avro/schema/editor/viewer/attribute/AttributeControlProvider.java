package org.talend.avro.schema.editor.viewer.attribute;

import org.eclipse.swt.widgets.Layout;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;

/**
 * This interface provides all the UI data needed to display an {@link AvroAttribute}.
 * 
 * @author timbault
 * @see AttributeViewer
 *
 */
public interface AttributeControlProvider {

	/**
	 * Provides the main layout used by the AttributeViewer.
	 * @return
	 */
	Layout getMainLayout();
	
	/**
	 * Provides the AttributeControl responsible for creation of the controls displaying the given attribute.  
	 * 
	 * @param attribute
	 * @return
	 */
	AttributeControl<?> getAttributeControl(AvroAttribute<?> attribute);
	
	/**
	 * Provides the layout data used by the attribute controls.
	 * 
	 * @param attribute
	 * @return
	 */
	Object getLayoutData(AvroAttribute<?> attribute);
	
	/**
	 * Provides some optional extra configuration for the given attribute.
	 * 
	 * @param attribute
	 * @return
	 */
	AttributeControlConfiguration getAttributeControlConfiguration(AvroAttribute<?> attribute);
	
	/**
	 * Indicates if the controls displaying the given attribute are visible.
	 * 
	 * @param attribute
	 * @return
	 */
	boolean isVisible(AvroAttribute<?> attribute);
	
}
