/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.policyrulestransmitter;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 S2 Grupo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import eu.musesproject.client.model.decisiontable.PolicyDT;
import eu.musesproject.server.connectionmanager.ConnectionManager;
import eu.musesproject.server.connectionmanager.DataHandler;
import eu.musesproject.server.connectionmanager.IConnectionManager;
import eu.musesproject.server.risktrust.Device;

/**
 * Class PolicyTransmitter
 * 
 * @author Sergio Zamarripa (S2)
 * @version Oct 7, 2013
 */

public class PolicyTransmitter {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private List<DataHandler> dataHandlerList	 = new CopyOnWriteArrayList<DataHandler>();
	private ConnectionManager connManager;
	
	public PolicyTransmitter() {
		connManager = ConnectionManager.getInstance();
	}

	/**
	 * Info D
	 * 
	 * Once the policy decision table has been computed, this method sends this
	 * policy to the device
	 * 
	 * @param policy
	 * 
	 * @param device
	 * 
	 * @return result of sending the policy
	 */

	public Integer sendPolicyDT(PolicyDT policy, Device device) {
		sendData(policy.getRawPolicy());
		return 1;
	}
	
	private void sendData(String dataToSend){
		
		logger.info("Retrieving session Ids...");
		logger.info("PolicyTransmitter sendData. Data to be sent:"+dataToSend);
		Set<String> sessionIds = connManager.getSessionIds();
		logger.info("Number of ids:"+sessionIds.size());
		if (sessionIds.size()==0){
			logger.info("No sessions available in the connection manager");
		}
		for (Iterator iterator = sessionIds.iterator(); iterator.hasNext();) {
			String sessionId = (String) iterator.next();
			logger.info("Sending data with sessionId:"+sessionId);
			logger.info(connManager.getSessionDetails(sessionId).getId());//TODO Identify the concrete sessionId coming from the device
			connManager.sendData(sessionId, dataToSend);
		}
		
		
		
	}

}
