package org.talend.avro.schema.editor.context;

import java.util.List;

import org.talend.avro.schema.editor.context.services.IContextualServiceProvider;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RootNode;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.model.SchemaNodeRegistry;
import org.talend.avro.schema.editor.model.attributes.custom.CustomAttributeConfiguration;
import org.talend.avro.schema.editor.registry.SchemaRegistry;

/**
 * 
 * @author timbault
 *
 */
public interface AvroContext extends IContextualServiceProvider {
	
	public static final String ID = "org.talend.avro.schema.editor.context"; //$NON-NLS-1$
	
	public enum Kind {
		MASTER, SLAVE
	}
	
	Kind getKind();
	
	String getId();
	
	AvroContext getMaster();
	
	AvroContext getSlave();
	
	boolean isMaster();
	
	boolean isSlave();
	
	RootNode getRootNode();
	
	AvroNode getInputNode();
	
	List<AvroNode> getContextualNodes();
		
	List<SchemaNode> getSchemaNodes();
		
	@Deprecated
	String getAvailableName(NodeType nodeType);
		
	@Deprecated
	String getDefaultNameSpace();
	
	@Deprecated
	String getEnclosingNameSpace();
	
	@Deprecated
	PrimitiveType getDefaultPrimitiveType();
	
	SchemaRegistry getSchemaRegistry();
	
	SchemaNodeRegistry getSchemaNodeRegistry();
	
	CopyContext getCopyContext();
	
	SearchNodeContext getSearchNodeContext();
	
	CustomAttributeConfiguration getCustomAttributeConfiguration();
	
	void addContextListener(AvroContextListener listener);
	
	void removeContextListener(AvroContextListener listener);
	
	void dispose();
	
}
