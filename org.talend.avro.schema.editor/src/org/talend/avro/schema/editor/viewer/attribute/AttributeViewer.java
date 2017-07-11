package org.talend.avro.schema.editor.viewer.attribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.model.attributes.AvroAttribute;
import org.talend.avro.schema.editor.viewer.attribute.config.AttributeControlConfiguration;

/**
 * This viewer is responsible for displaying the attributes of an {@link AvroNode}.
 * <p>
 * It is a "JFace like" component, i.e. it takes an avro node as input and uses providers to display the attributes of the input.
 * 
 * @author timbault
 *
 */
public class AttributeViewer {

	private Composite composite;	
	
	private ScrolledComposite scrolledComposite;
	
	private AttributeContentProvider contentProvider;
	
	private AttributeControlProvider attributeControlProvider;
	
	private Comparator<AvroAttribute<?>> attributeComparator;
	
	private Map<AvroAttribute<?>, AttributeControl<?>> attributeControlMap = new HashMap<>();
	
	private AvroContext context;
	
	private FormToolkit toolkit;
	
	public AttributeViewer() {
		super();
		toolkit = new FormToolkit(Display.getDefault());
	}

	public void setContentProvider(AttributeContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setAttributeControlProvider(AttributeControlProvider attributeControlProvider) {
		this.attributeControlProvider = attributeControlProvider;
	}

	public void setComparator(Comparator<AvroAttribute<?>> comparator) {
		this.attributeComparator = comparator;
	}
	
	public void createControl(Composite parent) {
		
		if (attributeControlProvider == null) {
			throw new IllegalStateException("Attribute control provider is missing");
		}
		
		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        
        composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        scrolledComposite.setContent(composite);
		
	}
	
	public Control getControl() {
		return scrolledComposite;
	}
	
	public void reflow() {		
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		composite.layout(true, true);
    }
	
	public void setInput(Object object, AvroContext context) {
		
		if (contentProvider == null) {
			throw new IllegalStateException("Attribute content provider is missing");
		}
		if (attributeControlProvider == null) {
			throw new IllegalStateException("Attribute control provider is missing");
		}
				
		this.context = context;
		
		clearControls();
		
		composite.setLayout(attributeControlProvider.getMainLayout());
		
		AvroAttribute<?>[] attributes = contentProvider.getAttributes(object);
	
		List<AvroAttribute<?>> attributeList = new ArrayList<>();
		for (AvroAttribute<?> attr : attributes) {
			attributeList.add(attr);
		}
		
		if (attributeComparator != null) {
			attributeList.sort(attributeComparator);
		}
		
		for (AvroAttribute attribute : attributeList) {
			if (attributeControlProvider.isVisible(attribute)) {
				AttributeControl attributeControl = attributeControlProvider.getAttributeControl(attribute);
				if (attributeControl != null) {
					AttributeControlConfiguration configuration = attributeControlProvider.getAttributeControlConfiguration(attribute);
					if (configuration != null) {
						attributeControl.setConfiguration(configuration);
					}
					attributeControl.createControl(composite, toolkit, attribute, this.context);
					attributeControl.setLayoutData(attributeControlProvider.getLayoutData(attribute));					
					attributeControl.setEnabled(attribute.isEnabled());
					attributeControlMap.put(attribute, attributeControl);
				}
			}
		}
				
		//composite.layout(true, true);
		reflow();
	}
	
	public void update() {
		// TODO: here we have to check if an attribute becomes visible and in this case we have to build its widget
		// TODO: we have also to re-configure attribute UI if needed 
		for (Map.Entry<AvroAttribute<?>, AttributeControl<?>> entry : attributeControlMap.entrySet()) {
			entry.getValue().update();
		}
		reflow();
	}
	
	protected void clearControls() {
		for (Control child : composite.getChildren()) {
			child.dispose();
		}
		for (Map.Entry<AvroAttribute<?>, AttributeControl<?>> entry : attributeControlMap.entrySet()) {
			entry.getValue().dispose();
		}
		attributeControlMap.clear();
	}
	
	public void setFocus() {
		composite.setFocus();
	}
	
	public void dispose() {
		for (Map.Entry<AvroAttribute<?>, AttributeControl<?>> entry : attributeControlMap.entrySet()) {
			entry.getValue().dispose();
		}
		attributeControlMap.clear();
	}
	
}
