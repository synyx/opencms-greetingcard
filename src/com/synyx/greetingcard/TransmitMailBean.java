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
package com.synyx.greetingcard;

import com.synyx.greetingcard.cms.CmsContext;
import com.synyx.greetingcard.cms.OpenCmsContext;
import com.synyx.greetingcard.dao.GreetingcardTemplate;
import com.synyx.greetingcard.dao.Greetingcard;
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.GreetingcardTemplateDao;
import com.synyx.greetingcard.dao.JdbcWhitelistDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardTemplateDao;
import com.synyx.greetingcard.dao.WhitelistDao;
import com.synyx.greetingcard.mail.MailTransmission;
import com.synyx.greetingcard.mail.OpenCmsMailService;
import com.synyx.greetingcard.mail.OpenCmsMailTransmission;
import java.util.Date;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencms.file.CmsObject;
import org.opencms.jsp.CmsJspActionElement;

/**
 * Encapsulates logic used for the transmit mail jsp.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class TransmitMailBean {

    private Log log = LogFactory.getLog(TransmitMailBean.class);
    private boolean transmitted = false;

    private String authorName = null;
    private String authorMail = null;
    private String receiverName = null;
    private String receiverAddress = null;
    private String subject = null;
    private String imagePath = null;
    private String greetingcardTemplatePath = null;
    private String transmitTime = null;
    
    private Date transmitDate = null;
    
    /**
     * Initializes the bean and sends or queues the mail.
     * @param context
     * @param request
     * @param response
     * @throws javax.servlet.jsp.JspException
     */
    public void init(PageContext context, HttpServletRequest request, HttpServletResponse response) throws JspException {
        try {
            CmsJspActionElement jsp = new CmsJspActionElement(context, request, response);

            CmsObject cms = jsp.getCmsObject();

            //get the parameters from the request
            authorName = request.getParameter("author_name");
            authorMail = request.getParameter("author_address");
            receiverName = request.getParameter("receiver_name");
            receiverAddress = request.getParameter("receiver_address");
            subject = request.getParameter("subject");
            imagePath = request.getParameter("image_url");
            greetingcardTemplatePath = request.getParameter("greetingcard_fileName");
            transmitTime = request.getParameter("transmit_date");
            
            transmitDate = null;
            if (!"0".equals(transmitTime)) {
                transmitDate = new Date(Long.parseLong(transmitTime));
            }
            
            GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(cms);
            CmsContext cmsContext = new OpenCmsContext(jsp);
            GreetingcardService greetingcardService = new DefaultGreetingcardService(jsp, configDao, cmsContext);

            GreetingcardConfig config = greetingcardService.getGreetingcardConfig();
            
            WhitelistDao whitelistDao = new JdbcWhitelistDao(config);
            
            MailTransmission transmission = new OpenCmsMailTransmission(cms, config, new OpenCmsMailService());
            
            GreetingcardTemplateDao greetingcardTemplateDao = new OpenCmsGreetingcardTemplateDao(cms);
            
            init(greetingcardService, whitelistDao, cmsContext, greetingcardTemplateDao, transmission);
            
            transmitted = true;
            
        } catch (MessagingException ex) {
            log.error("", ex);
            transmitted = false;
        } catch (DataAccessException ex) {
            log.error("", ex);
            transmitted = false;
        }

    }

    private void init(GreetingcardService greetingcardService, WhitelistDao whitelistDao, 
            CmsContext cmsContext, GreetingcardTemplateDao greetingcardTemplateDao, MailTransmission transmission) throws DataAccessException, MessagingException {

        boolean sendImage = !greetingcardService.isInternal(whitelistDao, receiverAddress);

        // get url of the server for sending the url of the generated image
        String serverUrl = cmsContext.getServerPath(cmsContext.link(cmsContext.removeSiteRoot(imagePath)));
        //String serverUrl = GreetingCardHelper.getServerContextPath(request, jsp.link(cms.getRequestContext().removeSiteRoot(imagePath)));

        // construct the card
        Greetingcard card = new Greetingcard();
        card.setAuthorMail(authorMail);
        card.setAuthorName(authorName);
        card.setImagePath(imagePath);
        card.setReceiverMail(receiverAddress);
        card.setReceiverName(receiverName);
        card.setSendImage(sendImage);
        card.setSubject(subject);
        card.setTransmitDate(transmitDate);
        card.setUrl(serverUrl);

        // read the greetingcard template
        GreetingcardTemplate cardTemplate = greetingcardTemplateDao.readCard(greetingcardTemplatePath);

        // transform the content using MailTemplate
        // TODO this should be transformed using method parameters not constructor
        MailTemplate mailTemplate = new StringMailTemplate(cardTemplate.getMailText());
        card.setContent(mailTemplate.getContent(card));

        // send the mail with the TransmitMail-class
        transmission.sendOrQueue(card);
    }
    
    public boolean isTransmitted() {
        return transmitted;
    }
}
