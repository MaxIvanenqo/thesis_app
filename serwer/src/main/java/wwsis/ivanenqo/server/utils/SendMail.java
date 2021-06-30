package wwsis.ivanenqo.server.utils;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMail {
    private static final String sendFrom = "MAILFROM";
    private static final String pass = "MAILPASS";
    public static void sendMessage(String recipient, String code){
        Message message = prepareMessage(sendingConfig(), recipient, code);
        System.out.println("email sent to " + recipient +" "+code);
        try {
            assert message != null;
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private static Session sendingConfig(){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sendFrom, pass);
            }
        };

        return Session.getInstance(properties, authenticator);
    }

    private static Message prepareMessage(Session session, String rec, String code){
        Message message = new MimeMessage(session);
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            message.setSubject("Potwierdzenie rejestracji");
            String html = "<h1>Potwierdzenie rejestracji</h1>" +
                    "<div style='padding-top: 1em; test-align: center'>kod: "+code+"</div>";
            message.setContent(html, "text/html");
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
