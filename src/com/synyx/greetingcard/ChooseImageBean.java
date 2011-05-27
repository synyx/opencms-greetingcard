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
import com.synyx.greetingcard.cms.OpenCmsContext;
import com.synyx.greetingcard.cms.OpenCmsVFSService;
import com.synyx.greetingcard.cms.VFSService;
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.jsp.CmsJspXmlContentBean;

/**
 * Bean for use in chooseImage.jsp.
 * @author Rainer Steinegger, Synyx GmbH & Co. KG
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class ChooseImageBean {
    
    private List<?> subFolders = Collections.EMPTY_LIST;
    private List<ScaledImage> images = Collections.emptyList();
    private CmsJspActionElement jsp = null;
    
    /**
     * Initializes the bean. This might include two steps:
     * <ul>
     * <li> filling a List with the subfolders of the current folder</li>
     * <li> filling a List with images for all greetingcard templates in the current folder</li>
     * </ul> 
     * @param context
     * @param request
     * @param response
     * @throws javax.servlet.jsp.JspException
     */
    public void init(PageContext context, HttpServletRequest request, HttpServletResponse response) throws JspException {
        // first get the content of the configuration xml-file
        jsp = new CmsJspXmlContentBean(context, request, response);

        GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(jsp.getCmsObject());

        CmsContext cmsContext = new OpenCmsContext(jsp);
        
        GreetingcardService service = new DefaultGreetingcardService(jsp, configDao, cmsContext);
        
        VFSService vfsService = new OpenCmsVFSService(jsp);
        
        try {
            init(cmsContext, vfsService, service, request.getParameter("greetingcard_path"));
        } catch (DataAccessException ex) {
            throw new JspException(ex);
        }
    }


    private void init(CmsContext cmsContext, VFSService vfsService, GreetingcardService service, String givenFolderPath) throws DataAccessException {
        
        GreetingcardConfig config = service.getGreetingcardConfig();

        // initialize the default name of the current folder
        String folderPath = cmsContext.removeSiteRoot(config.getGreetingcardFolder());
        if (givenFolderPath != null) {
            // if there is a request of a selected folder, the new name of the current folder is set
            folderPath = givenFolderPath;
        }

        // if there are more subfolders, put them into the list
        subFolders = vfsService.getNavigationForFolder(folderPath);
        
        images = service.scaleImagesForTemplatesInFolder(folderPath);
    }
    
    /**
     * Returns a List of subfolders for the current folder. The items are of
     * type CmsJspNavElement.
     * @return a List
     */
    public List<?> getSubFolders() {
        return subFolders;
    }

    /**
     * Returns a List of ScaledImages for all Greetingcard Templates in the current folder.
     * @return a List
     */
    public List<ScaledImage> getImages() {
        return images;
    }

    /**
     * Returns the current CmsJspActionElement.
     * @return jsp
     */
    public CmsJspActionElement getJsp() {
        return jsp;
    }
    
}
