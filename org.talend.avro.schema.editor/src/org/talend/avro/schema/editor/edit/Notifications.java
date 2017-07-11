package org.talend.avro.schema.editor.edit;

import org.talend.avro.schema.editor.context.AvroContext;
import org.talend.avro.schema.editor.context.AvroContext.Kind;

public class Notifications {

	public static final int NONE = 0;
	
	public static final int MASTER_CTX = 1;
	
	public static final int SLAVE_CTX = 1 << 1;
	
	public static final int CURRENT_CTX = 1 << 2;
	
	public static final int NOTIFY = 1 << 3;
	
	public static final int REFRESH = 1 << 4;
	
	public static final int REVEAL = 1 << 5;
	
	public static final int SELECT = 1 << 6;
	
	public static final int NOT_REF = NOTIFY | REFRESH;
	
	public static final int NOT_REF_REV = NOTIFY | REFRESH | REVEAL;
	
	public static final int FULL = NOTIFY | REFRESH | REVEAL | SELECT;
	
	public static final int addContext(int notifications, AvroContext.Kind kind) {
		switch (kind) {
		case MASTER:
			return notifications | MASTER_CTX;
		case SLAVE:
			return notifications | SLAVE_CTX;
		default:
			return notifications;
		}
	}
	
	public static final int addCurrent(int notifications) {
		return notifications | CURRENT_CTX;
	}
	
	public static final int FULL_MASTER = addContext(FULL, Kind.MASTER);
	
	public static final int FULL_SLAVE = addContext(FULL, Kind.SLAVE);
	
	public static final int full(AvroContext.Kind kind) {
		return addContext(FULL, kind);
	}
	
	public static final int full(AvroContext context) {
		return full(context.getKind());
	}

	public static final int NOT_REF_REV_MASTER = addContext(NOT_REF_REV, Kind.MASTER);
	
	public static final int NOT_REF_REV_SLAVE = addContext(NOT_REF_REV, Kind.SLAVE);
	
	public static final int notifyRefreshReveal(AvroContext.Kind kind) {
		return addContext(NOT_REF_REV, kind);
	}
	
	public static final int notifyRefreshReveal(AvroContext context) {
		return notifyRefreshReveal(context.getKind());
	}	
	
	public static final int NOT_REF_MASTER = addContext(NOT_REF, Kind.MASTER);
	
	public static final int NOT_REF_SLAVE = addContext(NOT_REF, Kind.SLAVE);
	
	public static final int notifyRefresh(AvroContext.Kind kind) {
		return addContext(NOT_REF, kind);
	}
	
	public static final int notifyRefresh(AvroContext context) {
		return notifyRefresh(context.getKind());
	}	
	
	public static final int NOT_REF_CURRENT = addCurrent(NOT_REF);
	
	public static final int NOT_REF_REV_CURRENT = addCurrent(NOT_REF_REV);
	
	public static final int FULL_CURRENT = addCurrent(FULL);
	
	public static final AvroContext.Kind getContextKind(int notifications) {
		if ((notifications & MASTER_CTX) != 0) {
			return Kind.MASTER;
		} else if ((notifications & SLAVE_CTX) != 0) {
			return Kind.SLAVE;
		}
		return null;
	}
	
	public static final boolean notifyCurrentContext(int notifications) {
		return (notifications & CURRENT_CTX) != 0;
	}
	
	public static final int addCurrentContext(int notifications, AvroContext context) {
		if (!notifyCurrentContext(notifications)) {
			throw new IllegalArgumentException("Notification does not contain the current context");
		}
		// first remove the current tag
		int currentCtx = CURRENT_CTX;
		int notCurrentCtx = ~currentCtx;
		int result = notifications & notCurrentCtx;
		// then add the specified context kind tag
		return addContext(result, context.getKind());
	}
	
}
