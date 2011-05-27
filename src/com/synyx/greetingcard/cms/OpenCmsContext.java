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


package com.synyx.greetingcard.cms;

import com.synyx.greetingcard.DataAccessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;

/**
 * Implementation of the cms context for OpenCms
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class OpenCmsContext implements CmsContext {

    // contains the protocols for resolving urls
    private static final Map<String, String> PROTOCOLS = new HashMap<String, String>();
    
    static {
        PROTOCOLS.put("HTTP/1.1", "http://");
        PROTOCOLS.put("HTTPS/1.1", "https://");
    }
    
    private CmsJspActionElement jsp = null;
    
    public OpenCmsContext(CmsJspActionElement jsp) {
        this.jsp = jsp;
    }
    
    /**
     * Links the given relative path so that it can be used from a browser.
     * This just appends the servlet context to the given path. 
     * @param path the relative path
     * @return the absolute path
     */
    public String link(String path) {
        return jsp.link(path);
    }
    
    /**
     * Removes the current site root from the given path
     * @param absolutePath
     * @return the 'cleaned' string
     */
    public String removeSiteRoot(String absolutePath) {
        return jsp.getCmsObject().getRequestContext().removeSiteRoot(absolutePath);
    }

    /**
     * Returns the full request path including protocol, server and port.
     * @param request the ServletRequest 
     * @param path the path to append to the server address
     * @return the full path (including servlet context)
     */
    public String getServerPath(final String path) {
        // TODO there should be a better solution for this
        StringBuilder builder = new StringBuilder();
        builder.append(PROTOCOLS.get(jsp.getRequest().getProtocol()));
        builder.append(jsp.getRequest().getServerName());
        builder.append(':');
        builder.append(jsp.getRequest().getServerPort());
        builder.append(path);

        return builder.toString();
    }
    
    /**
     * Returns the navigation for the current folder. List items are of type 
     * CmsJspNavElement.
     * @param folderPath
     * @return a List
     */
    public List<?> getNavigationForFolder(String folderPath) {
        return jsp.getNavigation().getNavigationForFolder(folderPath);
    }
    
    /**
     * Sets the project to offline if neccessary.
     * @throws DataAccessException if setting to offline fails
     * @return whether the project was the online project 
     */
    public boolean setProjectOffline() throws DataAccessException {
        boolean online = jsp.getCmsObject().getRequestContext().currentProject().isOnlineProject();
        if (online) {
            try {
                jsp.getCmsObject().getRequestContext().setCurrentProject(jsp.getCmsObject().readProject("Offline"));
            } catch (CmsException ex) {
                throw new DataAccessException(ex);
            }
        }
        return online;
    }
    
}
