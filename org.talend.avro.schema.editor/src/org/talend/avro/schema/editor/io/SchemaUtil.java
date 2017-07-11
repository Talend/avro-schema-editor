package org.talend.avro.schema.editor.io;

import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.avro.schema.editor.model.PrimitiveType;

/**
 * This class provides some convenient methods around the {@link Schema} avro API.
 * 
 * @author timbault
 *
 */
public class SchemaUtil {

	public static PrimitiveType getSinglePrimitiveTypeOfUnion(Schema fieldSchema) {
		PrimitiveType primitiveType = null;
		List<Schema> unionTypes = fieldSchema.getTypes();
		for (Schema unionType : unionTypes) {
			if (unionType.getType() != Type.NULL) {
				if (PrimitiveType.isPrimitive(unionType)) {
					if (primitiveType == null) {
						primitiveType = PrimitiveType.getPrimitiveType(unionType);
					} else {
						primitiveType = null;
						break;
					}
				} 
			}
		}
		return primitiveType;
	}
		
	public static boolean unionHasNullChild(Schema unionSchema) {
		return unionHasTypedChild(unionSchema, Type.NULL);
	}
	
	public static boolean unionHasTypedChild(Schema unionSchema, Type type) {
		List<Schema> unionTypes = unionSchema.getTypes();
		for (Schema unionType : unionTypes) {
			if (unionType.getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isOptionalUnion(Schema unionSchema) {
		return unionHasNullChild(unionSchema);
	}
	
	public static boolean isMultiChoiceUnion(Schema unionSchema) {
		List<Schema> unionTypes = unionSchema.getTypes();
		int nbrOfNoNullChild = 0;
		for (Schema unionType : unionTypes) {
			if (unionType.getType() != Type.NULL) {
				nbrOfNoNullChild++;
			}
		}
		return nbrOfNoNullChild > 1;
	}
	
}
