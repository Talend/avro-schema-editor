package org.talend.avro.schema.editor.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.log.AvroSchemaLogger;
import org.talend.avro.schema.editor.model.SchemaNode;
import org.talend.avro.schema.editor.viewer.SchemaViewer;

public final class UIUtils {

	public static IViewPart findView(String viewId) {		
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();		
		if (activeWorkbenchWindow != null) {			
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();			
			if (activePage != null) {				
				return activePage.findView(viewId);			
			}		
		}		
		return null;		
	}
	
	public static IViewPart showView(String viewId) {		
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();		
		if (activeWorkbenchWindow != null) {			
			IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();			
			if (activePage != null) {				
				try {
					return activePage.showView(viewId);
				} catch (PartInitException e) {					
					e.printStackTrace();
				}			
			}		
		}		
		return null;		
	}
	
	/**
     * @param runnable
     */
    public static void runSyncOrAsync(Runnable runnable) {
        if (isEventDispatchThread()) {
            runnable.run();
        }
        else {
            Display.getDefault().asyncExec(runnable);
        }
    }
	
    /**
     * Return true if the current thread is in the dispatchThread.
     * 
     * @return
     */
    public static boolean isEventDispatchThread() {
        return Display.getDefault().getThread() == Thread.currentThread();
    }
    
    public static void expandAll(SchemaViewer viewer, final String context) {
    	expandAll(viewer.getTreeViewer(), context);
    }
    
    public static void expandAll(final TreeViewer treeViewer, final String context) {
    	AvroSchemaLogger.logMsg(context + " BEGIN", false);
    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
			@Override
			public void run() {
				long time1 = System.currentTimeMillis();				
				try {		
					treeViewer.getTree().setRedraw(false);
					treeViewer.expandAll();
				} finally {
					treeViewer.getTree().setRedraw(true);
					long time2 = System.currentTimeMillis();
					long delay = time2 - time1;
					AvroSchemaLogger.logMsg(context + " END : " + delay + " ms", false);
				}
			}
		});
    }
 
    public static void expandAll(final TreeViewer treeViewer, final SchemaNode node) {
    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
			@Override
			public void run() {
				try {		
					treeViewer.getTree().setRedraw(false);					
					treeViewer.expandToLevel(node, AbstractTreeViewer.ALL_LEVELS);
				} finally {
					treeViewer.getTree().setRedraw(true);
				}
			}
		});
    }
    
    public static void collapseAll(final TreeViewer treeViewer, final SchemaNode node) {
    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
			@Override
			public void run() {
				try {		
					treeViewer.getTree().setRedraw(false);
					treeViewer.collapseToLevel(node, AbstractTreeViewer.ALL_LEVELS);
				} finally {
					treeViewer.getTree().setRedraw(true);					
				}
			}
		});
    }
    
    public static void collapseAll(SchemaViewer viewer) {
    	final TreeViewer treeViewer = viewer.getTreeViewer();
    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
			@Override
			public void run() {
				try {		
					treeViewer.getTree().setRedraw(false);
					treeViewer.collapseAll();
				} finally {
					treeViewer.getTree().setRedraw(true);					
				}
			}
		});
    }
    
    public static void refresh(final TreeViewer treeViewer, final String context) {
    	run(treeViewer, new Runnable() {			
			@Override
			public void run() {
				treeViewer.refresh();
			}
		}, context);
    }
    
    public static void run(final TreeViewer treeViewer, final Runnable runnable, final String context) {
    	AvroSchemaLogger.logMsg(context + " BEGIN", false);
    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
			@Override
			public void run() {
				long time1 = System.currentTimeMillis();				
				try {					
					treeViewer.getTree().setRedraw(false);
					runnable.run();
				} finally {
					treeViewer.getTree().setRedraw(true);
					long time2 = System.currentTimeMillis();
					long delay = time2 - time1;
					AvroSchemaLogger.logMsg(context + " END : " + delay + " ms", false);
				}
			}
		});
    }
    
//    public static void refresh(final IViewService viewService) {
//    	BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {				
//			@Override
//			public void run() {
//				String[] viewIds = viewService.getViewIds();
//				try {
//					for (String viewId : viewIds) {
//						IView view = viewService.getView(viewId);
//						if (view instanceof IDrawingView) {
//							((IDrawingView) view).setLockDraw(true);
//						}
//					}		
//					viewService.refresh();
//				} finally {
//					for (String viewId : viewIds) {
//						IView view = viewService.getView(viewId);
//						if (view instanceof IDrawingView) {
//							((IDrawingView) view).setLockDraw(false);
//						}
//					}	
//				}
//			}
//		});
//    }
    
    public static AvroSchemaEditor pickAvroSchemaEditorFromEditorParts() {
    	
    	AvroSchemaEditor editor = null;
    	
    	List<IEditorReference> schemaEditorRefList = new ArrayList<>();
		
		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for (IEditorReference editorRef : editorReferences) {
			IEditorPart editorPart = editorRef.getEditor(false);
			if (editorPart != null && editorPart instanceof IWithAvroSchemaEditor) {
				schemaEditorRefList.add(editorRef);
			}
		}
		
		if (schemaEditorRefList.size() == 1) {
			editor = ((IWithAvroSchemaEditor) schemaEditorRefList.get(0).getEditor(false)).getEditor();
		} else if (schemaEditorRefList.size() > 1) {
			// we have to ask the user to choose an editor
			// with a dialog
			ListDialog dialog = new ListDialog(Display.getCurrent().getActiveShell());
			dialog.setLabelProvider(new LabelProvider() {				
				@Override
				public String getText(Object element) {
					return ((IEditorReference) element).getPartName();
				}
			});
			dialog.setContentProvider(new ArrayContentProvider());
			dialog.setInput(schemaEditorRefList);
			int result = dialog.open();
			if (result == ListDialog.OK) {
				Object[] selectedEditor = dialog.getResult();
				IEditorReference selectedEditorRef = (IEditorReference) selectedEditor[0];
				editor = ((IWithAvroSchemaEditor) selectedEditorRef.getEditor(false)).getEditor();
			}
		}		
    	
		return editor;
		
    }
    
    public static String[] asString(boolean toLowerCase, Object... objects) {
    	String[] result = new String[objects.length];
    	for (int i = 0; i < result.length; i++) {
    		result[i] = toLowerCase ? objects[i].toString().toLowerCase() : objects[i].toString();
    	}
    	return result;
    }
    
}
