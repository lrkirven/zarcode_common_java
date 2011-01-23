package com.zarcode.security;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

// import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.zarcode.common.AppCommon;
import com.zarcode.common.Base64;

public class BlockTea {

	static private Logger logger = Logger.getLogger(BlockTea.class.getName());
	
	static public boolean BIG_ENDIAN = true;
	
	static public String encrypt(String plaintext, String password) {
		 int[] v = null;
		 
	    if (plaintext.length() == 0) {
	    	return "";
	    }
	    
	    try {
		    v = strToLongs2(plaintext.getBytes("UTF-8"));
		    if (v.length <= 1) {
		    	v[1] = 0; 
		    }
		    byte[] pwBytes = password.getBytes("UTF-8");
		    byte[] modPwBytes = new byte[ 16 ];
		    System.arraycopy(pwBytes, 0, modPwBytes, 0, modPwBytes.length);
		    int[] k = strToLongs2(modPwBytes);  
		    int n = v.length;
		    
		    // ---- <TEA coding> ---- 
		    
		    int z = v[n-1];
		    int y = v[0];
		    int delta = 0x9E3779B9;
		    int mx = 0;
		    int e = 0;
		    int q = (int)Math.floor(6 + 52/n);
		    int sum = 0;
		    int p = 0;
		    
		    while (q-- > 0) {  
		        sum += delta;
		        e = (sum>>>2) & 3;
		        for (p=0; p<n; p++) {
		            y = v[(p+1)%n];
		            mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[(int)p&3 ^ (int)e] ^ z);
		            z = v[p] += mx;
		        }
		    }
		    
		    // ---- </TEA> ----
	    }
	    catch (Exception e) {
	    	logger.warning("EXCEPTION :: " + e.getMessage());
	    }
	    
	    ByteBuffer cipherText = null;
	    if (v != null && v.length > 0) {
	    	cipherText = longsToStr2(v);
	    }
	    return (cipherText != null ? Base64.encodeBytes(cipherText.array()) : "");
	}
	
	static public String decrypt(String cipherText, String password) {
		String result = null;
		ByteBuffer sb = null;
		
	    if (cipherText != null && cipherText.length() == 0) {
	    	return "";
	    }
	    
	    try {
	    	/*
	    	logger.fine("Base64 encoded bytes :: " + cipherText);
	    	*/
	    	byte[] decodedBytes = Base64.decode(cipherText);
	    	/*
	    	logger.fine("Base64 decoded bytes :: " + AppCommon.dumpByteArray(decodedBytes));
	    	*/
		    int[] v = strToLongs2(decodedBytes);
		    
		    /*
		    logger.info("After strToLongs .....");
		    for (int i=0; i<v.length; i++) {
				logger.info(i + ") " + Integer.toHexString(v[i])); 
			}
			*/
		    
		    byte[] pwBytes = password.getBytes("UTF-8");
		    byte[] modPwBytes = new byte[ 16 ];
		    System.arraycopy(pwBytes, 0, modPwBytes, 0, modPwBytes.length);
		    int[] k = strToLongs2(modPwBytes); 
		    int n = v.length;
		    
		    // ---- <TEA decoding> ---- 
		    
		    int z = v[n-1];
		    int y = v[0];
		    int delta = 0x9E3779B9;
		    int mx = 0;
		    int e = 0;
		    int q = (int)Math.floor(6 + 52/n);
		    int sum = q*delta;
		    int p = 0;
		    
		    while (sum != 0) {
		        e = sum>>>2 & 3;
		        for (p=n-1; p>=0; p--) {
		            z = v[p>0 ? p-1 : n-1];
		            mx = (z>>>5 ^ y<<2) + (y>>>3 ^ z<<4) ^ (sum^y) + (k[p&3^(int)e] ^ z);
		            y = v[p] -= mx;
		        }
		        sum -= delta;
		    }
		    
		    // ---- </TEA> ---- 
		    
		    sb = longsToStr2(v);
		    
		    result = new String(sb.array());
		    result = removeTrailingZeros(result);
		    logger.fine("FINAL RESULT:: -->" + result + "<--");
	    }
	    catch (Exception ex) {
	    	logger.warning("EXCEPTION :: " + ex.getMessage());
	    }
	    return result;
	    
	}
	
	private static String removeTrailingZeros(String str){
		char [] copy = null;
		if (str == null){
			return null;
		}
		
		char[] chars = str.toCharArray();
		int length = str.length();
		int index = 0;
		index = length-1;
		for (; index >= 0; index--) {
			char ch = chars[index];
			byte val = (byte)chars[index];
			if (val != 0) {
			// if (ch != '0' && val != 0) {
				break;
			}
		}
		
		if (index != length-1) {
			int lastIndex = (index+1);
			copy = new char[ lastIndex ];
			System.arraycopy( chars, 0, copy, 0, lastIndex );
		}
	
	    // return (new String(copy));
	    return ( (index == length-1) ? str : (new String(copy)) );
		
	}
	
	static public int[] strToLongs(String s) {  // convert string to array of longs, each containing 4 chars
		int i = 0;
		byte[] temp = null;
		
	    // note chars must be within ISO-8859-1 (with Unicode code-point < 256) to fit 4/long
		byte[] b = s.getBytes();
		double val1 = ((double)b.length)/4.0;
		double val2 = Math.ceil(val1);
		int leng = (int)Math.round(val2);
		int padding = 0;
		if (b.length % 4 != 0) {
			temp = new byte[leng*4];
			System.arraycopy(b, 0, temp, 0, b.length);
		}
		else {
			temp = b;
		}
		int[] l = new int[ leng ];
	    for (i=0; i<leng; i++) {
	    	if (BIG_ENDIAN) {
	    		l[i] = temp[i*4]<<24 | (temp[i*4+1]<<16) | (temp[i*4+2]<<8) | (temp[i*4+3]);
	    	}
	    	else {
	    		l[i] = temp[i*4] | (temp[i*4+1]<<8) | (temp[i*4+2]<<16) | (temp[i*4+3]<<24);
	    	}
	    }
	    return l;  
	}
	
	static public int[] strToLongs2(byte[] b) {  // convert string to array of longs, each containing 4 chars
		int i = 0;
		byte[] temp = null;
		
		double val1 = ((double)b.length)/4.0;
		double val2 = Math.ceil(val1);
		int leng = (int)Math.round(val2);
		int padding = 0;
		if (b.length % 4 != 0) {
			temp = new byte[leng*4];
			System.arraycopy(b, 0, temp, 0, b.length);
		}
		else {
			temp = b;
		}
		int[] l = new int[ leng ];
	    for (i=0; i<leng; i++) {
	    	if (BIG_ENDIAN) {
	    		l[i] = (temp[i*4]<<24 & 0xFF000000) | (temp[i*4+1]<<16 & 0x00FF0000) | (temp[i*4+2]<<8 & 0x0000FF00) | (temp[i*4+3] & 0x000000FF);
	    	}
	    	else {
	    		l[i] = (temp[i*4] & 0x000000FF) | (temp[i*4+1]<<8 & 0x0000FF00) | (temp[i*4+2]<<16 & 0x00FF0000) | (temp[i*4+3]<<24 & 0xFF000000);
	    	}
	    }
	    return l;  
	}
	
	static public ByteBuffer longsToStr2(int[] l) {  
		int i = 0;
		int size = l.length * 4;
	    ByteBuffer sb = ByteBuffer.allocate(size);
	    
		if (BIG_ENDIAN) {
			sb.order(ByteOrder.BIG_ENDIAN);
		}
		else {
			sb.order(ByteOrder.LITTLE_ENDIAN);
		}
	    try {
		    for (i=0; i<l.length; i++) {
		    	if (BIG_ENDIAN) {
		    		byte b1 = (byte)(l[i]>>24 & 0xFF);
		    		sb.put(b1);
		    		byte b2 = (byte)(l[i]>>16 & 0xFF);
		    		sb.put(b2);
		    		byte b3 = (byte)(l[i]>>8 & 0xFF);
		    		sb.put(b3);
		    		byte b4 = (byte)(l[i] & 0xFF);
		    		sb.put(b4);
		    	}
		    	else {
		    		byte b4 = (byte)(l[i] & 0xFF);
		    		sb.put(b4);
		    		byte b3 = (byte)(l[i]>>8 & 0xFF);
		    		sb.put(b3);
		    		byte b2 = (byte)(l[i]>>16 & 0xFF);
		    		sb.put(b2);
		    		byte b1 = (byte)(l[i]>>24 & 0xFF);
		    		sb.put(b1);
		    	}
		    }
	    }
	    catch (Exception e) {
	    }
	    return sb; 
	}
	
	static public String longsToStr(int[] l) {  // convert array of longs back to string
		int i = 0;
		
	    StringBuilder sb = new StringBuilder();
	    try {
		    for (i=0; i<l.length; i++) {
		    	if (BIG_ENDIAN) {
		    		byte[] b1 = new byte[] { (byte)(l[i]>>24 & 0xFF) };
		    		sb.append(new String(b1, "UTF-8"));
		    		byte[] b2 = new byte[] { (byte)(l[i]>>16 & 0xFF) };
		    		sb.append(new String(b2, "UTF-8"));
		    		byte[] b3 = new byte[] { (byte)(l[i]>>8 & 0xFF) };
		    		sb.append(new String(b3, "UTF-8"));
		    		byte[] b4 = new byte[] { (byte)(l[i] & 0xFF) };
		    		sb.append(new String(b4, "UTF-8"));
		    	}
		    	else {
		    		byte[] b4 = new byte[] { (byte)(l[i] & 0xFF) };
		    		sb.append(new String(b4, "UTF-8"));
		    		byte[] b3 = new byte[] { (byte)(l[i]>>8 & 0xFF) };
		    		sb.append(new String(b3, "UTF-8"));
		    		byte[] b2 = new byte[] { (byte)(l[i]>>16 & 0xFF) };
		    		sb.append(new String(b2, "UTF-8"));
		    		byte[] b1 = new byte[] { (byte)(l[i]>>24 & 0xFF) };
		    		sb.append(new String(b1, "UTF-8"));
		    	}
		    }
	    }
	    catch (Exception e) {
	    }
	    return sb.toString(); 
	}
}
