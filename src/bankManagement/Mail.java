package bankManagement;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import bankManagement.AttachmentType.AttachmentType;
import bankManagement.AttachmentType.Excel;
import bankManagement.AttachmentType.PDF;
import bankManagement.AttachmentType.Word;

/**
 * Contains methods for mail
 * 
 * @author Abhinav
 * @version 25/06/2024
 */
public class Mail {
	String body;
	String subject;
	String attachmentType;
	String fileContents;
	AttachmentType attachment;
	HashMap<String, Object> detailsMap;

	/**
	 * Constructs the mail object
	 * 
	 * @param body           body of the mail
	 * @param header         header or subject of the mail
	 * @param attachmentType the type of attachment
	 * @param fileContents   the contents of the file
	 * @param detailsMap     contains metadata for the file
	 */
	public Mail(String body, String header, String attachmentType, String fileContents,
			HashMap<String, Object> detailsMap) {
		this.body = body;
		this.subject = header;
		this.attachmentType = attachmentType;
		this.fileContents = fileContents;
		this.detailsMap = detailsMap;
		if (this.attachmentType.equals("csv")) {
			attachment = new Excel(this.subject, this.fileContents, this.detailsMap);
		} else if (this.attachmentType.equals("docx")) {
			attachment = new Word(this.subject, this.fileContents, this.detailsMap);
		} else if (this.attachmentType.equals("pdf")) {
			attachment = new PDF(this.subject, this.fileContents, this.detailsMap);
		}
	}

	/**
	 * Constructs the mail body
	 * 
	 * @param subject     subject of the mail
	 * @param body        body of the mail
	 * @param session     session for MIME message
	 * @param fromAddress from which email address is the mail coming from
	 * @param toAddress   to which mail address is the mail going to
	 */
	public void mailBody(String subject, String body, Session session, String fromAddress, String toAddress) {
		MimeMessage msg = new MimeMessage(session);
		MimeBodyPart attachmentPart = new MimeBodyPart();
		MimeBodyPart bodyPart = new MimeBodyPart();
		try {
			bodyPart.addHeader("Content-Type", "text/html; charset=UTF-8");
			bodyPart.addHeader("format", "flowed");
			bodyPart.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom(new InternetAddress(fromAddress));
			msg.setReplyTo(InternetAddress.parse(toAddress, false));
			msg.setSubject(subject, "UTF-8");
			bodyPart.setText(body, "UTF-8");
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
			try {
				attachmentPart.attachFile(attachment.sendAttachment());
				Multipart mp = new MimeMultipart();
				mp.addBodyPart(bodyPart);
				mp.addBodyPart(attachmentPart);
				msg.setContent(mp);
				Transport.send(msg);
				if (attachment.sendAttachment().delete()) {
					System.out.println("Deleted the file");
				} else {
					System.out.println("Could not delete the file");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the mail
	 */
	public void mailRunner() {
		String fromEmail = System.getenv("MAIL_FROM");
		String toEmail = System.getenv("MAIL_TO");
		String password = System.getenv("MAIL_PASSWORD");

		System.out.println("SSL Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		Session session = Session.getInstance(props, auth);
		mailBody(this.subject, this.body, session, fromEmail, toEmail);
	}
}
