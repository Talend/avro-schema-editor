package org.talend.avro.schema.editor.log;

import org.eclipse.ui.IViewPart;
import org.talend.avro.schema.editor.registry.NSNode;
import org.talend.avro.schema.editor.statistics.NodeStats;
import org.talend.avro.schema.editor.utils.UIUtils;

public class AvroSchemaLogger {	
	
	private static boolean isLogViewOpened() {		
		IViewPart view = UIUtils.findView(AvroSchemaLogView.ID);		
		return view != null;		
	}
		
	private static AvroSchemaLogView getLogView(boolean show) {		
		AvroSchemaLogView logView = null;		
		if (show) {
			logView = (AvroSchemaLogView) UIUtils.showView(AvroSchemaLogView.ID);
		} else {
			logView = (AvroSchemaLogView) UIUtils.findView(AvroSchemaLogView.ID);
		}		
		return logView;		
	}
	
	public static void logMsg(String msg, boolean show) {
		if (isLogViewOpened()) {
			AvroSchemaLogView logView = getLogView(show);
			logView.logMsg(msg);
		}
	}
	
	public static void logStats(NodeStats stats, boolean show) {
		if (isLogViewOpened()) {
			AvroSchemaLogView logView = getLogView(show);
			logView.logStats(stats);
		}
	}
	
	public static void logNameSpaceTree(NSNode nameSpaceTree, boolean show) {
		if (isLogViewOpened()) {
			AvroSchemaLogView logView = getLogView(show);
			logView.logNameSpaceTree(nameSpaceTree);
		}
	}
	
}
