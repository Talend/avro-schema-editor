package org.talend.avro.schema.editor.studio.services;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.edit.EditAttributeInitializer;
import org.talend.avro.schema.editor.io.AvroSchemaParserImpl;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.RecordNode;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.attributes.AttributeInitializer;

public class StudioSchemaParserImpl extends AvroSchemaParserImpl {

	private AttributeInitializer attributeInitializer;
	
	public StudioSchemaParserImpl(AvroContext context) {
		super(context);
		attributeInitializer = new EditAttributeInitializer(context) {

			@Override
			protected String getNodeName(NodeType type) {
				return "StudioSchemaRecord"; //$NON-NLS-1$
			} 
			
		};
	}	

	@Override
	protected RootNode postParsing(RootNode rootNode) {
		// if the schema is empty, add a record
		if (!rootNode.hasChildren()) {
			RecordNode recordNode = new RecordNode(getContext());	
			recordNode.init(attributeInitializer);
			linkNodes(rootNode, recordNode);
			registerNode(recordNode);
		}
		return rootNode;
	}		
	
}
