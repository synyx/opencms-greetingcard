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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsProperty;
import org.opencms.file.types.CmsResourceTypeImage;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;

/**
 * Provides functionality to access the VFS.
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public class OpenCmsVFSService implements VFSService {

    private CmsJspActionElement jsp = null;
    
    /** Creates a new instance of VFSService */
    public OpenCmsVFSService(PageContext context, HttpServletRequest request, HttpServletResponse response) {
        jsp = new CmsJspActionElement(context, request, response);
    }

    public OpenCmsVFSService(CmsJspActionElement jsp) {
        this.jsp = jsp;
    }

    public void writeImage(RenderedImage image, String path, boolean isInternal) throws IOException, DataAccessException {
        CmsObject cms = jsp.getCmsObject();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            String pngPath = path + ".png";
            boolean wasOnline = setProjectOffline();
            cms.createResource(pngPath, CmsResourceTypeImage.getStaticTypeId(), out.toByteArray(), null);
            cms.writePropertyObject(pngPath, new CmsProperty("greetingCardInternal", String.valueOf(isInternal), String.valueOf(isInternal)));
            cms.unlockResource(pngPath);
            OpenCms.getPublishManager().publishResource(cms, pngPath);

            OpenCms.getPublishManager().waitWhileRunning();

            if (wasOnline) {
                cms.getRequestContext().setCurrentProject(cms.readProject(CmsProject.ONLINE_PROJECT_NAME));
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataAccessException("Failed to do VFS operation for writing image", ex);
        }
    }
    
    private boolean setProjectOffline() throws CmsException {
        boolean online = jsp.getRequestContext().currentProject().isOnlineProject();
        if (online) {
            jsp.getRequestContext().setCurrentProject(jsp.getCmsObject().readProject("Offline"));
        }
        return online;
    }

    public List<?> getNavigationForFolder(String folderPath) {
        return jsp.getNavigation().getNavigationForFolder(folderPath);
    }
}
