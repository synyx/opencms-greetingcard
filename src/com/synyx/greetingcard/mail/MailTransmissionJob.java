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

import com.synyx.greetingcard.dao.OpenCmsGreetingcardDao;
import com.synyx.greetingcard.dao.GreetingcardDao;
import com.synyx.greetingcard.dao.Greetingcard;
import com.synyx.greetingcard.dao.GreetingcardConfig;
import com.synyx.greetingcard.*;
import com.synyx.greetingcard.dao.GreetingcardConfigDao;
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsFile;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.scheduler.I_CmsScheduledJob;
import org.synyx.opencms.tools.FindMaster;


/**
 * This class is used by the greetingcards opencms modul. It becomes by the method sendMail(...)
 * the needed parameters and submits the mail, when the server has enough resources and the
 * time parameter is reached.
 *
 * @author Rainer Steinegger, Synyx GmbH & Co. KG, steinegger@synyx.de
 * @author Florian Hopf, Synyx GmbH & Co. KG, hopf@synyx.de
 * @author <a href="mailto:daniel@synyx.de"Markus Daniel</a> Synyx GmbH & Co. KG Karlsruhe, Germany
 *			<li>check if this is the master host before launching the scheduled job</li>
 */
public class MailTransmissionJob implements I_CmsScheduledJob {
    
    private static final Log log = CmsLog.getLog(MailTransmissionJob.class);

    /**
     * This variable is written into the opencms log after beeing started.
     */
    private final static String LOG = "Synyx Greeting Card modul sent mails.";
    
    /**
     * This method traverses the elements of the array list and returns the mails,
     * which are now able to send (time of mail < currentTime).
     *
     * @param currentTime The time of the system.
     * @return The elements which are now able to send (time of mail < currentTime).
     */
    private List<CmsResource> getElementsToSend(long currentTime, CmsObject cmsObject) {
        List<CmsResource> elementsToSend = new ArrayList<CmsResource>();
        // we only want to notice slight differences, so we use seconds instead of ms
        long currentSeconds = currentTime/1000 + 60;
        try {
            GreetingcardConfigDao dao = new OpenCmsGreetingcardConfigDao(cmsObject);
            GreetingcardConfig config = dao.readConfig();
            String folder = cmsObject.getRequestContext().removeSiteRoot(config.getTempFolder());
            boolean online = setProjectOffline(cmsObject);
            List allResources = cmsObject.getResourcesInFolder(folder, CmsResourceFilter.ALL);
            
            for (Object resourceObj: allResources) {
                CmsResource currentImage = (CmsResource) resourceObj;
                String imageUrl = cmsObject.getSitePath(currentImage);
                
                String archivePath =  cmsObject.getRequestContext().removeSiteRoot(config.getArchiveFolder());
                String newUrl = archivePath + currentImage.getName();
                try {
                    // read the property time
                    long cardSeconds = Long.parseLong(cmsObject.readPropertyObject(currentImage, "greetingCardTransmitTime", false).getValue()) / 1000;
                    if (cardSeconds <= currentSeconds) {
                        cmsObject.lockResource(currentImage.getRootPath());
                        cmsObject.moveResource(currentImage.getRootPath(), newUrl);
                        OpenCms.getPublishManager().publishResource(cmsObject, newUrl);
                        // wait until we can be sure that this resource is published
                        OpenCms.getPublishManager().waitWhileRunning();
                        CmsFile newRes = cmsObject.readFile(newUrl);
                        elementsToSend.add(newRes);
                    }
                } catch (NumberFormatException nfex) {
                    OpenCms.getLog(this.getClass()).error("Couldn't send one of the mails (" + imageUrl + ")", nfex);
                }
                
            }
            if (online) {
                cmsObject.getRequestContext().setCurrentProject(cmsObject.readProject(CmsProject.ONLINE_PROJECT_NAME));
            }
            
        } catch (CmsException ex) {
            OpenCms.getLog(this.getClass()).error("", ex);
        } catch (Exception ex) {
            OpenCms.getLog(this.getClass()).error("", ex);
        }
        return elementsToSend;
    }
    
    /**
     * These method is from the I_CmsScheduledJob interface. At the constructor of this
     * class the schedule job is written into the opencms system. The launch method is
     * called dependent on the cron job defnition.
     *
     * @param cmsObject The cmsobject to deal with.
     * @param map The parameters of the opencms.
     * @throws java.lang.Exception
     * @return The log to be written at the opencms log.
     */
    public String launch(CmsObject cms, Map map) throws Exception {
        
        log.debug("Launch the scheduled job. Initialize the FindMaster with the master name : "
                + (String)map.get("MasterName"));
        
        FindMaster findMaster = new FindMaster( (String)map.get("MasterName"));
        
        if (findMaster.amIMaster()) {
            GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(cms);
            GreetingcardConfig config = configDao.readConfig();
            GreetingcardDao dao = new OpenCmsGreetingcardDao(cms, config);
            OpenCmsMailTransmission transmission = new OpenCmsMailTransmission(cms, config, new OpenCmsMailService());
            int amountSend = 0;
            long currentTime = System.currentTimeMillis();
            List<CmsResource> toSend = getElementsToSend(currentTime, cms);
            // iterate over all elements and send them
            for (CmsResource currentImage: toSend) {
                
                String path = cms.getRequestContext().removeSiteRoot(currentImage.getRootPath());
                
                Greetingcard cardToSend = dao.readFromQueue(path);
                cardToSend.setTransmitDate(null);
                
                transmission.sendOrQueue(cardToSend);

                amountSend++;
            }
            
            return LOG + amountSend;
        } else {
            return "This is not the master so won't launch.";
        }
    }
    
    private boolean setProjectOffline(CmsObject cms) throws CmsException {
        boolean online = cms.getRequestContext().currentProject().isOnlineProject();
        if (online) {
            cms.getRequestContext().setCurrentProject(cms.readProject("Offline"));
        }
        return online;
    }
    
}
