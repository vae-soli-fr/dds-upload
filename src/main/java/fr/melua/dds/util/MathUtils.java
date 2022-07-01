package fr.melua.dds.util;

public class MathUtils {
	
	public static boolean isPow2(int x) {
    	if (x == 0) {
    		return false;
    	}
        return (x & (x - 1)) == 0;
    }

}
