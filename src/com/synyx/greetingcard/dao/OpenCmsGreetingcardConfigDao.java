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

import com.synyx.greetingcard.DataAccessException;
import java.util.Locale;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;

/**
 * OpenCms xml content implementation.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class OpenCmsGreetingcardConfigDao implements GreetingcardConfigDao {

    private static final String CONFIG_PATH = "/system/modules/com.synyx.greetingcards/config/configuration";
    
    private CmsObject cms = null;
    
    public OpenCmsGreetingcardConfigDao(CmsObject cms) {
        this.cms = cms;
    }
    
    public GreetingcardConfig readConfig() throws DataAccessException {
        GreetingcardConfig config = null;
        try {
            Locale currentLocale = cms.getRequestContext().getLocale();

            CmsFile file = cms.readFile(cms.getRequestContext().removeSiteRoot(CONFIG_PATH));
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(cms, file);

            // The name used for the connection to the database.
            String address = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_URL, currentLocale);
            String tableName = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_TABLE_NAME, currentLocale);
            String tableField = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_COLUMN_NAME, currentLocale);
            String usr = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_USER, currentLocale);
            String pass = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_PASSWORD, currentLocale);
            String useWhiteList = content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_DATABASE_USE_WHITELIST, currentLocale);

            int height = Integer.parseInt(content.getStringValue(cms,
                    CONFIG_SCHEMA_ELEMENT_THUMBNAIL_HEIGHT, currentLocale));
            int width = Integer.parseInt(content.getStringValue(cms,
                    CONFIG_SCHEMA_ELEMENT_THUMBNAIL_WIDTH, currentLocale));
            // the quality of the imag to be saved in percent
            int quality = Integer.parseInt(content.getStringValue(cms,
                    CONFIG_SCHEMA_ELEMENT_THUMBNAIL_QUALITY, currentLocale));

            int deleteCardsAfterDays = Integer.parseInt(content.getStringValue(cms,
                    CONFIG_SCHEMA_ELEMENT_CARD_DELETE_DAYS, currentLocale));
            int xmlId = Integer.parseInt(content.getStringValue(cms,
                    CONFIG_SCHEMA_ELEMENT_XMLID, currentLocale));
            String archiveValue =
                    content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_CARD_ARCHIVE_FOLDER, currentLocale);
            String cardFolderValue =
                    content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_CARD_GREETINGCARD_FOLDER, currentLocale);
            String tempFolder =
                    content.getStringValue(cms, CONFIG_SCHEMA_ELEMENT_CARD_TEMP_FOLDER, currentLocale);

            config = new GreetingcardConfig();
            config.setWhitelistConnection(address);
            config.setWhitelistColumnName(tableField);
            config.setUseWhitelist("true".equalsIgnoreCase(useWhiteList));
            config.setWhitelistPass(pass);
            config.setWhitelistTableName(tableName);
            config.setWhitelistUser(usr);
            config.setDeleteCardsAfterDays(deleteCardsAfterDays);
            config.setThumbnailHeight(height);
            config.setThumbnailQuality(quality);
            config.setThumbnailWidth(width);
            config.setXmlId(xmlId);
            config.setArchiveFolder(archiveValue);
            config.setGreetingcardFolder(cardFolderValue);
            config.setTempFolder(tempFolder);

        } catch (CmsException ex) {
            throw new DataAccessException(ex);
        }

        return config;

    }

}
