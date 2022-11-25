package org.talend.avro.schema.editor.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.avro.Schema;
import com.fasterxml.jackson.databind.JsonNode;

import org.talend.avro.schema.editor.log.AvroSchemaLogger;

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
 * Implementation of an {@link AvroSchema} which references a File.
 * <p>
 * @author timbault
 *
 */
public class AvroSchemaFile implements AvroSchema {

	
	/**
	 * A text file containing the schema.
	 */

	private File file;
	private AVRO_SCHEMA_FORMAT format;

	public AvroSchemaFile(File file) {
		super();
		this.file = file;
		if (getName().endsWith(".json")) {
			setFormat(AVRO_SCHEMA_FORMAT.JSON);
		}
		else {
			setFormat(AVRO_SCHEMA_FORMAT.AVSC);
		}
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public String getContent() {
		return getFileContent(file);
	}
	
	@Override
	public AVRO_SCHEMA_FORMAT getFormat() {
		return format;
	}

	@Override
	public void setFormat(AVRO_SCHEMA_FORMAT format) {
		this.format = format;
	}

	@Override
	public void setContent(String content) {
		
		AvroSchemaLogger.logMsg("Save into file " + file.getName() + " BEGIN", false);
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		try {
			
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8.name());
			bw = new BufferedWriter(osw);
			if (getFormat() == AVRO_SCHEMA_FORMAT.JSON) {
				bw.write(AvroSchemaText.ConvertAvscToJson(content));
			}
			else {
				bw.write(content);
			}
			
			
		} catch (IOException e) {			

			AvroSchemaLogger.logMsg("ERROR " + e.getMessage(), false);

		} catch (ProcessingException e) {
			AvroSchemaLogger.logMsg("ERROR " + e.getMessage(), false);
		} finally {

			try {

				if (bw != null) {
					bw.close();
				}
				

			} catch (IOException ex) {

				AvroSchemaLogger.logMsg("ERROR " + ex.getMessage(), false);

			}

			AvroSchemaLogger.logMsg("Save into file " + file.getName() + " FINISH", false);
			
		}
	}
	
	protected String getFileContent(File file) {
		AvroSchemaLogger.logMsg("getFileContent is called on " + file.getName(), false);
		try {
			byte[] allBytes = Files.readAllBytes(file.toPath());
			String content = new String(allBytes, StandardCharsets.UTF_8.name());
			if (getFormat() == AVRO_SCHEMA_FORMAT.JSON) {
				return AvroSchemaText.ConvertJsonToAvsc(content);
			}
			return content;
		}catch (ProcessingException e) {
			AvroSchemaLogger.logMsg("ProcessingException: " + e.getMessage(), false);
			return null;
		}catch (IOException e) {
			AvroSchemaLogger.logMsg("IOException: " + e.getMessage(), false);
			return null;
		} 
	}	
}
