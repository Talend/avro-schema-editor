package org.talend.avro.schema.editor.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(content);

		} catch (IOException e) {

			AvroSchemaLogger.logMsg("ERROR " + e.getMessage(), false);

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				AvroSchemaLogger.logMsg("ERROR " + ex.getMessage(), false);

			}

			AvroSchemaLogger.logMsg("Save into file " + file.getName() + " FINISH", false);
			
		}
	}
	
	protected String getFileContent(File file) {
		try {
			byte[] allBytes = Files.readAllBytes(file.toPath());
			return new String(allBytes);
		} catch (IOException e1) {
			return null;
		} 
	}
	
}
