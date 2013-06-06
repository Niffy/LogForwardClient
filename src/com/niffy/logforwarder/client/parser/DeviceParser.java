package com.niffy.logforwarder.client.parser;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.niffy.logforwarder.client.Device;
import com.niffy.logforwarder.client.tags.DeviceTags;

public class DeviceParser extends DefaultHandler {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(DeviceParser.class);

	// ===========================================================
	// Fields
	// ===========================================================
	protected final StringBuilder mStringBuilder = new StringBuilder();
	protected HashMap<String, Device> mDevices = new HashMap<String, Device>();
	protected Device tempObject = new Device();

	// ===========================================================
	// Constructors
	// ===========================================================

	public DeviceParser() {

	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(DeviceTags.DEVICE_DEVICES)) {
			// IGNORE
		} else if (qName.equals(DeviceTags.DEVICE_DEVICE)) {
			this.tempObject = new Device();
		} else if (qName.equals(DeviceTags.DEVICE_ID)) {
			// IGNORE do at endElement
		} else if (qName.equals(DeviceTags.DEVICE_NAME)) {
			// IGNORE do at endElement
		} else if (qName.equals(DeviceTags.DEVICE_IP)) {
			// IGNORE do at endElement
		} else if (qName.equals(DeviceTags.DEVICE_PORT)) {
			// IGNORE do at endElement
		} else if (qName.equals(DeviceTags.DEVICE_FILENAME)) {
			// IGNORE do at endElement
		} else {
			log.warn("Unknown start element in device parser: {}", qName);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(DeviceTags.DEVICE_DEVICES)) {
			//IGNORE
		} else if (qName.equals(DeviceTags.DEVICE_DEVICE)) {
			this.mDevices.put(tempObject.getName(), tempObject);
		} else if (qName.equals(DeviceTags.DEVICE_ID)) {
			tempObject.setID(Integer.parseInt(this.mStringBuilder.toString().trim()));
		} else if (qName.equals(DeviceTags.DEVICE_NAME)) {
			tempObject.setName(this.mStringBuilder.toString().trim());
		} else if (qName.equals(DeviceTags.DEVICE_IP)) {
			tempObject.setAddress(this.mStringBuilder.toString().trim());
		} else if (qName.equals(DeviceTags.DEVICE_PORT)) {
			tempObject.setPort(Integer.parseInt(this.mStringBuilder.toString().trim()));
		} else if (qName.equals(DeviceTags.DEVICE_FILENAME)) {
			tempObject.setFileName(this.mStringBuilder.toString().trim());
		} else {
			log.warn("Unknown end element in device parser: {}", qName);
		}
		this.mStringBuilder.setLength(0);
	}

	@Override
	public void characters(final char[] pCharacters, final int pStart, final int pLength) throws SAXException {
		this.mStringBuilder.append(pCharacters, pStart, pLength);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public HashMap<String, Device> getDevices() {
		return this.mDevices;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
