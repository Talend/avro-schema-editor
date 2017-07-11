package org.talend.avro.schema.editor.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.ui.services.IEvaluationService;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.SearchNodeContext;
import org.talend.avro.schema.editor.context.SearchNodePropertyTester;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.IWithAvroSchemaEditor;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.NodeType;

/**
 * This class builds the controls for the search feature.
 * 
 * @author timbault
 *
 */
public class SearchNodeControl extends WorkbenchWindowControlContribution {

	public static final String ID = "org.talend.avro.schema.editor.viewer.tree.searchNode"; //$NON-NLS-1$	
	
    @Override
    protected Control createControl(Composite parent) {
    	
    	final NodeType[] selectedType = new NodeType[] { NodeType.RECORD };
    	
    	Composite compo = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.marginHeight = 0;
        layout.marginWidth = 5;
        compo.setLayout(layout);
    	
        Label label = new Label(compo, SWT.NONE);
        label.setText("Search:");
        
        final Combo combo = new Combo(compo, SWT.READ_ONLY);
        combo.setItems(getSearchableTypes());
        combo.select(0);
        GridData layoutData = new GridData();
        layoutData.widthHint = 50;
        combo.setLayoutData(layoutData);
        
        
        final Button refButton = new Button(compo, SWT.CHECK);
        refButton.setText("Ref");
        refButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        refButton.setSelection(false);       
        
        final Text text = new Text(compo, SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.widthHint = 150;
        text.setLayoutData(layoutData);
        
        text.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent modifyEvent) {            	                                  	
            	String pattern = text.getText();            	
            	search(pattern, selectedType[0], refButton.getSelection());                
            }
            
        });      
        
        combo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectedTypeIndex = combo.getSelectionIndex();
            	String[] searchableTypes = getSearchableTypes();
            	String selectedTypeStr = searchableTypes[selectedTypeIndex];
            	selectedType[0] = NodeType.getType(selectedTypeStr);
            	String pattern = text.getText();            	
            	search(pattern, selectedType[0], refButton.getSelection());         
			}
		});
        
        refButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		String pattern = text.getText();
        		search(pattern, selectedType[0], refButton.getSelection());              
        	}
		});
        
        return compo;
        
    }
    
    protected void search(String pattern, NodeType type, boolean withRef) {    	    	
    	IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
        IEditorPart editorPart = workbenchPage.getActiveEditor();
        if (editorPart instanceof IWithAvroSchemaEditor) {
        	AvroSchemaEditor schemaEditor = ((IWithAvroSchemaEditor) editorPart).getEditor();        	
        	AvroContext masterContext = schemaEditor.getContext().getMaster();
        	SearchNodeContext searchNodeContext = masterContext.getSearchNodeContext();
        	if (pattern == null || pattern.trim().isEmpty()) {
        		searchNodeContext.reset();
        	} else if (searchNodeContext.searchNodes(type, pattern, withRef)) {
        		AvroNode node = searchNodeContext.next();
        		schemaEditor.getContentPart()
        			.getSchemaViewer(AvroContext.Kind.MASTER)
        			.setSelection(new StructuredSelection(node), true);
        	}
        	refreshCommands(editorPart, SearchNodePropertyTester.PROPERTIES);
        }
    }    
    
    protected void refreshCommands(IEditorPart editorPart, String... properties) {
		IEvaluationService evalService = (IEvaluationService) editorPart.getEditorSite().getService(IEvaluationService.class);
		if (evalService != null) {
			for (String property: properties) {
				evalService.requestEvaluation(property);
			}
		}        
    }
    
    protected String[] getSearchableTypes() {
    	List<String> result = new ArrayList<>();
    	for (NodeType type : NodeType.values()) {
    		if (type.hasFullName() && !type.isRef()) {
    			result.add(type.toString());
    		}
    	}
    	return result.toArray(new String[result.size()]);
    }
    
}
