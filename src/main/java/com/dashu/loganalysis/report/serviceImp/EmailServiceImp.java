package com.dashu.loganalysis.report.serviceImp;

import com.dashu.loganalysis.report.service.EmailService;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:18
 **/
@Service
public class EmailServiceImp implements EmailService {

    private static final String MAIL_HOST_SERVER = "mail.smtp.host";
    private static final String MAIL_STMP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String MAIL_SMTP_SSL_SOCKET_FACTORY = "mail.smtp.ssl.socketFactory";

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Override
    public void sendEmail(String from,
                          Address[] to,
                          String host,
                          String subject,
                          String content,
                          String emailType,
                          String password) {
        Properties properties = System.getProperties();
        properties.setProperty(MAIL_HOST_SERVER, host);
        properties.put(MAIL_STMP_AUTH, "true");
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            logger.error("email ssl 安全异常 {}", e);
        }
        sf.setTrustAllHosts(true);
        properties.put(MAIL_SMTP_SSL_ENABLE, "true");
        properties.put(MAIL_SMTP_SSL_SOCKET_FACTORY, sf);

        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties);
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            if ("text".equalsIgnoreCase(emailType)) {
                message.setText(content);
            } else if ("html".equalsIgnoreCase(emailType)) {
                message.setContent(content,"text/html");
            } else {
                logger.error("未支持的邮件格式，目前仅支持 text 与 html 格式的邮件");
                throw new IllegalArgumentException();
            }
            Transport.send(message,from,password);
        }catch (MessagingException e) {
            logger.error("email 发送失败 {}", e);
        }
    }
}
