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

package com.synyx.greetingcard.mail;

import com.synyx.greetingcard.dao.Greetingcard;

/**
 * Wrapper for mail config needed for sending greetingcard
 * @author Florian Hopf, Synyx GmbH &amp; Co. KG, hopf@synyx.de
 */
public class MessageConfig {
    
    private String from = null;
    private String fromName = null;
    private String to = null;
    private String toName = null;
    private String subject = null;
    private String content = null;
    private String contentType = "text/html";
    
    /** Creates a new instance of MailConfig */
    public MessageConfig() {
    }

    /** Creates a new instance from the given greeting card. */
    public MessageConfig(Greetingcard card) {
        this.from = card.getAuthorMail();
        this.fromName = card.getAuthorName();
        this.to = card.getReceiverMail();
        this.toName = card.getReceiverName();
        this.subject = card.getSubject();
        this.content = card.getContent();
    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("MessageConfig: [");
        builder.append("from=");
        builder.append(from);
        builder.append(", fromName=");
        builder.append(fromName);
        builder.append(", to=");
        builder.append(to);
        builder.append(", toName=");
        builder.append(toName);
        builder.append(", subject=");
        builder.append(subject);
        builder.append(", content=");
        builder.append(content);
        builder.append("]");
        return builder.toString();
    }
}
