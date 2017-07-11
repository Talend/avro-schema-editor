package org.talend.avro.schema.editor.viewer.attribute.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.EditorLayout;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewer;

public class AttributeView extends ViewPart {

	public static final String ID = "org.talend.avro.schema.editor.viewer.attribute.AttributeView"; //$NON-NLS-1$
	
	private Composite mainComposite;
	
	private Composite visibleCompo;
	
	private Composite hiddenCompo;
	
	private IPartListener partListener;
	
	private IPerspectiveListener2 perspectiveListener;
	
	private Map<AvroSchemaEditor, AttributeViewer> editor2viewer = new HashMap<>();
	
	private AvroSchemaEditor currentEditor;
	
	private AttributeViewer currentAttributeViewer;
	
	@Override
	public void createPartControl(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		
		StackLayout stackLayout = new StackLayout();
		mainComposite.setLayout(stackLayout);
		
		visibleCompo = new Composite(mainComposite, SWT.NONE);		
		visibleCompo.setLayout(new FillLayout());
		
		hiddenCompo = new Composite(mainComposite, SWT.NONE);
		hiddenCompo.setLayout(new RowLayout());
		
		stackLayout.topControl = visibleCompo;
		
		initListeners();
		
	}
	
	public void attachToView(AvroSchemaEditor editor) {
		if (currentEditor != null && currentEditor != editor) {
			hideCurrent();
		}
		show(editor);
		visibleCompo.layout(true, true);
	}
	
	protected void hideCurrent() {
		currentAttributeViewer.getControl().setParent(hiddenCompo);
		currentEditor = null;
		currentAttributeViewer = null;
	}
	
	protected void show(AvroSchemaEditor editor) {
		AttributeViewer viewer = editor2viewer.get(editor);
		if (viewer == null) {
			viewer = editor.getContentPart().getAttributeViewer();
			editor2viewer.put(editor, viewer);
		}
		currentEditor = editor;
		currentAttributeViewer = viewer;
		currentAttributeViewer.getControl().setParent(visibleCompo);
	}
	
	public void detachFromView(AvroSchemaEditor editor) {
		AttributeViewer attributeViewer = editor2viewer.get(editor);
		if (attributeViewer == null) {
			throw new IllegalArgumentException("Unknown editor");
		}
		editor2viewer.remove(editor);
		if (currentEditor == editor) {
			hideCurrent();
		}		
		mainComposite.layout(true, true);		
	}
	
	@Override
	public void setFocus() {
		mainComposite.setFocus();
	}
	
	protected void initListeners() {
		if (partListener == null) {
			partListener = new IPartListener() {
				
				@Override
				public void partOpened(IWorkbenchPart part) {
					
				}
				
				@Override
				public void partDeactivated(IWorkbenchPart part) {
					
				}
				
				@Override
				public void partClosed(IWorkbenchPart part) {
					if (part instanceof IWithAvroSchemaEditor) {
						AvroSchemaEditor closedEditor = ((IWithAvroSchemaEditor) part).getEditor();
						AttributeViewer attributeViewer = editor2viewer.get(closedEditor);
						if (attributeViewer != null) {
							if (closedEditor == currentEditor) {
								hideCurrent();
							}
							// we have to dispose the viewer control since it is not disposed by the editor
							attributeViewer.getControl().dispose();
							editor2viewer.remove(closedEditor);
						}
					}
				}
				
				@Override
				public void partBroughtToTop(IWorkbenchPart part) {
					
				}
				
				@Override
				public void partActivated(IWorkbenchPart part) {
					if (part instanceof IWithAvroSchemaEditor) {
						AvroSchemaEditor activedEditor = ((IWithAvroSchemaEditor) part).getEditor();
						if (activedEditor != currentEditor) {
							AttributeViewer attributeViewer = editor2viewer.get(activedEditor);
							if (attributeViewer != null) {
								if (currentEditor != null) {
									hideCurrent();
								}
								show(activedEditor);
							}
						}
					}
				}
			};
		}
		getSite().getPage().addPartListener(partListener);
		
		if (perspectiveListener == null) {
			perspectiveListener = new IPerspectiveListener2() {
				
				@Override
				public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
					perspectiveChanged(page, perspective, page.getActivePartReference(), changeId);
				}
				
				@Override
				public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
					
				}
				
				@Override
				public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
						IWorkbenchPartReference partRef, String changeId) {
					if (IWorkbenchPage.CHANGE_VIEW_HIDE.equals(changeId)) {
						if (partRef.getId().equals(ID)) {
							detachAll();
						}
					}
				}
			};
		}
		getSite().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	protected void detachAll() {
		Set<AvroSchemaEditor> editors = new HashSet<>(editor2viewer.keySet());
		for (AvroSchemaEditor editor : editors) {
			EditorLayout editorLayout = editor.getContentPart().getEditorLayout();
			editor.getContentPart().setEditorLayout(editorLayout.getAttachedAttributeLayout());
		}
		editor2viewer.clear();
	}
	
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		getSite().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);
		super.dispose();
	}
	
}
