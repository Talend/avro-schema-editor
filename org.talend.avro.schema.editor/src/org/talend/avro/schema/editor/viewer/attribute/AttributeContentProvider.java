package org.talend.avro.schema.editor.viewer.attribute;

import org.talend.avro.schema.editor.model.attributes.AvroAttribute;

/**
 * This interface provides all the {@link AvroAttribute} linked to the given element.
 * <p>
 * In the standard avro schema editor this input element should be an {@link AvroNode}.
 * <p>
 * This interface is used by the component {@kink AttributeViewer}. 
 *  
 * 
 * @author timbault
 * @see AttributeViewer
 *
 */
public interface AttributeContentProvider {

	/**
	 * Returns the avro attributes linked to the given element.
	 * 
	 * @param inputElement
	 * @return
	 */
	AvroAttribute<?>[] getAttributes(Object inputElement);
	
}
