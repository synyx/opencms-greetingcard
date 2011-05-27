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

import com.synyx.greetingcard.DataAccessException;
import com.synyx.greetingcard.dao.Greetingcard;
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.GreetingcardDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardDao;
import javax.mail.MessagingException;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.mail.CmsVfsDataSource;

/**
 * Provides high level functionality for sending
 * greetingcards.
 * @author Florian Hopf, hopf@synyx.de
 */
public class OpenCmsMailTransmission implements MailTransmission {

    private CmsObject cms = null;

    private GreetingcardDao greetingcardDao = null;

    private MailService mailService = null;
    
    public OpenCmsMailTransmission(CmsObject cms, GreetingcardConfig config, MailService service) {
        this.cms = cms;
        this.greetingcardDao = new OpenCmsGreetingcardDao((cms), config);
        this.mailService = service;
    }

    public void sendOrQueue(Greetingcard card) throws MessagingException, DataAccessException {

        try {
            if (card.getTransmitDate() == null) {
                // the mail will be sent now
                MessageConfig config = new MessageConfig(card);
                // TODO inject MailService, move cms functionality to VFSService
                if (card.isSendImage()) {
                    CmsFile file = cms.readFile(card.getImagePath());
                    CmsVfsDataSource dataSource = new CmsVfsDataSource(cms, file);
                    mailService.sendMultipartMail(config, dataSource, file.getName());
                } else {
                    mailService.sendMail(config);
                }
            } else {
                // the mail should be sent later, so the new image will be moved at the "toSend"-folder
                // and the needed properties for the trasmition are attached as fileproperties.
                greetingcardDao.writeInQueue(card);
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (MessagingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataAccessException("Sending or queuing mail failed", ex);
        }

    }

    
}
