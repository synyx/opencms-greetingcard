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

/**
 * Contains all information included in the global greetingcard config.
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public class GreetingcardConfig {
    
    private String whitelistConnection = null;
    private String whitelistUser = null;
    private String whitelistPass = null;
    private String whitelistTableName = null;
    private String whitelistColumnName = null;
    
    private int xmlId;
    private int thumbnailHeight;
    private int thumbnailWidth;
    private int thumbnailQuality;
    private int deleteCardsAfterDays;
    
    private boolean useWhitelist = false;
    
    private String archiveFolder = null;
    private String greetingcardFolder = null;
    private String tempFolder = null;
    
    /** Creates a new instance of GreetingcardConfig */
    public GreetingcardConfig() {
    }

    public String getWhitelistConnection() {
        return whitelistConnection;
    }

    public void setWhitelistConnection(String whitelistConnection) {
        this.whitelistConnection = whitelistConnection;
    }

    public String getWhitelistUser() {
        return whitelistUser;
    }

    public void setWhitelistUser(String whitelistUser) {
        this.whitelistUser = whitelistUser;
    }

    public String getWhitelistPass() {
        return whitelistPass;
    }

    public void setWhitelistPass(String whitelistPass) {
        this.whitelistPass = whitelistPass;
    }

    public String getWhitelistTableName() {
        return whitelistTableName;
    }

    public void setWhitelistTableName(String whitelistTableName) {
        this.whitelistTableName = whitelistTableName;
    }

    public String getWhitelistColumnName() {
        return whitelistColumnName;
    }

    public void setWhitelistColumnName(String whitelistColumnName) {
        this.whitelistColumnName = whitelistColumnName;
    }

    public int getXmlId() {
        return xmlId;
    }

    public void setXmlId(int xmlId) {
        this.xmlId = xmlId;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailQuality() {
        return thumbnailQuality;
    }

    public void setThumbnailQuality(int thumbnailQuality) {
        this.thumbnailQuality = thumbnailQuality;
    }

    public int getDeleteCardsAfterDays() {
        return deleteCardsAfterDays;
    }

    public void setDeleteCardsAfterDays(int deleteCardsAfterDays) {
        this.deleteCardsAfterDays = deleteCardsAfterDays;
    }

    public boolean isUseWhitelist() {
        return useWhitelist;
    }

    public void setUseWhitelist(boolean useWhitelist) {
        this.useWhitelist = useWhitelist;
    }

    public String getArchiveFolder() {
        return archiveFolder;
    }

    public void setArchiveFolder(String archiveFolder) {
        this.archiveFolder = archiveFolder;
    }

    public String getGreetingcardFolder() {
        return greetingcardFolder;
    }

    public void setGreetingcardFolder(String greetingcardFolder) {
        this.greetingcardFolder = greetingcardFolder;
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }
    
}
