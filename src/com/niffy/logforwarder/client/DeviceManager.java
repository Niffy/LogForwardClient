package com.niffy.logforwarder.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.niffy.logforwarder.client.parser.DeviceParser;

public class DeviceManager {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(DeviceManager.class);

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<Device> mDevices = new ArrayList<Device>();
	private String mDeviceFile;
	private AtomicInteger mDeviceIDToUse;
	private SettingManager mSettingManager;

	// ===========================================================
	// Constructors
	// ===========================================================

	public DeviceManager() {

	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setSettingManager(final SettingManager pSettingManager) {
		this.mSettingManager = pSettingManager;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	public void readInDeviceFile(final String pDeviceFilePath) {
		log.debug("Reading in devices from: {}", pDeviceFilePath);
		this.mDeviceFile = pDeviceFilePath;
		InputSource is = null;
		try {
			InputStream inputStream = new FileInputStream(this.mDeviceFile);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
		} catch (FileNotFoundException e) {
			log.error("File not found: {}", pDeviceFilePath, e);
			return;
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException: {}", pDeviceFilePath, e);
			return;
		}

		try {
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();
			final XMLReader xr = sp.getXMLReader();
			final DeviceParser parser = new DeviceParser();
			xr.setContentHandler(parser);
			xr.parse(is);
			this.mDevices = parser.getDevices();
			this.mDeviceIDToUse = new AtomicInteger(parser.getHighestID());
		} catch (final SAXException e) {
			log.error("SAXException loading devices", e);
		} catch (final ParserConfigurationException e) {
			log.error("ParserConfigurationException loading devices", e);
		} catch (final IOException e) {
			log.error("IOException loading devices", e);
		}
	}

	public Device getDevice(final int pDeviceNumber) {
		for (Device device : this.mDevices) {
			if (device.getID() == pDeviceNumber) {
				return device;
			}
		}
		log.warn("Could not find device: {} in device list", pDeviceNumber);
		return null;
	}

	/**
	 * Loop devices for debug printout
	 */
	public void loopDevices() {
		log.debug("Looping Devices: {}", this.mDevices.size());
		for (Device device : this.mDevices) {
			Object[] array = { device.getID(), device.getName(), device.getAddress(), device.getPort(),
					device.getFileName() };
			log.debug("Device ID: {} Name: {} Address: {}  Port: {} Filename: {}", array);
		}
	}
	
	public void listDevices() {
		StringBuilder builder = new StringBuilder();
		for (Device device : this.mDevices) {
			builder.append("ID: ");
			builder.append(device.getID());
			builder.append(" Device: ");
			builder.append(device.getName());
			builder.append(" File: ");
			builder.append(device.getFileName());
			builder.append(" Port: ");
			builder.append(device.getPort());
			builder.append(" IP: ");
			builder.append(device.getAddress());
			log.info(builder.toString());
			builder.setLength(0);
		}
	}

	public void createDevice(final String pName, final String pIP, final String pPort, final String pFileName) {
		final int port = (pPort == null) ? Defaults.CLIENT_PORT : Integer.parseInt(pPort);
		if (pName == null || pIP == null || pFileName == null) {
			log.info("Required attributes not found");
			if (pName == null)
				log.info("Name required");
			if (pIP == null)
				log.info("IP required");
			if (pFileName == null)
				log.info("File name of log required");
		} else {
			Device device = new Device();
			device.setID(this.mDeviceIDToUse.incrementAndGet());
			device.setName(pName);
			device.setAddress(pIP);
			device.setFileName(pFileName);
			device.setPort(port);
			this.mDevices.add(device.getID(), device);
		}
	}

	public void deleteDevice(final String pID) {
		if (pID == null) {
			log.info("Cannot delete device if device ID is not passed");
		} else {
			/*
			 * TODO locate device and delete, also search settings for device to delete as well.
			 */
		}
	}

	public void updateDevice(final String pID, final String pName, final String pIP, final String pPort,
			final String pFileName) {
		if (pID == null) {
			log.info("Require device ID!");
			return;
		}
		final int id = Integer.parseInt(pID);
		Device device = this.mDevices.get(id);
		final int port = (pPort == null) ? device.getPort() : Integer.parseInt(pPort);
		final String name = (pName == null) ? device.getName() : pName;
		final String ip = (pIP == null) ? device.getAddress() : pIP;
		final String filename = (pFileName == null) ? device.getFileName() : pFileName;
		if (device != null) {
			if (device.getID() == id) {
				if (device.getName().compareToIgnoreCase(name) != 0)
					device.setName(name);
				if (device.getAddress().compareToIgnoreCase(ip) != 0)
					device.setAddress(ip);
				if (device.getPort() != port)
					device.setPort(port);
				if (device.getFileName().compareToIgnoreCase(filename) != 0)
					device.setFileName(filename);
				log.info("Updated device profile: {} . REMEMBER TO CALL -w IN DEVICE MODE", id);
			} else {
				log.info("Mismatched device profile and arraylist index. Found device profile: {}", device.getID());
			}
		} else {
			log.info("Could not locate device profile: {}", id);
		}
	}

	/**
	 * Write devices back to devices file. <br>
	 * This will overwrite the devices file. <br>
	 * <b>Currently outputs unformatted.</b>
	 */
	public void writeDevices() {

	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
