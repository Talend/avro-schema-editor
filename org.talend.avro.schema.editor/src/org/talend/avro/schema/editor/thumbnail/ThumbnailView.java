package org.talend.avro.schema.editor.thumbnail;

import org.apache.avro.Schema;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.edit.AvroSchema;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.AvroSchemaText;
import org.talend.avro.schema.editor.edit.IEditorConfiguration;
import org.talend.avro.schema.editor.io.AvroSchemaFormatter;
import org.talend.avro.schema.editor.io.AvroSchemaGenerator;
import org.talend.avro.schema.editor.model.RootNode;

/**
 * Eclipse debug view responsible for displaying in text format the avro schema currently in edition in the active avro schema editor.
 * 
 * @author timbault
 *
 */
public class ThumbnailView extends ViewPart {

	public static final String ID = "org.talend.avro.schema.editor.thumbnail.ThumbnailView"; //$NON-NLS-1$
		
	private Text text;
	
	@Override
	public void createPartControl(Composite parent) {		
		
		text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		
	}		
	
	/**
	 * Get the avro schema from the active editor and display it in a text format.
	 *  
	 * @param editor
	 */
	public void downloadContent(AvroSchemaEditor editor) {
		
		/*
		Schema schema = SchemaBuilder
				.unionOf()
				.record("Author")
				.fields()
				.name("name").type().stringType().noDefault()
				.name("birthday").type().longType().noDefault()				
				.endRecord().and()
				.record("Book")
				.fields()				
				.name("title").type().stringType().noDefault()
				.name("author").type().unionOf()
				.type("Author").and()
				.stringType().endUnion()
				.noDefault()
				.endRecord().endUnion();
				*/
		
		RootNode rootNode = editor.getContext().getRootNode();
		
		IEditorConfiguration editorConfiguration = editor.getEditorConfiguration();		
		
		AvroSchemaGenerator schemaGenerator = editorConfiguration.getGenerator(editor.getContext());
		Schema schema = schemaGenerator.generate(rootNode);
		
		String content = schema == null ? "" : schema.toString(true);
		
		text.setText(content);
		
	}
	
	/**
	 * Set the current view content as input of the active avro schema editor.
	 *  
	 * @param editor
	 */
	public void uploadContent(AvroSchemaEditor editor) {
		String content = text.getText().trim();
		if (!content.isEmpty()) {
			AvroSchema avroSchema = new AvroSchemaText(ID, content);
			editor.setInput(avroSchema);
		}
	}
	
	/**
	 * Format the current view content.
	 */
	public void format() {
		String content = text.getText().trim();
		if (!content.isEmpty()) {
			AvroSchemaFormatter formatter = new AvroSchemaFormatter();		
			String formattedContent = formatter.format(content);		
			text.setText(formattedContent);
		}
	}
	
	/**
	 * Clear the view content.
	 */
	public void clear() {
		text.setText("");
	}
	
	@Override
	public void setFocus() {
		text.setFocus();
	}
	
	public void dispose() {
		super.dispose();
    }

}
