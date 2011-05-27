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

/**
 * Dao interface for accessing a greetingcard config.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public interface GreetingcardConfigDao {
    String CONFIG_SCHEMA_ELEMENT_CARD_ARCHIVE_FOLDER = "ArchiveFolder[1]";
    String CONFIG_SCHEMA_ELEMENT_CARD_DELETE_DAYS = "DeleteCardsAfterDays[1]";
    String CONFIG_SCHEMA_ELEMENT_CARD_GREETINGCARD_FOLDER = "GreetingcardFolder[1]";
    String CONFIG_SCHEMA_ELEMENT_CARD_TEMP_FOLDER = "TempFolder[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_COLUMN_NAME = "Database[1]/ColumnName[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_PASSWORD = "Database[1]/DatabasePassword[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_SCHEMA = "Database";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_TABLE_NAME = "Database[1]/TableName[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_URL = "Database[1]/DatabaseUrl[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_USER = "Database[1]/DatabaseUser[1]";
    String CONFIG_SCHEMA_ELEMENT_DATABASE_USE_WHITELIST = "Database[1]/UseWhiteList[1]";
    String CONFIG_SCHEMA_ELEMENT_THUMBNAIL_HEIGHT = "Thumbnail[1]/ThumbnailHeight[1]";
    String CONFIG_SCHEMA_ELEMENT_THUMBNAIL_QUALITY = "Thumbnail[1]/ThumbnailQuality[1]";
    String CONFIG_SCHEMA_ELEMENT_THUMBNAIL_SCHEMA = "Thumbnail";
    String CONFIG_SCHEMA_ELEMENT_THUMBNAIL_WIDTH = "Thumbnail[1]/ThumbnailWidth[1]";
    String CONFIG_SCHEMA_ELEMENT_XMLID = "XMLID[1]";

    GreetingcardConfig readConfig() throws DataAccessException;

}
