package com.zarcode.common;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHelper {
	
	private static Logger logger = Logger.getLogger(EmailHelper.class.getName());

	public static void sendAppAlert(String title, String msgBody) {
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("lrkirven@gmail.com", "LazyLaker App"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("lrkirven@gmail.com", "L. Kirven"));
            msg.setSubject(title);
            msg.setText(msgBody);
            Transport.send(msg);
        } 
        catch (AddressException e) {
        	logger.severe("Unable to send email " + Util.getStackTrace(e));
        } 
        catch (MessagingException e) {
        	logger.severe("Unable to send email " + Util.getStackTrace(e));
        } 
        catch (UnsupportedEncodingException e) {
        	logger.severe("Unable to send email " + Util.getStackTrace(e));
		}
	}
}
