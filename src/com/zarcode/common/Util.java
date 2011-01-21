package com.zarcode.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Util {

	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = "\n" + sw.toString();
        return str;
	}
	
}
