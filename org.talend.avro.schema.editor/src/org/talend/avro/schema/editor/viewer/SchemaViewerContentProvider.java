package org.talend.avro.schema.editor.viewer;

import java.util.List;

import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.SchemaContentProvider;

/**
 * Describes the content of an avro schema.
 * 
 * @author timbault
 *
 */
public interface SchemaViewerContentProvider extends SchemaContentProvider {

	List<AvroNode> getNodesToRefresh(AvroNode node);
	
}
