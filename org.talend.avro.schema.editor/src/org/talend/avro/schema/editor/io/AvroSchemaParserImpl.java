package org.talend.avro.schema.editor.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.talend.avro.schema.editor.Defines;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.log.AvroSchemaLogger;
import org.talend.avro.schema.editor.model.ArrayNode;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.EnumNode;
import org.talend.avro.schema.editor.model.FieldNode;
import org.talend.avro.schema.editor.model.FixedNode;
import org.talend.avro.schema.editor.model.MapNode;
import org.talend.avro.schema.editor.model.Metadata;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.PrimitiveTypeNode;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.RefNodeImpl;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.registry.SchemaRegistry;
import org.talend.avro.schema.editor.statistics.NodeStats;
import org.talend.avro.schema.editor.utils.StringUtils;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Implementation of an {@link AvroSchemaParser}.
 * 
 * @author timbault
 *
 */
public class AvroSchemaParserImpl implements AvroSchemaParser {

	private AvroContext context;
	
	private SchemaAttributeInitializer schemaAttributeInitializer;
	
	private FieldAttributeInitializer fieldAttributeInitializer;
	
	private NodeStats stats = null;
	
	public AvroSchemaParserImpl(AvroContext context) {
		super();
		this.context = context;
		this.schemaAttributeInitializer = new SchemaAttributeInitializer(context);
		this.fieldAttributeInitializer = new FieldAttributeInitializer(context);
	}
	
	protected SchemaAttributeInitializer getSchemaAttributeInitializer() {
		return schemaAttributeInitializer;
	}

	protected FieldAttributeInitializer getFieldAttributeInitializer() {
		return fieldAttributeInitializer;
	}

	protected AvroContext getContext() {
		return context;
	}

	protected void populate(Schema schema, RootNode rootNode) {		
		stats.start();
		registerNode(rootNode);		
		parseSchema(schema, rootNode);				
		stats.finish();
		AvroSchemaLogger.logStats(stats, false);
	}
	
	protected void countNode(AvroNode node) {
		stats.count(node);
	}
	
	public RootNode parse(String content, String description) {
		
		String info = description == null ? "unknown" : description;
		
		stats = new NodeStats("Parse " + info + " content.");
		
		RootNode rootNode = new RootNode(context);
		rootNode.init(schemaAttributeInitializer);
		
		if (isContentValid(content)) {
			
			Schema schema = null;

			schema = new Schema.Parser().parse(content);

			if (schema != null) {
				populate(schema, rootNode);				
			}
			
		}
		
		rootNode = postParsing(rootNode);
		
		String desc = StringUtils.removeExtension(info, Defines.AVSC_FILE_EXTENSION);
		rootNode.addMetadata(Metadata.SCHEMA_DESCRIPTION, desc);
		
		return rootNode;		
	}
	
	protected RootNode postParsing(RootNode rootNode) {
		return rootNode;
	}
	
	protected boolean isContentValid(String content) {
		return content != null && !content.trim().isEmpty();
	}
			
	protected boolean isFileEmpty(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			if (br.readLine() == null) {
				// No errors, and file empty
				br.close();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return false;
	}
	
	protected AvroNode parseSchema(Schema schema, AvroNode parentNode) {
		
		AvroNode schemaNode = null;
		Type schemaType = schema.getType();
		
		switch (schemaType) {
		case RECORD:
			schemaNode = parseRecordSchema(schema, parentNode);
			break;
		case UNION:
			schemaNode = parseUnionSchema(schema, parentNode);
			break;
		case ENUM:
			schemaNode = parseEnumNode(schema, parentNode);
			break;
		case ARRAY:
			schemaNode = parseArrayNode(schema, parentNode);
			break;
		case FIXED:
			schemaNode = parseFixedNode(schema, parentNode);
			break;
		case MAP:
			schemaNode = parseMapNode(schema, parentNode);
			break;
		// Primitive types
		case NULL:
		case BOOLEAN:
		case INT:
		case LONG:
		case FLOAT:
		case DOUBLE:
		case BYTES:
		case STRING:
			schemaNode = parsePrimitiveTypeNode(schema, parentNode);
			break;
		default:
			break;
		}
		
		return schemaNode;
	}
	
	protected ArrayNode parseArrayNode(Schema schema, AvroNode parentNode) {
		ArrayNode arrayNode = new ArrayNode(context);
		schemaAttributeInitializer.setSchema(schema);
		arrayNode.init(schemaAttributeInitializer);
		linkNodes(parentNode, arrayNode);
		registerNode(arrayNode);
		Schema arrayItemSchema = schema.getElementType();
		if (!PrimitiveType.isPrimitive(arrayItemSchema)) { 
			// add child node representing the complex type
			parseSchema(arrayItemSchema, arrayNode);			
		}				
		return arrayNode;
	}
	
	protected MapNode parseMapNode(Schema schema, AvroNode parentNode) {
		MapNode mapNode = new MapNode(context);
		schemaAttributeInitializer.setSchema(schema);
		mapNode.init(schemaAttributeInitializer);
		linkNodes(parentNode, mapNode);
		registerNode(mapNode);
		Schema mapValueType = schema.getValueType();
		if (!PrimitiveType.isPrimitive(mapValueType)) { 
			// add child node representing the complex type
			parseSchema(mapValueType, mapNode);			
		}				
		return mapNode;
	}
	
	protected PrimitiveTypeNode parsePrimitiveTypeNode(Schema schema, AvroNode parentNode) {
		PrimitiveTypeNode primTypeNode = new PrimitiveTypeNode(context);
		schemaAttributeInitializer.setSchema(schema);
		primTypeNode.init(schemaAttributeInitializer);
		linkNodes(parentNode, primTypeNode);
		registerNode(primTypeNode);
		return primTypeNode;
	}
	
	protected AvroNode parseFixedNode(Schema schema, AvroNode parentNode) {
		
		schemaAttributeInitializer.setSchema(schema);
		
		String fixedFullName = getFullName(schema);
		
		SchemaRegistry schemaRegistry = context.getSchemaRegistry();
		
		if (schemaRegistry.isRegistered(NodeType.FIXED, fixedFullName)) {
			
			AvroNode fixedNode = schemaRegistry.getRegisteredNode(NodeType.FIXED, fixedFullName);
			RefNode refNode = new RefNodeImpl(fixedNode, context);
			refNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, refNode);
			registerNode(refNode);
			return refNode;
			
		} else {
		
			FixedNode fixedNode = new FixedNode(context);
			fixedNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, fixedNode);
			// no children
			registerNode(fixedNode);
			return fixedNode;
			
		}
		
	}
	
	protected AvroNode parseEnumNode(Schema schema, AvroNode parentNode) {
		
		schemaAttributeInitializer.setSchema(schema);
		
		String enumFullName = getFullName(schema);
		
		SchemaRegistry schemaRegistry = context.getSchemaRegistry();
		
		if (schemaRegistry.isRegistered(NodeType.ENUM, enumFullName)) {
			
			AvroNode enumNode = schemaRegistry.getRegisteredNode(NodeType.ENUM, enumFullName);
			RefNode refNode = new RefNodeImpl(enumNode, context);
			refNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, refNode);
			registerNode(refNode);
			return refNode;
			
		} else {
		
			EnumNode enumNode = new EnumNode(context);
			enumNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, enumNode);
			// no children
			registerNode(enumNode);
			return enumNode;
			
		}
		
	}
	
	protected UnionNode parseUnionSchema(Schema schema, AvroNode parentNode) {
		UnionNode unionNode = new UnionNode(context);
		// init
		schemaAttributeInitializer.setSchema(schema);
		unionNode.init(schemaAttributeInitializer);
		linkNodes(parentNode, unionNode);
		// register
		registerNode(unionNode);
		// create children
		List<Schema> types = schema.getTypes();
		for (Schema type : types) {
			parseSchema(type, unionNode);			
		}
		return unionNode;
	}
	
	protected void registerNode(AvroNode node) {
		countNode(node);
		SchemaRegistry schemaRegistry = context.getSchemaRegistry();
		if (schemaRegistry != null && 
				schemaRegistry.isRegistrable(node)) {
			schemaRegistry.register(node, false);
		}
	}
	
	protected String getFullName(Schema schema) {
		String name = schema.getName();
		String namespace = schema.getNamespace();
		// debug purpose
		//String fullName = schema.getFullName(); 
		return getFullName(namespace, name);
	}
	
	protected String getFullName(String namespace, String name) {
		return AttributeUtil.getFullName(namespace, name);
	}
	
	protected AvroNode parseRecordSchema(Schema schema, AvroNode parentNode) {
		
		schemaAttributeInitializer.setSchema(schema);
		
		String recordFullName = getFullName(schema);
		
		SchemaRegistry schemaRegistry = context.getSchemaRegistry();
		
		if (schemaRegistry.isRegistered(NodeType.RECORD, recordFullName)) {
			
			AvroNode recordNode = schemaRegistry.getRegisteredNode(NodeType.RECORD, recordFullName);
			RefNode refNode = new RefNodeImpl(recordNode, context);
			refNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, refNode);
			registerNode(refNode);
			return refNode;
			
		} else {
		
			RecordNode recordNode = new RecordNode(context);
			recordNode.init(schemaAttributeInitializer);
			linkNodes(parentNode, recordNode);
			registerNode(recordNode);

			// fields
			List<Field> fields = schema.getFields();
			for (Field field : fields) {
				parseField(field, recordNode);								
			}
		
			return recordNode;
		}
				
	}
	
	protected FieldNode parseField(Field field, AvroNode parentNode) {

		FieldNode fieldNode = new FieldNode(context);
		// init attributes
		fieldAttributeInitializer.setField(field);
		fieldNode.init(fieldAttributeInitializer);
		linkNodes(parentNode, fieldNode);
		registerNode(fieldNode);
		
		// type
		Schema fieldSchema = field.schema();
		if (!PrimitiveType.isPrimitive(fieldSchema)) {
			// add child node representing the complex type
			parseSchema(fieldSchema, fieldNode);			
		}		
		
		return fieldNode;
	}
		
	protected void linkNodes(AvroNode parentNode, AvroNode childNode) {
		parentNode.addChild(childNode);
		childNode.setParent(parentNode);
	}
	
}
