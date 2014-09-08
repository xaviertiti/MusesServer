package eu.musesproject.server.contextdatareceiver;

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



import java.awt.Desktop.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.server.eventprocessor.util.EventTypes;


public class JSONManager {
	public Logger logger = Logger.getLogger(JSONManager.class.getName());
	
	/**
	 * Method to be called by the ConnectionManager/DataHandler when the message is originated from the sensors
	 * @param String root of the JSON message
	 * @return void
	 * @throws JSONException
	 * 
	 */
	
	public static List<ContextEvent> processJSONMessage(String message,	String requestType) {
		// Action action = null;
		Map<String, String> properties = null;
		ContextEvent contextEvent = null;
		String username = null;
		String deviceId = null;
		List<ContextEvent> resultList = new ArrayList<ContextEvent>();
		if (requestType.equals(RequestType.UPDATE_CONTEXT_EVENTS)) {
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Update context events JSONMessage received: Processing message...");
		} else if ((requestType.equals(RequestType.ONLINE_DECISION)||(requestType.equals(RequestType.LOCAL_DECISION)))) {// TODO Remove LOCAL_DECISION when sensors are updated conveniently
			Logger.getLogger(JSONManager.class)
					.log(Level.INFO,
							"Online decision JSONMessage received: Processing message...");

			try {
				// Process the root JSON object
				JSONObject root = new JSONObject(message);
				
				// Get the action part
				JSONObject actionJson = root
						.getJSONObject(JSONIdentifiers.ACTION_IDENTIFIER);

				contextEvent = extractActionContextEvent(actionJson);
				resultList.add(contextEvent);

				// Get the List<ContextEvent> from each sensor
				JSONObject sensorJson = root
						.getJSONObject(JSONIdentifiers.SENSOR_IDENTIFIER);

				for (Iterator iterator = sensorJson.keys(); iterator.hasNext();) {
					String contextEventType = (String) iterator.next();
					JSONObject contextEventJson = sensorJson
							.getJSONObject(contextEventType);
					contextEvent = extractContextEvent(contextEventJson);
					Logger.getLogger(JSONManager.class.getName()).log(
							Level.INFO, "A new event has been received.");
					printContextEventInfo(contextEvent);
					resultList.add(contextEvent);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (requestType.equals(RequestType.USER_ACTION)) {
			
			// Process the root JSON object
			JSONObject root;
			try {
				root = new JSONObject(message);
				// TODO Get the behavior part
				JSONObject behaviorJson = root
						.getJSONObject(JSONIdentifiers.USER_BEHAVIOR);
				contextEvent = new ContextEvent();
				contextEvent.setType(EventTypes.USERBEHAVIOR);
				properties = new HashMap<String,String>();
				for (Iterator iterator = behaviorJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						String value = behaviorJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
				Logger.getLogger(JSONManager.class.getName()).log(
						Level.INFO, "A new event has been received.");
				printContextEventInfo(contextEvent);
				resultList.add(contextEvent);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return resultList;

	}
	
	public static List<ContextEvent> processJSONMessage(String message){
		//Action action = null;
		Map<String,String> properties = null;
		ContextEvent contextEvent = null;
		List<ContextEvent> resultList = new ArrayList<ContextEvent>();
		Logger.getLogger(JSONManager.class).info("JSONMessage received: Processing message...");
		try {
			// Process the root JSON object
			JSONObject root = new JSONObject(message);		
			//TODO Get the action part
			JSONObject actionJson = root.getJSONObject(JSONIdentifiers.ACTION_IDENTIFIER);
			

			contextEvent = extractActionContextEvent(actionJson);
			if (contextEvent!=null){
				resultList.add(contextEvent);
			}	
			
			// Get the List<ContextEvent> from each sensor			
			JSONObject sensorJson = root.getJSONObject(JSONIdentifiers.SENSOR_IDENTIFIER);

			for (Iterator iterator = sensorJson.keys(); iterator.hasNext();) {
				String contextEventType = (String) iterator.next();
				JSONObject contextEventJson = sensorJson.getJSONObject(contextEventType);
				contextEvent = extractContextEvent(contextEventJson);
				Logger.getLogger(JSONManager.class.getName()).log(Level.INFO, "A new event has been received.");
				printContextEventInfo(contextEvent);
				if (contextEvent == null){
					Logger.getLogger(JSONManager.class).log(Level.INFO, "Extracted event as null! Original message:"+contextEventJson);
				}else{
					resultList.add(contextEvent);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Logger.getLogger(JSONManager.class).info("Exception with JSON message:"+message);
			Logger.getLogger(JSONManager.class).info("*******");
		}
		
		return resultList;
		
	}
	
	public static void printContextEventInfo(ContextEvent contextEvent){
		Map<String, String> properties = null;
		if ((contextEvent!=null)&&(contextEvent.getType()!=null)){
			Logger.getLogger(JSONManager.class.getName()).log(Level.INFO, "		Event type:" + contextEvent.getType());
		}
		properties = contextEvent.getProperties();
		if (properties != null){
			for (Map.Entry<String, String> entry : properties.entrySet())
			{
				Logger.getLogger(JSONManager.class.getName()).log(Level.INFO, "		" + entry.getKey() + "/" + entry.getValue());
			}
		}
	}

	
	/**
	 * conversion of JSONObject of each one of the context events in the list of each sensor
	 * @param JSONObject 
	 * @return ContextEvent 
	 * @throws JSONException
	 */
	private static ContextEvent extractContextEvent(JSONObject contextEventJson) throws JSONException {

		//TODO Retrieve context event of each JSONObject		
		ContextEvent contextEvent = null;
		Map<String,String> properties = null;
		String contextEventType = null;
		String value = null;
		
		try{
			contextEventType = contextEventJson.getString(ContextEvent.KEY_TYPE);
		
			if ((contextEventJson != null)&&(contextEventType != null)){
				contextEvent = new ContextEvent();
				contextEvent.setType(contextEventType);
				contextEvent.setTimestamp(contextEventJson.getLong(ContextEvent.KEY_TIMESTAMP));
				properties = new HashMap<String,String>();
				for (Iterator iterator = contextEventJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						value = contextEventJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return contextEvent;
	}
	
	/**
	 * conversion of JSONObject of each one of the context events in the list of each sensor
	 * @param JSONObject 
	 * @return ContextEvent 
	 * @throws JSONException
	 */
	private static ContextEvent extractActionContextEvent(JSONObject contextEventJson) throws JSONException {
	
		ContextEvent contextEvent = null;
		Map<String,String> properties = null;
		String contextEventType = null;
		String value = null;
		
		try{
			contextEventType = contextEventJson.getString(ContextEvent.KEY_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
			//TODO Tweaked for the case where type of the action is not completed, remove this when it is fixed
			contextEventType = "open_asset";
			
		}
		try{
			if ((contextEventJson != null)&&(contextEventType != null)){
				contextEvent = new ContextEvent();
				contextEvent.setTimestamp(contextEventJson.getLong(ContextEvent.KEY_TIMESTAMP));
				properties = new HashMap<String,String>();
				for (Iterator iterator = contextEventJson.keys(); iterator.hasNext();) {
					String key = (String) iterator.next();
					if ((!key.equals(ContextEvent.KEY_TYPE))&&(!key.equals(ContextEvent.KEY_TIMESTAMP))){
						value = contextEventJson.getString(key);
						properties.put(key, value);
					}
				}
				contextEvent.setProperties(properties);
				if (contextEventType.equals(ActionType.OPEN_ASSET)||contextEventType.equals(ActionType.ACCESS)||contextEventType.equals(ActionType.OPEN)){
					contextEvent.setType(EventTypes.FILEOBSERVER);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.OPEN_APPLICATION)){
					contextEvent.setType(EventTypes.APPOBSERVER);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.SEND_MAIL)){
					contextEvent.setType(EventTypes.SEND_MAIL);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.VIRUS_FOUND)){
					contextEvent.setType(EventTypes.VIRUS_FOUND);
					properties.put("event", contextEventType);
				}else if (contextEventType.equals(ActionType.UPDATE)){
					Logger.getLogger(JSONManager.class).log(Level.INFO, "Action type for update of events");
					return null; //This is not a concrete type of action, it just reflects that the list of events is an update_events request type
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			//TODO Tweaked for the case where type of the action is not completed
			
			return null;
		}

		return contextEvent;
	}
	
	public static JSONObject createJSON(String requestType, String authResult, String authMessage) {
		JSONObject root = new JSONObject();
		try {

            root.put(JSONIdentifiers.REQUEST_TYPE_IDENTIFIER, requestType);
            
            root.put("auth-result", authResult);
            
            root.put("auth-message", authMessage);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return root;
	}

}
