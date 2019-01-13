package de.dfki.sonogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.Properties;

public class FeedbackMessage extends JDialog {

    Sonogram reftomain;
    JTextField from = new JTextField (1);
    JTextArea messageText = new JTextArea(8,40);

    public FeedbackMessage(Sonogram ref) {
	super(ref,"Send Feedback Message",true);
	reftomain = ref; 
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	setLayout (new GridBagLayout());

	final JLabel label = new JLabel ("Your eMail Adress:");
	JPanel email = new JPanel();
	email.setLayout(new BorderLayout());
	email.setBorder(new TitledBorder(new EtchedBorder(),"Who you are ?"));
	email.add(label,BorderLayout.WEST);
	email.add(from,BorderLayout.CENTER);

	JPanel message = new JPanel();
 	message.setBorder(new TitledBorder(new EtchedBorder(),"What you would like to say about Sonogram ?"));
	messageText.setText("I use Sonogram mainly for...\n\nI have a great idea, could you please implement the feature...\n\nMy opinion about Sonogram...\n\nI found a Bug in...");
	JScrollPane scrollpane = new JScrollPane(messageText);
	message.add(scrollpane);

	JButton send = new JButton("Send Message",new ImageIcon(Sonogram.class.getResource("SendMail.gif")));
        ActionListener sendListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String emailText = new String("Feedback Message from Sonogram User\n");
		    emailText += "SENDER EMAIL ADRESS: ";
		    emailText += from.getText()+"\n";
		    emailText += "MESSAGE TEXT:\n";
		    emailText += messageText.getText();
		    emailText += "\n"+getSystemProperties();
		    sendText(emailText);
		    setVisible(false);
		}
	    };
	send.addActionListener(sendListener);
	JPanel p = new JPanel();
 	p.setBorder(new TitledBorder(new EtchedBorder(),"Please provide me with a Bug report or new Ideas..."));
        p.setLayout (new BorderLayout());
        p.add(email, BorderLayout.NORTH);
	p.add(message, BorderLayout.CENTER);
        p.add(send, BorderLayout.SOUTH);
        getContentPane().add(p);
	pack();
	int scw  = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	int sch = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	setLocation(scw/2-getWidth()/2,sch/2-getHeight()/2);
	setResizable(false);
	setVisible(true);
    }

    public void sendText(String text) {
	System.out.println("--> Try to send the Message.");	    
	SendEmail mailSender = new SendEmail();
	if (mailSender.sentEmailMessage(text) == true) {
	    JOptionPane.showMessageDialog(reftomain,"Your Feedback Message has successfully been sent to Me.\nI will contact you as soon as possible...","Message successfully Sent...",1,new ImageIcon(Sonogram.class.getResource("chris.jpg")));
	    System.out.println("--> Feedback Message successfully send.");	    
	}
	else {
	    int confirm = JOptionPane.showOptionDialog(reftomain,"There was a problem sending your Feedback Message to me.\nThis could be a Problem with your Internet Connection or your\nFirewall. Would you like to try it again ?","Feedback Message could not been Send !",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
	    if (confirm == 0) { // send again
		sendText(text);
	    }					   
	    System.out.println("--> PROBLEM while sending Feedback Message.");	    
	}	
    }

    public String getSystemProperties() {
	String propertiesString = new String("\nSYSTEM PROPERTIES:\n");
	propertiesString += "Sonogram Version = " + reftomain.version + "\n";
	propertiesString += "Sonogram Build = " + reftomain.build + "\n";
	Properties sysprops = System.getProperties();
	java.util.Enumeration propnames = sysprops.propertyNames();
	while (propnames.hasMoreElements()) {
	    String propname = (String)propnames.nextElement();
	    propertiesString += propname + "=" + System.getProperty(propname) + "\n";
	}
	return propertiesString;
    }
}