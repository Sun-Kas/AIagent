package com.mak.aiagent.tool;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Properties;

public class EmailTool {


        private final String smtpHost="";//your email
        private final String smtpPort="";//your email
        private final String senderEmail="";//your email
        private final  String senderPassword="";//your email

        @Tool(description = "send email to a someone",returnDirect = true)
        public String sendEmail(@ToolParam(description = "the Recipient email") String recipientEmail,
                              @ToolParam(description = "Email Subject")String subject,
                              @ToolParam(description = "Email Body")String messageText) {
            // 设置邮件服务器属性
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true"); // 启用TLS
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);

            // 创建Session对象
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                // 创建邮件对象
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(recipientEmail)
                );
                message.setSubject(subject);
                message.setText(messageText);

                // 发送邮件
                Transport.send(message);
                return "邮件发送成功！";
            } catch (MessagingException e) {
                return "邮件发送失败！"+e.getMessage();
            }
        }

}
