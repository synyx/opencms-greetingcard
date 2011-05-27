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

import com.synyx.greetingcard.*;
import com.synyx.greetingcard.mail.MailTransmissionJob;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.db.CmsPublishList;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsContextInfo;
import org.opencms.main.CmsEvent;
import org.opencms.main.OpenCms;
import org.opencms.module.CmsModule;
import org.opencms.module.I_CmsModuleAction;
import org.opencms.report.I_CmsReport;
import org.opencms.scheduler.CmsScheduleManager;
import org.opencms.scheduler.CmsScheduledJobInfo;
import org.opencms.scheduler.CmsSchedulerException;
import org.opencms.security.CmsRoleViolationException;

/**
 * This class will be called at the startup of the server. It initializes
 * the needed cron jobs for the mail message queue.
 *
 *@author Rainer Steinegger, Synyx GmbH & Co. KG, steinegger@synyx.de
 *@author <a href="mailto:daniel@synyx.de"Markus Daniel</a> Synyx GmbH & Co. KG Karlsruhe, Germany
 *			check if the scheduled jobs are already there
 */
public class InitializeGreetingcard implements I_CmsModuleAction {
    
    /**
     * The Log for OpenCms.
     */
    private final Log log = LogFactory.getLog(InitializeGreetingcard.class);
    
    
    /**
     * The job name for the scheduler.
     */
    private final static String JOBNAME_TRANSMIT_MAIL = "Synyx Greeting Card Modul - Transmit Mail";
    
    /**
     * The class name for the scheduler.
     */
    private final static String CLASSNAME_TRANSMIT_MAIL = MailTransmissionJob.class.getName();
    
    /**
     * The cron expression for the scheduler.
     * This job executes the given class every 15 minutes of an hour.
     */
    private final static String CRONEXPRESSION_TRANSMIT_MAIL = "0 0/15 * * * ?";
    
    //FOR THE DELETION JOB:
    //=====================
    
    /**
     * The job name for the scheduler.
     */
    private final static String JOBNAME_DELETION_JOB = "Synyx Greeting Card Modul - Deletion Job";
    
    /**
     * The class name for the scheduler.
     */
    private final static String CLASSNAME_DELETION_JOB = DeleteCards.class.getName();
    
    /**
     * The cron expression for the scheduler.
     * This job executes the given class every 15 minutes of an hour.
     */
    private final static String CRONEXPRESSION_DELETION_JOB = "0 0 0 * * ?";
    
    
    /**
     * Creates a new instance of InitializeGreetingcard
     */
    public InitializeGreetingcard() {
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#initialize(CmsObject cmsObject, CmsConfigurationManager cmsConfigurationManager, CmsModule cmsModule)
     */
    public void initialize(CmsObject cmsObject, CmsConfigurationManager cmsConfigurationManager, CmsModule cmsModule) {
        boolean isTransmitMaiJobThere = false;
        boolean isDeletionJobThere = false;
        TreeMap map = new TreeMap();
        CmsContextInfo context = new CmsContextInfo(cmsObject.getRequestContext());

        CmsScheduleManager scheduleManager = OpenCms.getScheduleManager();
        List jobs = scheduleManager.getJobs();
        Iterator jobsIter = jobs.iterator();
        while (jobsIter.hasNext()) {
            CmsScheduledJobInfo jobInfo = ( (CmsScheduledJobInfo) jobsIter.next());
            log.debug("The name of the ScheduledClass is : " + jobInfo.getClassName());
            log.debug("The name of the ScheduledJob is : " + jobInfo.getJobName());
            if (jobInfo.getClassName().equals(CLASSNAME_TRANSMIT_MAIL)) {
                isTransmitMaiJobThere = true;
            }
            if (jobInfo.getClassName().equals(CLASSNAME_DELETION_JOB)) {
                isDeletionJobThere = true;
            }
        }
        //check if the necessary jobs are there or not
        if (!isDeletionJobThere) {
            try {
                // create a new schedule job at the opencms system to delete greeting cards from archive after a specified time
                CmsScheduledJobInfo scheduleDeletionJob = new CmsScheduledJobInfo(null, JOBNAME_DELETION_JOB,
                        CLASSNAME_DELETION_JOB, context, CRONEXPRESSION_DELETION_JOB, false, true, map);
                // add the new job to the schedule manager
                scheduleManager.scheduleJob(cmsObject, scheduleDeletionJob);
            } catch (CmsRoleViolationException ex) {
                log.error("Exception scheduling job", ex);
            } catch (CmsSchedulerException ex) {
                log.error("Exception scheduling job", ex);
            }
        } 
        if (!isTransmitMaiJobThere) {
            try {
                // create a new schedule job at the opencms system to transmit mails at the specified timestamp
                CmsScheduledJobInfo newScheduleJob = new CmsScheduledJobInfo(null, JOBNAME_TRANSMIT_MAIL,
                        CLASSNAME_TRANSMIT_MAIL, context, CRONEXPRESSION_TRANSMIT_MAIL, false, true, map);
                // add the new job to the schedule manager
                scheduleManager.scheduleJob(cmsObject, newScheduleJob);
            } catch (CmsRoleViolationException ex) {
                log.error("Exception scheduling job", ex);
            } catch (CmsSchedulerException ex) {
                log.error("Exception scheduling job", ex);
            }
        }
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#moduleUninstall(CmsModule cmsModule)
     */
    public void moduleUninstall(CmsModule cmsModule) {
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#moduleUpdate(CmsModule cmsModule)
     */
    public void moduleUpdate(CmsModule cmsModule) {
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#publishProject(CmsObject cmsObject, CmsPublishList cmsPublishList, int i, I_CmsReport i_CmsReport)
     */
    public void publishProject(CmsObject cmsObject, CmsPublishList cmsPublishList, int i, I_CmsReport i_CmsReport) {
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#shutDown(CmsModule cmsModule)
     */
    public void shutDown(CmsModule cmsModule) {
    }
    
    /**
     *@see org.opencms.module.I_CmsModuleAction#cmsEvent(CmsEvent cmsEvent)
     */
    public void cmsEvent(CmsEvent cmsEvent) {
    }
    
}
