package com.zarcode.common;

public class AppCommon {
	
	public static final int SUNDAY 			= 0;
	public static final int MONDAY 			= 1;
	public static final int TUESDAY 		= 2;
	public static final int WEDNESDAY 		= 3;
	public static final int THURSDAY 		= 4;
	public static final int FRIDAY 			= 5;
	public static final int SATURDAY 		= 6;
	
	private static final byte[] HEX_CHAR = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


	public static String capitalize(String inputWord) {
		String res = null;
		
		// get first letter
        String firstLetter = inputWord.substring(0,1);
        // get remainder of word
        String remainder   = inputWord.substring(1);
        // combine
        res = firstLetter.toUpperCase() + remainder.toLowerCase();

        return res;
    }
	
	public static String dumpByteArray(byte[] buffer) {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		if (buffer == null) {
			return "";
	    }

	    for (i=0; i<buffer.length; i++) {
	    	sb.append( "0x" ).append( (char) ( HEX_CHAR[( buffer[i] & 0x00F0 ) >> 4] ) ).append(
	    			(char) (HEX_CHAR[buffer[i] & 0x000F] ) ).append( " " );
	    }
		return sb.toString();
	}
	

}
