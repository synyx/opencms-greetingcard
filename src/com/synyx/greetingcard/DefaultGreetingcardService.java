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
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.WhitelistDao;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import org.opencms.file.CmsFile;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.loader.CmsImageScaler;
import org.opencms.main.CmsException;

/**
 * Default implementation of the Greetingcard service.
 * @author Florian Hopf, Synyx Gmbh & Co. KG
 */
public class DefaultGreetingcardService implements GreetingcardService {

    private CmsJspActionElement jsp = null;
    private GreetingcardConfigDao configDao = null;
   
    private GreetingcardConfig config = null;
    
    private CmsContext context = null;
    
    /**
     * Creates a new instance of the service.
     * @param jsp
     * @param configDao
     * @param cmsContext
     */
    public DefaultGreetingcardService(CmsJspActionElement jsp, GreetingcardConfigDao configDao, CmsContext context) {
        this.jsp = jsp;
        this.configDao = configDao;
        this.context = context;
    }

    /**
     * Reads all greetingcard templates in the given folder. The Image property of the greetingcard
     * is used to create a scaled image based on the values in the GreetingcardConfiguration.
     * 
     * @param folderName
     * @return a List with scaled images for all greetingcard templates in the folder
     * @throws com.synyx.greetingcard.DataAccessException
     */
    public List<ScaledImage> scaleImagesForTemplatesInFolder(String folderName) throws DataAccessException {
        initConfig();
        try {
            CmsImageScaler scaler = new CmsImageScaler();
            scaler.setHeight(config.getThumbnailHeight());
            scaler.setWidth(config.getThumbnailWidth());
            scaler.setQuality(config.getThumbnailQuality());

            List<ScaledImage> images = new ArrayList<ScaledImage>();
            List<?> greetingcardTemplateFiles = jsp.getCmsObject().getFilesInFolder(folderName);
            Attributes additionalAttributes = new Attributes(0);
            for (Object templateFileObj : greetingcardTemplateFiles) {
                CmsFile templateFile = (CmsFile) templateFileObj;
                String templateFilePath = context.removeSiteRoot(templateFile.getRootPath());
                String imagePath = jsp.getContent(templateFilePath, "Image", 
                        jsp.getCmsObject().getRequestContext().getLocale());
                String imageTag = jsp.img(imagePath, scaler, additionalAttributes);
                images.add(new ScaledImage(templateFilePath, imageTag));
            }

            return images;
        } catch (CmsException ex) {
            throw new DataAccessException(ex);
        }
    }
    
    /**
     * Returns whether the given address is internal.
     * @param dao the WhitelistDao to check against
     * @param mail the email address to check against
     * @return whether the 
     * @throws com.synyx.greetingcard.DataAccessException in case dao access fails
     */
    public boolean isInternal(WhitelistDao dao, String mail) throws DataAccessException {
        boolean internal = false;
        if (mail.indexOf("@") != -1) {
            String domain = mail.split("@")[1];
            internal = dao.contains(domain);
        }
        return internal;

    }
    
    /**
     * Returns the GreetingcardConfig.
     * @return an initialized GreetingcardConfig
     * @throws com.synyx.greetingcard.DataAccessException in case VFS access failed
     */
    public GreetingcardConfig getGreetingcardConfig() throws DataAccessException {
        initConfig();
        return config;
    }
    
    private void initConfig() throws DataAccessException {
        if (config == null) {
            config = configDao.readConfig();
        }
    }

}
