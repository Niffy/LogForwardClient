package com.niffy.logforwarder.client.parser;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.niffy.logforwarder.client.Setting;
import com.niffy.logforwarder.client.tags.SettingsTags;

public class SettingsParser extends DefaultHandler {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(SettingsParser.class);

	// ===========================================================
	// Fields
	// ===========================================================
	protected final StringBuilder mStringBuilder = new StringBuilder();
	protected ArrayList<Setting> mSettings = new ArrayList<Setting>();
	protected Setting tempObject = new Setting(); 
	protected int mHighestID = 0;
	// ===========================================================
	// Constructors
	// ===========================================================

	public SettingsParser() {

	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(SettingsTags.SETTINGS_SETTINGS)) {
			// Do nothing
		} else if (qName.equals(SettingsTags.SETTINGS_SETTING)) {
			this.tempObject = new Setting();
		} else if (qName.equals(SettingsTags.SETTINGS_ID)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_NAME)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_BUFFER)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_SERVER_PORT)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_STORAGE_PATH)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_FILENAME_PATH)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_SDCARD)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_DEVICES)) {
			// IGNORE do at endElement
		} else if (qName.equals(SettingsTags.SETTINGS_DEVICES_DEVICE)) {
			// IGNORE do at endElement
		} else {
			log.warn("Unknown start element in settings parser: {}", qName);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(SettingsTags.SETTINGS_SETTINGS)) {
			// Do nothing
		} else if (qName.equals(SettingsTags.SETTINGS_SETTING)) {
			this.mSettings.add(tempObject.getID(), tempObject);
		} else if (qName.equals(SettingsTags.SETTINGS_ID)) {
			this.tempObject.setID(Integer.parseInt(this.mStringBuilder.toString().trim()));
			if(this.tempObject.getID() > this.mHighestID){
				this.mHighestID = this.tempObject.getID();
			}
		} else if (qName.equals(SettingsTags.SETTINGS_NAME)) {
			this.tempObject.setName(this.mStringBuilder.toString().trim());
		} else if (qName.equals(SettingsTags.SETTINGS_BUFFER)) {
			this.tempObject.setBuffer(Integer.parseInt(this.mStringBuilder.toString().trim()));
		} else if (qName.equals(SettingsTags.SETTINGS_SERVER_PORT)) {
			this.tempObject.setServerPort(Integer.parseInt(this.mStringBuilder.toString().trim()));
		} else if (qName.equals(SettingsTags.SETTINGS_STORAGE_PATH)) {
			this.tempObject.setStoragePath(this.mStringBuilder.toString().trim());
		} else if (qName.equals(SettingsTags.SETTINGS_FILENAME_PATH)) {
			this.tempObject.setFileNamePath(this.mStringBuilder.toString().trim());
		} else if (qName.equals(SettingsTags.SETTINGS_SDCARD)) {
			this.tempObject.setSDCard(Boolean.parseBoolean(this.mStringBuilder.toString().trim()));
		} else if (qName.equals(SettingsTags.SETTINGS_DEVICES)) {
			// Nothing to add
		} else if (qName.equals(SettingsTags.SETTINGS_DEVICES_DEVICE)) {
			this.tempObject.addDevices(this.mStringBuilder.toString().trim());
		} else {
			log.warn("Unknown end element in settings parser: {}", qName);
		}
		this.mStringBuilder.setLength(0);
	}

	@Override
	public void characters(char[] pCharacters, int pStart, int pLength) throws SAXException {
		this.mStringBuilder.append(pCharacters, pStart, pLength);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public ArrayList<Setting> getSettings() {
		return this.mSettings;
	}
	
	/**
	 * Get the highest ID found.
	 * @return
	 */
	public int getHighestID(){
		return this.mHighestID;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
