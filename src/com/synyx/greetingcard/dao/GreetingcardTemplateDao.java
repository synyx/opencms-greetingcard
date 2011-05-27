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
 * Dao interface for accessing a GreetingcardTemplate
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public interface GreetingcardTemplateDao {

    /**
     * Reads the card from the path specified.
     * @param path a path that can be read as it is
     * @throws DataAccessException if access fails
     */
    GreetingcardTemplate readCard(String path) throws DataAccessException;

}
