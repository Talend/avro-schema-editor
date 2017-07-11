package org.talend.avro.schema.editor.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.avro.schema.editor.AvroSchemaEditorActivator;
import org.talend.avro.schema.editor.AvroSchemaEditorImages;
import org.talend.avro.schema.editor.edit.services.IEditorServiceProvider;
import org.talend.avro.schema.editor.model.AvroNode;
import org.talend.avro.schema.editor.model.ModelUtil;
import org.talend.avro.schema.editor.model.NodeType;
import org.talend.avro.schema.editor.model.PrimitiveType;
import org.talend.avro.schema.editor.model.RefNode;
import org.talend.avro.schema.editor.model.UnionNode;
import org.talend.avro.schema.editor.preferences.AvroSchemaEditorPreferences;
import org.talend.avro.schema.editor.preferences.IEditPreferencesService;
import org.talend.avro.schema.editor.preferences.IPreferencesListener;
import org.talend.avro.schema.editor.preferences.PreferencesAdapter;
import org.talend.avro.schema.editor.viewer.SchemaViewer.DisplayMode;
import org.talend.avro.schema.editor.viewer.attribute.AttributeUtil;

/**
 * Label provider for the avro schema editor trees.
 * 
 * @author timbault
 *
 */
public class SchemaTreeLabelProviderImpl implements SchemaViewerLabelProvider {
	
	private IEditorServiceProvider serviceProvider;
	
	private String space = null;
	
	private IPreferencesListener preferenceListener;
	
	private DisplayMode displayMode;
	
	public SchemaTreeLabelProviderImpl(IEditorServiceProvider serviceProvider, DisplayMode displayMode) {
		super();
		this.serviceProvider = serviceProvider;
		this.displayMode = displayMode;
		this.space = computeSpace();
		initPreferenceListener(serviceProvider);
	}

	private void initPreferenceListener(IEditorServiceProvider serviceProvider) {
		this.preferenceListener = new PreferencesAdapter() {
			
			@Override
			public void onIntChange(String key, int newValue) {
				switch (key) {
				case AvroSchemaEditorPreferences.HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_KEY:
					space = computeSpace();
					break;
				}
			}

		};
		serviceProvider.getService(IEditPreferencesService.class).addPreferencesListener(preferenceListener);
	}
	
	protected String computeSpace() {
		int horizontalSpace = getHorizontalSpace();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < horizontalSpace; i++) {
			buffer.append(" ");
		}
		return buffer.toString();
	}
	
	@Override
	public Image getImage(AvroNode node) {
		NodeType type = node.getType();
		if (type.isRef()) {
			type = ((RefNode) node).getReferencedType();
			node = ((RefNode) node).getReferencedNode();
		}
		boolean optional = ModelUtil.isOptional(node);
		return AvroSchemaEditorActivator.getImage(AvroSchemaEditorImages.getElementImageKey(type, optional, getImageVersion()));	
	}

	protected int getImageVersion() {
		return serviceProvider.getService(IEditPreferencesService.class)
				.getInteger(AvroSchemaEditorPreferences.ICONS_VERSION_KEY);
	}
	
	protected boolean showTooltip() {
		return serviceProvider.getService(IEditPreferencesService.class)
				.getBoolean(AvroSchemaEditorPreferences.SHOW_TOOLTIP_IN_SCHEMA_VIEWER_KEY);
	}
	
	protected boolean showTypeText() {
		return displayMode == DisplayMode.WITHOUT_COLUMNS && 
				serviceProvider.getService(IEditPreferencesService.class)
				.getBoolean(AvroSchemaEditorPreferences.SHOW_ELEMENT_TYPE_IN_SCHEMA_VIEWER_KEY);
	}
	
	protected boolean showDocText() {
		return displayMode == DisplayMode.WITHOUT_COLUMNS &&
				serviceProvider.getService(IEditPreferencesService.class)
				.getBoolean(AvroSchemaEditorPreferences.SHOW_ELEMENT_DOC_IN_SCHEMA_VIEWER_KEY);
	}
	
	protected int getDocDisplayedLength() {
		return serviceProvider.getService(IEditPreferencesService.class)
				.getInteger(AvroSchemaEditorPreferences.DOC_LENGTH_IN_SCHEMA_VIEWER_KEY);
	}
	
	protected int getHorizontalSpace() {
		return serviceProvider.getService(IEditPreferencesService.class)
				.getInteger(AvroSchemaEditorPreferences.HORIZONTAL_SPACE_IN_SCHEMA_VIEWER_KEY);
	}
	
	@Override
	public String getText(AvroNode node) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getName(node));
		if (showTypeText() && hasTypeText(node)) {
			buffer.append(space);
			buffer.append("[");
			buffer.append(getTypeText(node));
			buffer.append("]");
		}
		if (showDocText() && hasDocText(node)) {
			buffer.append(space);
			buffer.append(getDocText(node));
		}
		return buffer.toString();
	}

	protected boolean hasDocText(AvroNode node) {
		AvroNode referencedNode = getReferencedNode(node);
		if (AttributeUtil.hasDocAttribute(referencedNode)) {
			String doc = AttributeUtil.getDoc(referencedNode);
			return doc != null && !doc.trim().isEmpty();
		}
		return false;
	}
	
	protected String getDocText(AvroNode node) {
		AvroNode referencedNode = getReferencedNode(node);
		String doc = AttributeUtil.getDoc(referencedNode);
		doc = doc.trim();
		int length = getDocDisplayedLength();
		if (length > 0 && doc.length() > length) {
			doc = doc.substring(0, length);
			doc = doc + "...";
		}
		return doc;
	}
	
	protected AvroNode getReferencedNode(AvroNode node) {
		if (node.getType() == NodeType.REF) {
			return ((RefNode) node).getReferencedNode();
		}
		return node;
	}
	
	protected String getName(AvroNode node) {
		AvroNode referencedNode = getReferencedNode(node);
		if (AttributeUtil.isNameWithNameSpace(referencedNode)) {
			return AttributeUtil.getTrueName(referencedNode);
		}
		return AttributeUtil.getNameFromAttribute(referencedNode);
	}
	
	protected boolean hasTypeText(AvroNode node) {
		NodeType nodeType = node.getType();
		switch (nodeType) {
		case UNION:
		case PRIMITIVE_TYPE:
			return false;
		default:
			return true;
		}
	}
	
	protected String getTypeText(AvroNode node) {
		NodeType nodeType = node.getType();
		switch (nodeType) {
		case RECORD:
			return "record";
		case FIELD:
			return getFullTypeText(node);
		case ARRAY:
		case MAP:
			return getFullTypeText(node);
		case ENUM:
			return "enum";	
		case FIXED:
			return "fixed";
		case REF:
			StringBuffer buffer = new StringBuffer();
			buffer.append("ref ");
			RefNode refNode = (RefNode) node;
			AvroNode referencedNode = refNode.getReferencedNode();
			buffer.append(getTypeText(referencedNode));
			return buffer.toString();
		default:
			break;
		}
		return "...";
	}
	
	protected String getFullTypeText(AvroNode node) {
		StringBuffer buffer = new StringBuffer();
		if (AttributeUtil.hasComplexType(node)) {
			buffer.append(getSubTypeText(node.getChild(0)));
		} else {
			PrimitiveType type = AttributeUtil.getPrimitiveType(node);
			buffer.append(type.getName());
		}
		return buffer.toString();
	}
	
	protected String getSubTypeText(AvroNode node) {
		NodeType type = node.getType();
		switch (type) {
		case RECORD:
		case ENUM:
		case FIXED:
		case REF:
		case PRIMITIVE_TYPE:
			return AttributeUtil.getNameFromAttribute(node);
		case UNION:
			StringBuffer buffer = new StringBuffer();
			UnionNode unionNode = (UnionNode) node;
			boolean isOptional = ModelUtil.hasNullChild(unionNode);
			if (isOptional) {
				buffer.append("optional ");
			}
			if (AttributeUtil.isChoiceType(unionNode)) {
				buffer.append("choice");
			} else {
				AvroNode child = ModelUtil.getFirstNotNullChild(unionNode);
				String typeName = getSubTypeText(child);
				buffer.append(typeName);
			}
			return buffer.toString();
		case ARRAY:			
			return "array of " + getFullTypeText(node);
		case MAP:
			return "map of " + getFullTypeText(node);		
		default:
			break;
		}
		return "";
	}
	
	@Override
	public String getToolTipText(AvroNode node) {
		if (showTooltip()) {
			AvroNode referencedNode = getReferencedNode(node);
			if (AttributeUtil.hasDocAttribute(referencedNode)) {
				return AttributeUtil.getDoc(referencedNode);
			}
		}
		return null;
	}

	@Override
	public StyleRange[] getStyleRanges(AvroNode node) {
		
		List<StyleRange> styleRangeList = new ArrayList<>();
		
		int startIndex = 0;
		
		// name style
		String name = getName(node);
		int length = name.length();
		Color foregroundColor = Display.getDefault().getSystemColor(SWT.DEFAULT);
		Color backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		StyleRange nameStyle = new StyleRange(startIndex, length, foregroundColor, backgroundColor);
		if (node.getType().isRef()) {
			nameStyle.font = getItalicFont();
		}
		styleRangeList.add(nameStyle);
		startIndex = length;
		
		// type style
		if (showTypeText() && hasTypeText(node)) {
			String typeText = getTypeText(node);
			length = typeText.length() + space.length() + 2; // add "   [" and "]" lengths
			StyleRange typeStyle = new StyleRange(startIndex, length, foregroundColor, backgroundColor);
			if (node.getType().isRef()) {
				typeStyle.font = getItalicFont();
			}
			styleRangeList.add(typeStyle);
			startIndex = startIndex + length;
		}
		
		// doc style
		if (showDocText() && hasDocText(node)) {
			String docText = getDocText(node);
			length = docText.length() + space.length(); // add "   " length
			Color docColor = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
			StyleRange docStyle = new StyleRange(startIndex, length, docColor, backgroundColor);
			docStyle.font = getItalicFont();
			styleRangeList.add(docStyle);
		}
		
		return styleRangeList.toArray(new StyleRange[styleRangeList.size()]);
	}
	
	protected Font getItalicFont() {
		return JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
	}

	protected Font getBoldFont() {
		return JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
	}
	
	@Override
	public Color getBackgroundColor(AvroNode node) {
		// use default background color
		return null;
	}

	@Override
	public void dispose() {
		serviceProvider.getService(IEditPreferencesService.class).removePreferencesListener(preferenceListener);
		preferenceListener = null;
	}
	
}
