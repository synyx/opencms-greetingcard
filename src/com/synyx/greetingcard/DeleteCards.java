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
import com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.file.CmsResource;
import org.opencms.file.CmsResourceFilter;
import org.opencms.file.types.CmsResourceTypeImage;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.scheduler.I_CmsScheduledJob;
import org.synyx.opencms.tools.FindMaster;

/**
 *
 * @author Sandra Haggenmiller, Synyx GmbH & Co. KG.
 * @author <a href="mailto:daniel@synyx.de"Markus Daniel</a> Synyx GmbH & Co. KG Karlsruhe, Germany
 *			<i>check if this is the master host before launching the scheduled job</i>
 * @author <a href="mailto:hopf@synyx.de"Florian Hopf</a> Synyx GmbH & Co. KG Karlsruhe, Germany
 *                      <i>some cleanup and error safety</i>
 */
public class DeleteCards implements I_CmsScheduledJob {
    
    private static final Log log = CmsLog.getLog(DeleteCards.class);
    
    /**
     * This variable is written into the opencms log after beeing started.
     */
    private final static String LOG = "Synyx Greeting Card modul deleted cards from archive: ";
    
    
    /**
     * This is the conversion factor of milliseconds to days or the other way round
     */
    private final static long CONVERSION_FACTOR_DAYS_MILLISEC = 1000*60*60*24;
    
    
    /**
     * This method overrides the launch method of the I_CmsScheduledJob interface.
     * The launch method is called dependent on the cron job defnition.
     *
     * @param cmsObject The cmsobject to deal with.
     * @param map The parameters of the opencms.
     * @throws java.lang.Exception
     * @return The log to be written at the opencms log.
     */
    public String launch(CmsObject cmsObject, Map map) throws Exception {
        log.debug("Launch the scheduled job. Initialize the FindMaster with the master name : "
                + (String)map.get("MasterName"));
        FindMaster findMaster = new FindMaster( (String)map.get("MasterName"));
        if (findMaster.amIMaster()) {
            long currentTime = System.currentTimeMillis()/(CONVERSION_FACTOR_DAYS_MILLISEC);
            log.debug("CurrentTimeAsDate: " + new Date(System.currentTimeMillis()));
            
            GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(cmsObject);
            GreetingcardConfig config = configDao.readConfig();
            
            int deletionValue = config.getDeleteCardsAfterDays();
            
            //Get all resource from archive folder
            CmsResourceFilter filter = CmsResourceFilter.DEFAULT_FILES.requireType(CmsResourceTypeImage.getStaticTypeId());
            String folder = cmsObject.getRequestContext().removeSiteRoot(config.getArchiveFolder());
            List allResources = cmsObject.getResourcesInFolder(folder, filter);
            Iterator resourceIterator = allResources.iterator();
            int resourcesDeleted = 0;
            boolean online = setProjectOffline(cmsObject);
            while (resourceIterator.hasNext()) {

                //Getting created date of current greeting card
                CmsResource currentCard = (CmsResource) resourceIterator.next();
                long cardCreatedDay = currentCard.getDateCreated()/(CONVERSION_FACTOR_DAYS_MILLISEC);
                log.debug("Created Date: " + new Date(currentCard.getDateCreated()));

                //calculate deletion date for current greeting card
                long deletionDay = cardCreatedDay + deletionValue;
                
                if (deletionDay <= currentTime) {
                    cmsObject.lockResource(currentCard.getRootPath());
                    cmsObject.deleteResource(currentCard.getRootPath(), CmsResource.DELETE_REMOVE_SIBLINGS);
                    cmsObject.unlockResource(currentCard.getRootPath());
                    OpenCms.getPublishManager().publishResource(cmsObject, currentCard.getRootPath());

                    ++resourcesDeleted;
                }
            }

            if (online) {
                cmsObject.getRequestContext().setCurrentProject(cmsObject.readProject(CmsProject.ONLINE_PROJECT_ID));
            }
            return LOG + resourcesDeleted;
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
