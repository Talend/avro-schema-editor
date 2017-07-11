package org.talend.avro.schema.editor.viewer.attribute.view;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.services.AbstractEditorService;

public class AttributeViewServiceImpl extends AbstractEditorService implements AttributeViewService {
	
	private AttributeView attributeView;
	
	@Override
	public void init(AvroSchemaEditor editor) {
		super.init(editor);
	}
	
	private AttributeView showAttributeView() {
		IViewPart view = null;
		try {
			view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(AttributeView.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
			return null;
		}
		if (view instanceof AttributeView) {
			return (AttributeView) view;
		}
		return null;
	}

	@Override
	public void attachToView() {
		AttributeView attributeView = showAttributeView();
		if (attributeView != null) {
			attributeView.attachToView(getEditor());
		}
	}
	
	@Override
	public boolean isAttached() {		
		return attributeView != null;
	}

	@Override
	public void detachFromView() {
		if (attributeView != null) {
			attributeView.detachFromView(getEditor());
			attributeView = null;
		}
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

}
