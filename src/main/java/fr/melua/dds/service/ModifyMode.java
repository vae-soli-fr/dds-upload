package fr.melua.dds.service;

public enum ModifyMode {
	RESIZE,
	CROP;
	
	public static ModifyMode fromString(String value) {
		if (value == null) {
			return null;
		}
		try {
			return ModifyMode.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException iaex) {
			return null;
		}
	}

}
