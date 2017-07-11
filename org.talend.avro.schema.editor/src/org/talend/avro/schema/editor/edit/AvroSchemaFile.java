package org.talend.avro.schema.editor.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.talend.avro.schema.editor.log.AvroSchemaLogger;

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

	public AvroSchemaFile(File file) {
		super();
		this.file = file;
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
	public void setContent(String content) {
		
		AvroSchemaLogger.logMsg("Save into file " + file.getName() + " BEGIN", false);
		
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		try {
			
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8.name());
			bw = new BufferedWriter(osw);
			bw.write(content);
			
		} catch (IOException e) {			

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
		try {
			byte[] allBytes = Files.readAllBytes(file.toPath());
			return new String(allBytes, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			return null;
		} 
	}
	
}
