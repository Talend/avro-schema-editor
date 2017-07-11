package org.talend.avro.schema.editor.model;

/**
 * Defines a position relative to a reference node.
 * <p>
 * BEFORE = before the reference node
 * UPON = upon the reference node
 * AFTER = after the reference node
 * 
 * @author timbault
 *
 */
public enum TargetPosition {
	BEFORE, UPON, AFTER;
	
	/**
	 * Transforms 'before' into 'after' and 'after' into 'before'.
	 * 
	 * @return
	 */
	public TargetPosition reverse() {
		if (this == AFTER) {
			return BEFORE;
		} else if (this == BEFORE) {
			return AFTER;
		} else {
			return UPON;
		}
	}
		
	public static final TargetPosition[] ALL = TargetPosition.values();
	
	public static final TargetPosition[] BEFORE_AND_AFTER = new TargetPosition[] { BEFORE, AFTER };
	
}
