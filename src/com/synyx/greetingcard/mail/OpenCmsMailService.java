/*
 * This file is part of the Synyx Greetingcard module for OpenCms.
 *
 * Copyright (c) 2007 Synyx GmbH & Co.KG (http://www.synyx.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.synyx.greetingcard.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencms.mail.CmsMailHost;
import org.opencms.main.OpenCms;

/**
 * Provides all functionality relevant for sending mail.
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public class OpenCmsMailService implements MailService {
    
    private Log log = LogFactory.getLog(OpenCmsMailService.class);
    
    /** Creates a new instance of MailService */
    public OpenCmsMailService() {
    }
    
    public void sendMail(MessageConfig config) throws MessagingException {
        log.debug("Sending message " + config);
        
        Session session = getSession();
        final MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(config.getFrom(), config.getFromName()));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(config.getTo(), config.getToName()));
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Setting from or to failed", ex);
        }
        mimeMessage.setSubject(config.getSubject());
        mimeMessage.setContent(config.getContent(), config.getContentType());
        // we don't send in a new Thread so that we get the Exception
        Transport.send(mimeMessage);
    }
    
    public void sendMultipartMail(MessageConfig config, DataSource ds, String filename) throws MessagingException {
        log.debug("Sending multipart message " + config);
        
        Session session = getSession();
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart html = new MimeBodyPart();
        html.setContent(config.getContent(), config.getContentType());
        html.setHeader("MIME-Version" , "1.0");
        html.setHeader("Content-Type" , html.getContentType());
        multipart.addBodyPart(html);
        
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(ds));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
        
        final MimeMessage message = new MimeMessage(session);
        message.setContent(multipart);
        try {
            message.setFrom(new InternetAddress(config.getFrom(), config.getFromName()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(config.getTo(), config.getToName()));
        } catch (UnsupportedEncodingException ex) {
            throw new MessagingException("Setting from or to failed", ex);
        }
        
        message.setSubject(config.getSubject());
        
        // we don't send in a new Thread so that we get the Exception
        Transport.send(message);
    }
    
    private Session getSession() {
        final CmsMailHost host = OpenCms.getSystemInfo().getMailSettings().getDefaultMailHost();
        Properties props = new Properties();
        props.put("mail.transport.protocol", host.getProtocol());
        props.put("mail.smtp.host", host.getHostname());
        props.put("mail.smtp.auth", host.isAuthenticating());
        
        Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(host.getUsername(), host.getPassword());
            }
        };
        
        return Session.getInstance(props, authenticator);
    }
    
    public String getContent(String archiveUrl) {
        return archiveUrl;
    }
    
}
