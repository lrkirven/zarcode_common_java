package com.zarcode.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PlatformCommon {
	
	public static final int SUNDAY 			= 0;
	public static final int MONDAY 			= 1;
	public static final int TUESDAY 		= 2;
	public static final int WEDNESDAY 		= 3;
	public static final int THURSDAY 		= 4;
	public static final int FRIDAY 			= 5;
	public static final int SATURDAY 		= 6;
	
	private static final byte[] HEX_CHAR = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static byte[] doc2bytes(Node node) {
        try {
            Source source = new DOMSource(node);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return out.toByteArray();
        } 
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } 
        catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static Document bytesToXml(byte[] xml)  throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml));
	}

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
