package de.dfki.sonogram;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

public class SendEmail {
    String SMTP_SERVER    = "mail.gmx.net";
    String SMTP_AUTH_USER = "christoph_lauer@gmx.de";
    String SMTP_AUTH_PASS = "?WhadUg?";
    String MAIL_SENDER    = "christoph_lauer@gmx.de";
    String MAIL_RECEIVER  = "christoph.lauer@gmail.com";
    String MAIL_SUBJECT   = "!!! SONOGRAM FEEDBACK !!!";

    public static void main (String[] args) {
	SendEmail mail = new SendEmail();
    }

    public SendEmail() {
    }
    public boolean sentEmailMessage(String mailText) {
	try 
	    {
		SMTPAuthenticator auth = new SMTPAuthenticator();
		Properties props = System.getProperties();
		props.put("mail.smtp.host", SMTP_SERVER);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, auth);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(MAIL_SENDER));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(MAIL_RECEIVER));
		message.setSubject(MAIL_SUBJECT);
		message.setText(mailText);
		Transport.send(message);
	    }
	catch (Exception e) 
	    { 
		return (false);
	    }
	return (true);
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
	    return new PasswordAuthentication(SMTP_AUTH_USER,SMTP_AUTH_PASS);
        }
    }
}

