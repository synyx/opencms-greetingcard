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

/**
 * Interface for accessing cms context specific functionality.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public interface CmsContext {

    /**
     * Returns the full request path including protocol, server and port.
     * @param request the ServletRequest
     * @param path the path to append to the server address
     * @return the full path (including servlet context)
     */
    String getServerPath(final String path);

    /**
     * Links the given relative path so that it can be used from a browser.
     * This just appends the servlet context to the given path.
     * @param path the relative path
     * @return the absolute path
     */
    String link(String path);

    /**
     * Removes the current site root from the given path
     * @param absolutePath
     * @return the 'cleaned' string
     */
    String removeSiteRoot(String absolutePath);

    /**
     * Sets the project to offline if neccessary.
     * @throws DataAccessException if setting to offline fails
     * @return whether the project was the online project
     */
    boolean setProjectOffline() throws DataAccessException;

}
