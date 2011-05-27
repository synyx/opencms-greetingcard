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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.GraphicsEnvironment;
import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsEncoder;
import org.opencms.widgets.A_CmsSelectWidget;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsWorkplace;

/**
 * Widget for selecting the fonts that are installed on the system.
 * @author Rainer Steinegger, Synyx GmbH & Co. KG
 */
public class CmsFontTypeSelectorWidget extends A_CmsSelectWidget {
    
    /**
     * Creates a new select widget.<p>
     */
    public CmsFontTypeSelectorWidget() {
        
        // empty constructor is required for class registration
        super();
    }
    
    /**
     * Creates a select widget with the select options specified in the given configuration List.<p>
     *
     * The list elements must be of type <code>{@link CmsSelectWidgetOption}</code>.<p>
     *
     * @param configuration the configuration (possible options) for the select widget
     *
     * @see CmsSelectWidgetOption
     */
    public CmsFontTypeSelectorWidget(List configuration) {
        
        super(configuration);
    }
    
    /**
     * Creates a select widget with the specified select options.<p>
     *
     * @param configuration the configuration (possible options) for the select box
     */
    public CmsFontTypeSelectorWidget(String configuration) {
        
        super(configuration);
    }
    
    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    public String getDialogWidget(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {
        
        String id = param.getId();
        StringBuilder result = new StringBuilder();
        result.append("<td class=\"xmlTd\">");
        
        result.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>");
        // medium text input field
        result.append("<input type=\"text\" class=\"xmlInputMedium");
        if (param.hasError()) {
            result.append(" xmlInputError");
        }
        result.append("\" name=\"");
        result.append(id);
        result.append("\" id=\"");
        result.append(id);
        result.append("\"");
        String selected = getSelectedValue(cms, param);
        if (selected != null) {
            // append the selection
            result.append(" value=\"");
            result.append(CmsEncoder.escapeXml(selected));
            result.append("\"");
        }
        result.append(">");
        result.append("</td><td>");
        // button to open combo box
        result.append("<button name=\"test\" onclick=\"showCombo(\'").append(id).append("\', \'combo").append(id);
        result.append("\');return false;\" class=\"widgetcombobutton\">");
        result.append("<img src=\"");
        result.append(CmsWorkplace.getSkinUri()).append("components/widgets/combo.png");
        result.append("\" width=\"7\" height=\"12\" alt=\"\" border=\"0\">");
        result.append("</button></td></tr></table>");
        
        result.append("</td>");
        return result.toString();
    }
    
    /**
     * @see org.opencms.widgets.A_CmsWidget#getDialogHtmlEnd(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    public String getDialogHtmlEnd(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {
        
        String id = param.getId();
        StringBuilder result = new StringBuilder();
        
        // get the select box options
        String [] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List options = new ArrayList();
        for (int i = 0; i < fontList.length; i++) {
            options.add(fontList[i]);
        }
        
        if (options.size() > 0) {
            // create combo div
            result.append("<div class=\"widgetcombo\" id=\"combo");
            result.append(id);
            result.append("\">\n");
            
            int count = 0;
            Iterator i = options.iterator();
            while (i.hasNext()) {
                String option = (String)i.next();
                String itemId = new StringBuffer(64).append("ci").append(id).append('.').append(count).toString();
                // create the link around value
                result.append("\t<a href=\"javascript:setComboValue(\'");
                result.append(id);
                result.append("\', \'");
                result.append(itemId);
                result.append("\')\" name=\"");
                result.append(itemId);
                result.append("\" id=\"");
                result.append(itemId);
                result.append("\"");
                result.append(">");
                result.append(option);
                result.append("</a>\n");
                count++;
            }
            
            // close combo div
            result.append("</div>\n");
            
        }
        
        // return the icon help text from super class
        result.append(super.getDialogHtmlEnd(cms, widgetDialog, param));
        return result.toString();
    }
    
    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    public I_CmsWidget newInstance() {
        
        return new CmsFontTypeSelectorWidget(getConfiguration());
    }
}