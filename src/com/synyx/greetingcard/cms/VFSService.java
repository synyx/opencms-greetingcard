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

import com.synyx.greetingcard.dao.*;
import com.synyx.greetingcard.DataAccessException;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;

/**
 * Interface for accessing VFS functionality used by module.
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public interface VFSService {

    public void writeImage(RenderedImage image, String path, boolean isInternal) throws IOException, DataAccessException;
    
    /**
     * Returns the navigation for the current folder. List items are of type
     * CmsJspNavElement.
     * @param folderPath
     * @return a List
     */
    List<?> getNavigationForFolder(String folderPath);
    
}
