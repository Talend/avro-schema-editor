package org.talend.avro.schema.editor.utils;

import java.util.List;

import org.talend.avro.schema.editor.Defines;

/**
 * Provides some convenient methods around strings.
 * 
 * @author timbault
 *
 */
public final class StringUtils {

	/**
	 * Given a default name and a list of names, this method computes a valid name 
	 * 
	 * @param defaultName
	 * @param sep
	 * @param names
	 * @return
	 */
	public static String getAvailableName(String defaultName, String sep, List<String> names) {		
		boolean used = isNameUsed(defaultName, names);		
		if (!used) {
			return defaultName;
		}
		int index = 0;
		String name = null;
		while (used) {
			index++;
			name = defaultName + sep + index;
			used = isNameUsed(name, names);
		}		
		return name;
	}
	
	protected static boolean isNameUsed(String name, List<String> names) {
		for (String n : names) {
			if (n.trim().equals(name.trim())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean areEqual(String str1, String str2) {
		if (isNull(str1)) {
			return isNull(str2);
		} else {
			if (isNull(str2)) {
				return false;
			} else {
				return str1.trim().equals(str2.trim());
			}
		}
	}
	
	protected static boolean isNull(String str) {
		return str == null || str.trim().isEmpty();
	}	
	
	public static String removeExtension(String file, String extension) {
		String ext = extension;
		if (!extension.startsWith(Defines.DOT)) {
			ext = Defines.DOT + extension;
		}
		if (file.endsWith(ext)) {
			return file.substring(0, file.length() - ext.length());
		}
		return file;
	}
	
}
