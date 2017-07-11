package org.talend.avro.schema.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.talend.avro.schema.editor.model.NodeType;

/**
 * The activator class controls the plug-in life cycle
 */
public class AvroSchemaEditorActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.talend.avro.schema.editor"; //$NON-NLS-1$

	private static final String ICONS_PATH = "icons/";//$NON-NLS-1$
	
	public static final int[] ELEMENT_IMAGE_VERSIONS = new int[] { -1, 1, 2 };
	
	private static final boolean OPTIONAL = true;
	
	private static final boolean NOT_OPTIONAL = false;
	
	private static final boolean[] OPTIONAL_PARAMS = new boolean[] { NOT_OPTIONAL, OPTIONAL };
	
	// The shared instance
	private static AvroSchemaEditorActivator plugin;
	
	/**
	 * The constructor
	 */
	public AvroSchemaEditorActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AvroSchemaEditorActivator getDefault() {
		return plugin;
	}

	@Override
    protected void initializeImageRegistry(ImageRegistry registry) {
        addImage(registry, AvroSchemaEditorImages.SYNCHRONIZE_IMAGE, ICONS_PATH + "synched.gif");//$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.INCREASE_TREE_LEVEL_IMAGE, ICONS_PATH + "inc_tree_level.png");//$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.DECREASE_TREE_LEVEL_IMAGE, ICONS_PATH + "dec_tree_level.png");//$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.CLEAR, ICONS_PATH + "clear.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.ERROR_OVERLAY, ICONS_PATH + "error_ov.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.ELEMENT, ICONS_PATH + "elem.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.BUTTON_EXPAND, ICONS_PATH + "button_expand.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.BUTTON_COLLAPSE, ICONS_PATH + "button_collapse.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.NAME_SPACE, ICONS_PATH + "namespace.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.ADD_ELEMENT, ICONS_PATH + "add.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.REMOVE_ELEMENT, ICONS_PATH + "delete.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.MOVE_UP, ICONS_PATH + "pageup.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.MOVE_DOWN, ICONS_PATH + "pagedown.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.CONFIGURE, ICONS_PATH + "configure.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.CHECKED, ICONS_PATH + "checked.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.UNCHECKED, ICONS_PATH + "unchecked.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.WITH_COLUMNS_DISPLAY_MODE, ICONS_PATH + "with_columns_mode.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.WITHOUT_COLUMNS_DISPLAY_MODE, ICONS_PATH + "without_columns_mode.png"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.COPY, ICONS_PATH + "copy.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.PASTE, ICONS_PATH + "paste.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.UNDO, ICONS_PATH + "undo_edit.gif"); //$NON-NLS-1$
        addImage(registry, AvroSchemaEditorImages.REDO, ICONS_PATH + "redo_edit.gif"); //$NON-NLS-1$
        for (NodeType type : NodeType.values()) {
        	for (int i = 0; i < ELEMENT_IMAGE_VERSIONS.length; i++) {
        		int version = ELEMENT_IMAGE_VERSIONS[i];
        		for (boolean optional : OPTIONAL_PARAMS) {        		
        			if (AvroSchemaEditorImages.isDefined(type, optional, version)) {
        				addImage(registry, 
        						AvroSchemaEditorImages.getElementImageKey(type, optional, version), 
        						ICONS_PATH + AvroSchemaEditorImages.getElementImagePath(type, optional, version));
        			}
        		}
        	}
        }
	}
	
	/**
     *
     * @param reg
     * @param key
     * @param path
     */
    private void addImage(ImageRegistry reg, String key, String path) {
        // load image from bundle of plugin
        reg.put(key, AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path));
    }

    /**
     * @param key
     * @return
     */
    public static ImageDescriptor getImageDescriptor(String key) {
        return getDefault().getImageRegistry().getDescriptor(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public static Image getImage(String key) {
        return getDefault().getImageRegistry().get(key);
    }
	
}
