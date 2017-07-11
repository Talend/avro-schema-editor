package org.talend.avro.schema.editor.view;

import java.util.ArrayList;
import java.util.List;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;
import org.talend.avro.schema.editor.edit.AvroSchemaEditor;
import org.talend.avro.schema.editor.edit.SchemaEditorContentPart;
import org.talend.avro.schema.editor.edit.services.NotificationServiceImpl;
import org.talend.avro.schema.editor.viewer.SchemaViewer;
import org.talend.avro.schema.editor.viewer.attribute.AttributeViewer;

public class ViewServiceImpl extends NotificationServiceImpl implements IViewService {
	
	private AvroSchemaEditor editor;
	
	private List<IView> views = new ArrayList<>();
	
	public ViewServiceImpl() {
		super();
	}	

	@Override
	public void init(AvroSchemaEditor editor) {
		this.editor = editor;
		final SchemaEditorContentPart contentPart = editor.getContentPart();
		registerView(null, contentPart.getSchemaViewer(AvroContext.Kind.MASTER));
		registerView(null, new IView() {
						
			@Override
			public void select(Object object, Kind context) {
				SchemaViewer slaveSchemaViewer = contentPart.getSchemaViewer(AvroContext.Kind.SLAVE);
				if (slaveSchemaViewer != null) {
					slaveSchemaViewer.select(object, context);
				}
			}
			
			@Override
			public void reveal(Object object, Kind context) {
				SchemaViewer slaveSchemaViewer = contentPart.getSchemaViewer(AvroContext.Kind.SLAVE);
				if (slaveSchemaViewer != null) {
					slaveSchemaViewer.reveal(object, context);
				}
			}
			
			@Override
			public void refresh(Object object) {
				SchemaViewer slaveSchemaViewer = contentPart.getSchemaViewer(AvroContext.Kind.SLAVE);
				if (slaveSchemaViewer != null) {
					slaveSchemaViewer.refresh(object);
				}
			}
			
			@Override
			public void refresh() {
				SchemaViewer slaveSchemaViewer = contentPart.getSchemaViewer(AvroContext.Kind.SLAVE);
				if (slaveSchemaViewer != null) {
					slaveSchemaViewer.refresh();
				}
			}

			@Override
			public String getId() {
				return SchemaViewer.getId(AvroContext.Kind.SLAVE);
			}

			@Override
			public void notify(Object object) {
				// 
			}
			
		});
		final AttributeViewer attributeViewer = contentPart.getAttributeViewer();
		registerView(null, new IView() {
			
			@Override
			public void select(Object object, Kind context) {
				// no selection
			}
			
			@Override
			public void reveal(Object object, Kind context) {
				// nothing to reveal
			}
			
			@Override
			public void refresh(Object object) {
				attributeViewer.update();
			}
			
			@Override
			public void refresh() {
				attributeViewer.update();
			}

			@Override
			public void notify(Object object) {
				// 
			}
			
			@Override
			public String getId() {
				return "AttributeViewer"; //$NON-NLS-1$
			}
			
		});
	}

	public IView getView(String id) {
		for (IView view : views) {
			if (view.getId().equals(id)) {
				return view;
			}
		}
		return null;
	}
		
	protected int getViewPosition(String viewId) {
		for (int i = 0; i < views.size(); i++) {
			IView view = views.get(i);
			if (view.getId().equals(viewId)) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public void registerView(String viewId, IView view) {
		// first check that the view is not already registered
		IView oldView = getView(view.getId());
		if (oldView != null) {
			// replace the existing one
			int oldPos = views.indexOf(oldView);
			views.remove(oldView);
			views.add(oldPos, view);			
		} else if (viewId == null) {
			views.add(view);
		} else {
			int viewPos = getViewPosition(viewId);
			if (viewPos >= 0) {
				views.add(viewPos, view);
			} else {
				throw new IllegalArgumentException("Invalid view position");
			}
		}
	}

	@Override
	public String unregisterView(String viewId) {
		IView view = getView(viewId);
		if (view == null) {
			throw new IllegalArgumentException("View " + viewId + " is not registered");
		}
		int pos = views.indexOf(view);		
		views.remove(view);
		if (pos >= views.size()) {
			return null;
		}
		IView v = views.get(pos);
		return v.getId();
	}

	@Override
	public String[] getViewIds() {
		String[] viewIds = new String[views.size()];
		for (int i = 0; i < viewIds.length; i++) {
			viewIds[i] = views.get(i).getId();
		}
		return viewIds;
	}

	@Override
	public void notify(Object object) {
		editor.setDirty(object, true);
		super.notify(object);
	}

	@Override
	public void refresh() {
		for (IView view : views) {
			view.refresh();
		}
		super.refresh();
	}

	@Override
	public void refresh(Object object) {
		for (IView view : views) {
			view.refresh(object);
		}
		super.refresh(object);
	}
	
	@Override
	public void reveal(Object object, AvroContext.Kind context) {
		for (IView view : views) {
			view.reveal(object, context);
		}	
	}
	
	@Override
	public void select(Object object, Kind context) {
		for (IView view : views) {
			view.select(object, context);
		}
	}
	
}
