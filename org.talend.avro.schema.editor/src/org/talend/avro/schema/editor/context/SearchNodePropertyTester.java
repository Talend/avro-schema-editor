package org.talend.avro.schema.editor.context;

import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.handlers.SchemaEditorPropertyTester;

/** 
 * This property tester provides two properties (has previous & has next) on the search feature.
 * <p>
 * 
 * 
 * @author timbault
 *
 */
public class SearchNodePropertyTester extends SchemaEditorPropertyTester {

	public static final String NAME_SPACE = "org.talend.avro.schema.editor.search"; //$NON-NLS-1$
	
	public static final String ID = NAME_SPACE + ".SearchNodePropertyTester"; //$NON-NLS-1$
	
	public static final String HAS_PREVIOUS = "hasprevious"; //$NON-NLS-1$
	
	public static final String HAS_NEXT = "hasnext"; //$NON-NLS-1$
	
	public static final String[] PROPERTIES = new String[] {
			NAME_SPACE + "." + HAS_PREVIOUS,
			NAME_SPACE + "." + HAS_NEXT
	};
	
	@Override
	protected boolean test(Object receiver, String property, Object[] args, Object expectedValue,
			AvroSchemaEditor editor) {
		
		if (HAS_PREVIOUS.equals(property) || HAS_NEXT.equals(property)) {			
			SearchNodeContext searchNodeContext = editor.getContext().getSearchNodeContext();
			if (HAS_PREVIOUS.equals(property)) {
				return searchNodeContext.hasPrevious();
			} else {
				return searchNodeContext.hasNext();
			}
		}
		return false;
		
	}	
	
}
