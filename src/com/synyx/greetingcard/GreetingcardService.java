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

import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.WhitelistDao;
import java.util.List;

/**
 * Interface for higher level functionality of the module.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public interface GreetingcardService {

    /**
     * Returns the GreetingcardConfig.
     * @return an initialized GreetingcardConfig
     * @throws com.synyx.greetingcard.DataAccessException in case VFS access failed
     */
    GreetingcardConfig getGreetingcardConfig() throws DataAccessException;

    /**
     * Returns whether the given address is internal.
     * @param dao the WhitelistDao to check against
     * @param mail the email address to check against
     * @return whether the
     * @throws com.synyx.greetingcard.DataAccessException in case dao access fails
     */
    boolean isInternal(WhitelistDao dao, String mail) throws DataAccessException;

    /**
     * Reads all greetingcard templates in the given folder. The Image property of the greetingcard
     * is used to create a scaled image based on the values in the GreetingcardConfiguration.
     *
     * @param folderName
     * @return a List with scaled images for all greetingcard templates in the folder
     * @throws com.synyx.greetingcard.DataAccessException
     */
    List<ScaledImage> scaleImagesForTemplatesInFolder(String folderName) throws DataAccessException;

}
