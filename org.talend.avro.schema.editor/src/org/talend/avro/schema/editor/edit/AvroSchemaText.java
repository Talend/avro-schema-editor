package org.talend.avro.schema.editor.edit;

import java.io.IOException;

import org.apache.avro.Schema;
import org.talend.avro.schema.editor.log.AvroSchemaLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.avro.MutableTree;
import com.github.fge.avro.translators.AvroTranslators;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.jsonschema2avro.AvroWriterProcessor;

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

	@Override
	public AVRO_SCHEMA_FORMAT getFormat() {
		// AvroSchemaText should be always shown as AVSC format
		return AVRO_SCHEMA_FORMAT.AVSC;
	}

	@Override
	public void setFormat(AVRO_SCHEMA_FORMAT type) {
		// do nothing
	}
	
	/**
	 * 
	 * @param input Json formatted schema
	 * @return Avro formatted schema
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public static String ConvertJsonToAvsc(String input) throws IOException, ProcessingException {
		AvroSchemaLogger.logMsg("Converting Json formatted schema to Avro formatted schema.", false);
		JsonNode jsonSchema = JsonLoader.fromString(input);
		final SchemaTree tree = new CanonicalSchemaTree(SchemaKey.forJsonRef(JsonRef.emptyRef()), jsonSchema);
		final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);
		final ProcessingReport report = new ListProcessingReport();
		AvroWriterProcessor processor = new AvroWriterProcessor();
		final Schema avroSchema = processor.process(report, holder).getValue();
		return avroSchema.toString(true);
	}
	
	/**
	 * 
	 * @param input Avro formatted schema
	 * @return Json formatted schema
	 * @throws ProcessingException
	 */
	public static String ConvertAvscToJson(String input) throws ProcessingException {
		Schema avroSchema = new Schema.Parser().parse(input);
		final MutableTree tree = new MutableTree();
		final ProcessingReport report = new ListProcessingReport();
        AvroTranslators.getTranslator(avroSchema.getType())
            .translate(avroSchema, tree, report);
        return (new CanonicalSchemaTree(SchemaKey.forJsonRef(JsonRef.emptyRef()), 
        		tree.getBaseNode())).toString();
	}
}
