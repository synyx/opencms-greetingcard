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

package com.synyx.greetingcard.dao;

import com.synyx.greetingcard.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsProperty;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;

/**
 * OpenCms xml content implementation
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class OpenCmsGreetingcardDao implements GreetingcardDao {

    private CmsObject cms = null;
    private GreetingcardConfig config = null;
    
    public static final String PROPERTY_TRANSMIT_TIME = "greetingCardTransmitTime";
    public static final String PROPERTY_AUTHOR_NAME = "greetingCardAuthorName";
    public static final String PROPERTY_AUTHOR_MAIL = "greetingCardAuthorMail";
    public static final String PROPERTY_RECEIVER_NAME = "greetingCardReceiverName";
    public static final String PROPERTY_RECEIVER_MAIL = "greetingCardReceiverMail";
    public static final String PROPERTY_SUBJECT = "greetingCardSubject";
    public static final String PROPERTY_CONTENT = "greetingCardContent";
    public static final String PROPERTY_ARCHIVE_URL = "greetingCardArchiveUrl";
    public static final String PROPERTY_SEND_IMAGE = "greetingCardSendImage";
    
    public OpenCmsGreetingcardDao(CmsObject cms, GreetingcardConfig config) {
        this.cms = cms;
        this.config = config;
    }
    
    private String getName(String path) {
        String name = null;
        if (path != null) {
            int posSlash = path.lastIndexOf('/');
            if (posSlash != -1) {
                name = path.substring(posSlash + 1, path.length());
            } else {
                name = path;
            }
        }
        
        return name;
    }
    
    public void writeInQueue(Greetingcard card) throws DataAccessException {

        try {
            
            boolean online = setProjectOffline();

            // TODO construct good path
            String imageToWrite = config.getTempFolder() + getName(card.getImagePath());
            imageToWrite = cms.getRequestContext().removeSiteRoot(imageToWrite);

            cms.lockResource(card.getImagePath());
            List<CmsProperty> propertyList = new ArrayList<CmsProperty>();

            String transmitTime = String.valueOf(card.getTransmitDate().getTime());
            CmsProperty prop = new CmsProperty(PROPERTY_TRANSMIT_TIME, transmitTime, transmitTime);
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_AUTHOR_NAME, card.getAuthorName(), card.getAuthorName());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_AUTHOR_MAIL, card.getAuthorMail(), card.getAuthorMail());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_RECEIVER_NAME, card.getReceiverName(), card.getReceiverName());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_RECEIVER_MAIL, card.getReceiverMail(), card.getReceiverMail());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_SUBJECT, card.getSubject(), card.getSubject());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_ARCHIVE_URL, card.getUrl(), card.getUrl());
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_SEND_IMAGE, String.valueOf(card.isSendImage()), 
                    String.valueOf(card.isSendImage()));
            propertyList.add(prop);
            prop = new CmsProperty(PROPERTY_CONTENT, String.valueOf(card.getContent()), 
                    String.valueOf(card.getContent()));
            propertyList.add(prop);
            
            //cmsObject.writePropertyObjects(imageUrl, propertyList);
            for (Iterator it = propertyList.iterator(); it.hasNext();) {
                cms.writePropertyObject(card.getImagePath(), (CmsProperty) it.next());

            }
            cms.moveResource(card.getImagePath(), imageToWrite);
            OpenCms.getPublishManager().publishResource(cms, imageToWrite);

            if (online) {
                cms.getRequestContext().setCurrentProject(cms.readProject(CmsProject.ONLINE_PROJECT_ID));
            }

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataAccessException(ex);
        }
    }

    public Greetingcard readFromQueue(String path) throws DataAccessException {
        try {

            List propertyObjects = cms.readPropertyObjects(path, false);

            Greetingcard card = new Greetingcard();
            card.setImagePath(path);
            for (Object propObj: propertyObjects) {
                CmsProperty prop = (CmsProperty) propObj;
                String name = prop.getName();
                String value = prop.getValue();
                if (PROPERTY_ARCHIVE_URL.equals(name)) {
                    card.setUrl(value);
                } else if (PROPERTY_AUTHOR_MAIL.equals(name)) {
                    card.setAuthorMail(value);
                } else if (PROPERTY_AUTHOR_NAME.equals(name)) {
                    card.setAuthorName(value);
                } else if (PROPERTY_CONTENT.equals(name)) {
                    card.setContent(value);
                } else if (PROPERTY_RECEIVER_MAIL.equals(name)) {
                    card.setReceiverMail(value);
                } else if (PROPERTY_RECEIVER_NAME.equals(name)) {
                    card.setReceiverName(value);
                } else if (PROPERTY_SEND_IMAGE.equals(name)) {
                    card.setSendImage("true".equalsIgnoreCase(value));
                } else if (PROPERTY_SUBJECT.equals(name)) {
                    card.setSubject(value);
                } else if (PROPERTY_TRANSMIT_TIME.equals(name)) {
                    Long time = Long.parseLong(value);
                    card.setTransmitDate(new Date(time));
                }
            }

            return card;
        } catch (CmsException ex) {
            throw new DataAccessException(ex);
        }
    }

    private boolean setProjectOffline() throws CmsException {
        boolean online = cms.getRequestContext().currentProject().isOnlineProject();
        if (online) {
            cms.getRequestContext().setCurrentProject(cms.readProject("Offline"));
        }
        return online;
    }
    
}
