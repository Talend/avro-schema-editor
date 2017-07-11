package org.talend.avro.schema.editor.edit.dnd;

import org.talend.avro.schema.editor.context.AvroContext;

/**
 * Implementation of the drag and drop behavior for an array or map element. 
 * 
 * @author timbault
 *
 */
public class MoveArrayOrMapHandler extends AbstractMoveHandler {
	
	public MoveArrayOrMapHandler(AvroContext context) {
		super(context);
	}
	
}
