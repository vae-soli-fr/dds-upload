package fr.melua.dds.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringUtils {
	
	public static String join(String separator, Object... items) {
    	return Arrays.asList(items).stream().map(Objects::toString).collect(Collectors.joining(separator));
    }
    
	public static String concat(Object... items) {
    	return Arrays.asList(items).stream().map(Objects::toString).collect(Collectors.joining());
    }
	
	public static boolean isBlank(String s) {
		return s == null || !hasLength(s.trim(), 0);
    }
	
	public static boolean hasLength(String s, int length) {
		return s.length() != 0 && s.length() >= length;
    }

}
