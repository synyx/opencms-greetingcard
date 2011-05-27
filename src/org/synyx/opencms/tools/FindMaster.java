/*
 * NetworkClient.java
 *
 * Created on 17. Mai 2007, 07:29
 *
 */

package org.synyx.opencms.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains functionality to find out if this host is the "master" host.
 * The master is that host which's ip address machtes with the given host name.
 *
 * @author <a href="mailto:daniel@synyx.de"Markus Daniel</a> Synyx GmbH & Co. KG Karlsruhe, Germany
 */
public class FindMaster {

	/** For debugging or error purpose we want to log.*/
	private Log log = LogFactory.getLog(this.getClass());
	
	private String masterName = null;
		
	private InetAddress inetAddress = null;
	
	/** Creates a new instance of NetworkClient. */
	public FindMaster(String masterName) {
		this.setMasterName(masterName);
	}
	
	/**
	 * Method to find out if this host is the specified "master" host.
	 *
	 * @return <code>true</code> if this host has the specified "master" name, otherwise <code>false</code>.
	 */
	public boolean amIMaster() {
		boolean amIMaster = false;
		try {
			inetAddress = InetAddress.getByName(getMasterName());
			log.debug("The name of the host is : " + inetAddress.getHostName());
			log.debug("The address of the host is : " + inetAddress.getHostAddress());
			try {
				// check if this host has an interface with the wished name
				if (NetworkInterface.getByInetAddress(inetAddress) != null) {
					log.debug("This host has an interface with the specified host name.");
					amIMaster = true;
				} else {
					log.debug("This host has not an interface with the specified host name.");
				}
			} catch (SocketException ex) {
				log.error("", ex);
			}
		} catch (UnknownHostException ex) {
			log.error("", ex);
		}
		return amIMaster;
	}

	/**
	 * 
	 * @return the master name of null if no name is set.
	 */
	public String getMasterName() {
		return masterName;
	}

	/**
	 * 
	 * @param masterName 
	 */
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	
}
