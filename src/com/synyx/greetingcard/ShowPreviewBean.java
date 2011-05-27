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
import com.synyx.greetingcard.dao.TextField;
import com.synyx.greetingcard.dao.GreetingcardTemplate;
import com.synyx.greetingcard.cms.OpenCmsVFSService;
import com.synyx.greetingcard.cms.VFSService;
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.GreetingcardTemplateDao;
import com.synyx.greetingcard.dao.JdbcWhitelistDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardTemplateDao;
import com.synyx.greetingcard.dao.WhitelistDao;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencms.file.CmsObject;
import org.opencms.jsp.CmsJspActionElement;

/**
 * Bean that encapsulates the logic used in showPreviewBean.jsp
 * @author Rainer Steinegger, Synyx GmbH & Co. KG
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class ShowPreviewBean {

    private boolean sendNow;
    private boolean isDateInPast;
    private boolean isTooManyDays;
    private String url;
    private String dateDay;
    private String dateMonth;
    private String dateYear;
    private String dateHour;
    private String dateMinute;
    private String authorAddress;
    private String receiverAddress;
    private String subject;
    private String authorName;
    private String receiverName;
    private String resourceName;
    private long transmitTime;
    
    private boolean error = false;

    private Log log = LogFactory.getLog(ShowPreviewBean.class);
    
    public void init(PageContext context, HttpServletRequest request, HttpServletResponse response) throws JspException {

        sendNow = (request.getParameter("sendNow") != null && request.getParameter("sendNow").equals("0"));

        dateDay = request.getParameter("date_day");
        dateMonth = request.getParameter("date_month");
        dateYear = request.getParameter("date_year");
        dateHour = request.getParameter("date_hour");
        dateMinute = request.getParameter("date_minute");

        // check if the entered date is valid, if not, the user has a second chance to enter it, either the mail becomes send the next possible date
        // get the entered date from the request
        int numericDateDay = Integer.parseInt(dateDay);
        int numericDateMonth = Integer.parseInt(dateMonth);
        int numericDateYear = Integer.parseInt(dateYear);
        int numericDateHour = Integer.parseInt(dateHour);
        int numericDateMinute = Integer.parseInt(dateMinute);

        // get the amount days for the selected year and month
        int daysOfTheMonth = DateBean.getAmountDays(numericDateMonth, numericDateYear);

        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        long currentLong = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, numericDateHour);
        calendar.set(Calendar.MINUTE, numericDateMinute);
        calendar.set(Calendar.DATE, numericDateDay);
        calendar.set(Calendar.MONTH, numericDateMonth - 1);
        calendar.set(Calendar.YEAR, numericDateYear);
        transmitTime = calendar.getTimeInMillis();

        isDateInPast = !isSendNow() && transmitTime < currentLong;
        isTooManyDays = !isSendNow() && numericDateDay > daysOfTheMonth;

        if (!isIsDateInPast() && !isIsTooManyDays()) {
            try {
                createCard(context, request, response);
            } catch (RuntimeException ex) {
                error = true;
                throw ex;
            } catch (Exception ex) {
                log.error("Error occured trying to create card", ex);
                error = true;
            }
        }
    }

    public boolean hasError() {
        return error;
    }
    
    public boolean isDateInPast() {
        return isIsDateInPast();
    }

    public boolean isTooManyDays() {
        return isIsTooManyDays();
    }

    private void createCard(PageContext context, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CmsJspActionElement jsp = new CmsJspActionElement(context, request, response);
        CmsObject cmsObject = jsp.getCmsObject();

        // the text is send by the parameter text0, text1, .. depending on how much textfields are available
        // this text fields are reveived now
        // TODO this is currently not used
        int whichTextField = 0;
        List<String> text = new ArrayList<String>();
        while (request.getParameter("text" + whichTextField) != null) {
            text.add(request.getParameter("text" + whichTextField));
            whichTextField++;
        }
        // to get the text information its necessary to know the path of the xml content
        String cardPath = request.getParameter("greetingcard_fileName");

        //get the parameters from the request
        authorName = request.getParameter("author_name");
        authorAddress = request.getParameter("author_address");
        receiverName = request.getParameter("receiver_name");
        if (request.getParameter("receiver_address") != null) {
            receiverAddress = request.getParameter("receiver_address");
        } else {
            receiverAddress = request.getParameter("receiver_address_whitelist") + "@" + request.getParameter("receiver_domain");
        }
        subject = request.getParameter("subject");

        VFSService service = new OpenCmsVFSService(jsp);
        GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(cmsObject);
        GreetingcardTemplateDao templateDao = new OpenCmsGreetingcardTemplateDao(cmsObject);
        CmsContext cmsContext = new OpenCmsContext(jsp);
        
        GreetingcardService greetingcardService = new DefaultGreetingcardService(jsp, configDao, cmsContext);
        
        createCard(templateDao, greetingcardService, cmsContext, service, text, cardPath);
        
    }

    private void createCard(GreetingcardTemplateDao templateDao, GreetingcardService greetingcardService,  
            CmsContext cmsContext, VFSService vfsService, List<String> text, String cardPath) throws DataAccessException, IOException {
        GreetingcardTemplate card = templateDao.readCard(cardPath);
        GreetingcardConfig config = greetingcardService.getGreetingcardConfig();

        String imagePath = card.getImage();

        // generate the picture and initialize an instance of the image creater class for adding the received text to the picture
        String newUrl = "";
        String pictureFileName = cmsContext.getServerPath(cmsContext.link(imagePath));
        Image backgroundImage = ImageIO.read(new URL(pictureFileName));
        ImageCreator imageCreator = new ImageCreator(backgroundImage, authorName, receiverName, config.getArchiveFolder());

        // add the text to the picture
        for (int i = 0; i < card.getTextFields().size(); i++) {
            TextField field = card.getTextFields().get(i);
            String currentText = text.get(i);
            String fontColor = field.getFontColor();
            String fontType = field.getFontType();
            int fontSize = field.getFontSize();
            int pictureTopX = field.getTopX();
            int pictureTopY = field.getTopY();
            int pictureBottomX = field.getBottomX();
            int pictureBottomY = field.getBottomY();
            imageCreator.addText(pictureTopX, pictureTopY, pictureBottomX, pictureBottomY, currentText, fontSize,
                    fontColor, field.isItalic(), field.isUnderline(), field.isBold(), field.isPlain(), fontType);
        }

        // get the generated image from the image creater and save it
        RenderedImage newPicture = (RenderedImage) imageCreator.getFile();
        // the image creater class also handles the new filename (with the path) of the picture to save at
        newUrl = cmsContext.removeSiteRoot(imageCreator.getNewLocation());

        WhitelistDao whitelistDao = new JdbcWhitelistDao(config);

        boolean internal = greetingcardService.isInternal(whitelistDao, receiverAddress);

        vfsService.writeImage(newPicture, newUrl, internal);

        resourceName = newUrl + ".png";
        url = cmsContext.link(resourceName);

    }
    
    public boolean isSendNow() {
        return sendNow;
    }

    public boolean isIsDateInPast() {
        return isDateInPast;
    }

    public boolean isIsTooManyDays() {
        return isTooManyDays;
    }

    public String getUrl() {
        return url;
    }

    public String getDateDay() {
        return dateDay;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public String getDateYear() {
        return dateYear;
    }

    public String getDateHour() {
        return dateHour;
    }

    public String getDateMinute() {
        return dateMinute;
    }

    public String getAuthorAddress() {
        return authorAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public long getTransmitTime() {
        return transmitTime;
    }

    public String getResourceName() {
        return resourceName;
    }
}
