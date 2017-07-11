package org.talend.avro.schema.editor.edit;

/**
 * Base implementation of an {@link AvroSchema}.
 * <p>
 * @author timbault
 *
 */
public class AvroSchemaText implements AvroSchema {

	private String name; 
	
	private String content;
	
	public AvroSchemaText(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}	
	
}
