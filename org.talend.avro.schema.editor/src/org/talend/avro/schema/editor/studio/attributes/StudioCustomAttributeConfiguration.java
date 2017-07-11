package org.talend.avro.schema.editor.studio.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeImpl;
import org.talend.avro.schema.editor.model.attributes.AvroAttributeSet;
import org.talend.avro.schema.editor.model.attributes.AvroAttributes;
import org.talend.avro.schema.editor.model.attributes.BooleanAttribute;
import org.talend.avro.schema.editor.model.attributes.StringAttribute;
import org.talend.avro.schema.editor.model.attributes.custom.BooleanAttributeIO;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeIO;
import org.talend.avro.schema.editor.model.attributes.custom.StringAttributeIO;

public class StudioCustomAttributeConfiguration implements CustomAttributeConfiguration {

	private Map<InternalKey, CustomAttributeIO<?>> customAttrIOMap = new HashMap<>();
	
	public StudioCustomAttributeConfiguration() {
		super();
		init();
	}

	private void init() {
		customAttrIOMap.put(key(NodeType.FIELD, StudioAttributes.KEY), new BooleanAttributeIO());
		customAttrIOMap.put(key(NodeType.FIELD, StudioAttributes.NULLABLE), new BooleanAttributeIO());		
		customAttrIOMap.put(key(NodeType.FIELD, StudioAttributes.TYPE), new SchemaTypeAttributeIO());
		customAttrIOMap.put(key(NodeType.FIELD, StudioAttributes.DATE_FORMAT), new StringAttributeIO(false));
	}
	
	@Override
	public boolean isCustomAttribute(AvroContext context, NodeType type, String attributeName) {
		return type == NodeType.FIELD && StudioAttributes.isCustomAttribute(attributeName);
	}

	@Override
	public CustomAttributeIO<?> getCustomAttributeIO(AvroContext context, NodeType type, String attributeName) {
		CustomAttributeIO<?> customAttributeIO = customAttrIOMap.get(key(type, attributeName));
		if (customAttributeIO == null) {
			// use default custom attr io (string -> string)
			customAttributeIO = new StringAttributeIO();
		}
		return customAttributeIO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AvroAttribute<Object>[] configureAttributes(AvroContext context, AvroNode node, AvroAttributeSet attributes) {
		
		// first create the new attributes
		List<AvroAttribute<?>> customAttributes = new ArrayList<>();
		NodeType type = node.getType();
		
		if (type == NodeType.FIELD) {
			
			// create custom attributes
			
			// key
			AvroAttribute<Boolean> keyAttribute = new BooleanAttribute(node, StudioAttributes.KEY, false);
			customAttributes.add(keyAttribute);
			
			// type
			StudioSchemaTypes schemaTypeValue =
					new StudioSchemaTypes(StudioSchemaType.STRING);
			AvroAttributeImpl<StudioSchemaTypes> typeAttribute =
					new StudioTypeAttribute(node, StudioAttributes.TYPE, schemaTypeValue);
			customAttributes.add(typeAttribute);
			
			// nullable
			AvroAttribute<Boolean> nullableAttribute = new BooleanAttribute(node, StudioAttributes.NULLABLE, true);
			customAttributes.add(nullableAttribute);
			
			// date format
			AvroAttribute<String> dateFormatAttribute = new StringAttribute(node, StudioAttributes.DATE_FORMAT, "");
			dateFormatAttribute.setEnabled(false);
			customAttributes.add(dateFormatAttribute);
			
			// then reconfigure some existing attributes
			attributes.getAttribute(AvroAttributes.ALIASES).setVisible(false);
			attributes.getAttribute(AvroAttributes.PRIMITIVE_TYPE).setVisible(false);
			attributes.getAttribute(AvroAttributes.OPTIONAL).setVisible(false);
			//attributes.getAttribute(AvroAttributes.CHOICE_TYPE).setVisible(false);
			attributes.getAttribute(AvroAttributes.CUSTOM_PROPERTIES).setVisible(false);
		}		
		
		// finally return the new ones
		return customAttributes.toArray(new AvroAttribute[customAttributes.size()]);
	}	

	private static InternalKey key(NodeType type, String attrName) {
		return new InternalKey(type, attrName);
	}
	
	private static class InternalKey {
		
		private NodeType type;
		
		private String attributeName;

		public InternalKey(NodeType type, String attributeName) {
			super();
			this.type = type;
			this.attributeName = attributeName;
		}		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InternalKey other = (InternalKey) obj;
			if (attributeName == null) {
				if (other.attributeName != null)
					return false;
			} else if (!attributeName.equals(other.attributeName))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
		
	}
	
}
