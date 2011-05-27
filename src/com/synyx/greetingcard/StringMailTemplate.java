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

import com.synyx.greetingcard.dao.Greetingcard;
import java.util.regex.Pattern;

/**
 * Dummy implementation for configuring a mail body that
 * simply does a String replacement for variables in freemarker
 * syntax.
 * @author Florian Hopf, hopf@synyx.de
 */
public class StringMailTemplate implements MailTemplate {
    
    private String templateText = null;
    
    public StringMailTemplate(String templateText) {
        this.templateText = templateText;
    }
    
    public String getContent(Greetingcard card) {
        String result = templateText;
        result = result.replaceAll(transformToFMVariable(AUTHOR_MAIL), card.getAuthorMail());
        result = result.replaceAll(transformToFMVariable(AUTHOR_NAME), card.getAuthorName());
        result = result.replaceAll(transformToFMVariable(IMAGE_URL), card.getUrl());
        result = result.replaceAll(transformToFMVariable(RECEIVER_MAIL), card.getReceiverMail());
        result = result.replaceAll(transformToFMVariable(RECEIVER_NAME), card.getReceiverName());
        return result;
    }

    private String transformToFMVariable(String name) {
        StringBuilder builder = new StringBuilder("${");
        builder.append(name);
        builder.append("}");
        return Pattern.quote(builder.toString());
    }
}
