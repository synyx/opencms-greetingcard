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
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.JdbcWhitelistDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import com.synyx.greetingcard.dao.WhitelistDao;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.opencms.jsp.CmsJspActionElement;

/**
 * Bean used in dataInput.jsp
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class DataInputBean {
    
    private boolean useWhitelist = false;
    
    private List<String> domains = Collections.emptyList();
    
    private String greetingcardPath = null;
    
    public void init(PageContext context, HttpServletRequest request, HttpServletResponse response) throws JspException {
        CmsJspActionElement jsp = new CmsJspActionElement(context, request, response);
        
        GreetingcardConfigDao dao = new OpenCmsGreetingcardConfigDao(jsp.getCmsObject());
        GreetingcardConfig config = null;
        try {
            config = dao.readConfig();
        } catch (DataAccessException ex) {
            throw new JspException(ex);
        }
        
        // initialize the default name of the current folder
        greetingcardPath = request.getParameter("greetingcard_fileName");
        
        try {
            init(config, new JdbcWhitelistDao(config));
        } catch (DataAccessException ex) {
            throw new JspException(ex);
        }
    }
    
    private void init(GreetingcardConfig config, WhitelistDao dao) throws DataAccessException {
        useWhitelist = config.isUseWhitelist();

        if (useWhitelist) {
            WhitelistDao whitelistDao = new JdbcWhitelistDao(config); 
            domains = whitelistDao.getWhitelist();
        }
        
    }
    
    public boolean isUseWhitelist() {
        return useWhitelist;
    }
    
    public List getDomains() {
        return domains;
    }
    
    public String getGreetingcardPath() {
        return greetingcardPath;
    }
}
