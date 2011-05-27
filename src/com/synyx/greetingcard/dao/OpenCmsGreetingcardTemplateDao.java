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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.xml.CmsXmlUtils;
import org.opencms.xml.content.CmsXmlContent;
import org.opencms.xml.content.CmsXmlContentFactory;
import org.opencms.xml.types.CmsXmlBooleanValue;
import org.opencms.xml.types.I_CmsXmlContentValue;

/**
 * OpenCms xml content implementation.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class OpenCmsGreetingcardTemplateDao implements GreetingcardTemplateDao {

    public static final String CARD_SCHEMA_ELEMENT_IMAGE = "Image[1]";
    public static final String CARD_SCHEMA_ELEMENT_MAIL_BODY = "MailText[1]";
    public static final String CARD_TEXT_FIELD_SCHEMA_SUFFIX = "TextField";
    public static final String CARD_DESCRIPTION = "Description[1]";
    public static final String CARD_FONT_COLOR = "FontColor[1]";
    public static final String CARD_FONT_SIZE = "FontSize[1]";
    public static final String CARD_FONT_TYPE = "FontType[1]";
    public static final String CARD_FONT_ITALIC = "FontItalic[1]";
    public static final String CARD_FONT_BOLD = "FontBold[1]";
    public static final String CARD_FONT_UNDERLINE = "FontUnderline[1]";
    public static final String CARD_FONT_PLAIN = "FontPlain[1]";
    public static final String CARD_FONT_TOP_X = "PictureTop-X[1]";
    public static final String CARD_FONT_TOP_Y = "PictureTop-Y[1]";
    public static final String CARD_FONT_BOTTOM_X = "PictureBottom-X[1]";
    public static final String CARD_FONT_BOTTOM_Y = "PictureBottom-Y[1]";
    public static final String CONFIG_PATH = "/system/modules/com.synyx.greetingcards/config/configuration";
    public static final String DEFAULT_MAIL_BODY = "Eine Gru&szlig;karte f&uuml;r Sie! <br/> Die Gru&szlig;karte k&ouml;nnen Sie <a href=\"${url}\">hier</a> ansehen.";
    
    private CmsObject cms = null;
    
    public OpenCmsGreetingcardTemplateDao(CmsObject cms) {
        this.cms = cms;
    }
    
    public GreetingcardTemplate readCard(String path) throws DataAccessException {
                GreetingcardTemplate card = null;
        try {
            CmsFile file = cms.readFile(path);
            CmsXmlContent content = CmsXmlContentFactory.unmarshal(cms, file);

            Locale locale = cms.getRequestContext().getLocale();

            String image = content.getStringValue(cms, CARD_SCHEMA_ELEMENT_IMAGE, locale);

            String mailText = DEFAULT_MAIL_BODY;
            if (content.hasValue(CARD_SCHEMA_ELEMENT_MAIL_BODY, locale)) {
                mailText = content.getStringValue(cms, CARD_SCHEMA_ELEMENT_MAIL_BODY, locale);
            }

            List<TextField> fields = new ArrayList<TextField>();

            List values = content.getValues(CARD_TEXT_FIELD_SCHEMA_SUFFIX, locale);

            for (Iterator it = values.iterator(); it.hasNext();) {
                I_CmsXmlContentValue value = (I_CmsXmlContentValue) it.next();
                String textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_DESCRIPTION);
                String description = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_COLOR);
                String colorValue = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_SIZE);
                String fontSize = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_TYPE);
                String fontType = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_BOLD);
                CmsXmlBooleanValue fontBold = (CmsXmlBooleanValue) content.getValue(textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_ITALIC);
                CmsXmlBooleanValue fontItalic = (CmsXmlBooleanValue) content.getValue(textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_PLAIN);
                CmsXmlBooleanValue fontPlain = (CmsXmlBooleanValue) content.getValue(textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_UNDERLINE);
                CmsXmlBooleanValue fontUnderline = (CmsXmlBooleanValue) content.getValue(textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_TOP_X);
                String topX = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_TOP_Y);
                String topY = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_BOTTOM_X);
                String bottomX = content.getStringValue(cms, textfieldPath, locale);

                textfieldPath = CmsXmlUtils.concatXpath(value.getPath(), CARD_FONT_BOTTOM_Y);
                String bottomY = content.getStringValue(cms, textfieldPath, locale);

                TextField textField = new TextField();
                textField.setDescription(description);
                textField.setBold(fontBold.getBooleanValue());
                textField.setBottomX(Integer.parseInt(bottomX));
                textField.setBottomY(Integer.parseInt(bottomY));
                textField.setFontColor(colorValue);
                textField.setFontSize(Integer.parseInt(fontSize));
                textField.setFontType(fontType);
                textField.setItalic(fontItalic.getBooleanValue());
                textField.setPlain(fontPlain.getBooleanValue());
                textField.setTopX(Integer.parseInt(topX));
                textField.setTopY(Integer.parseInt(topY));
                textField.setUnderline(fontUnderline.getBooleanValue());

                fields.add(textField);
            }

            card = new GreetingcardTemplate();
            card.setImage(image);
            card.setTextFields(fields);
            card.setMailText(mailText);

        } catch (CmsException ex) {
            throw new DataAccessException(ex);
        }

        return card;

    }


}
