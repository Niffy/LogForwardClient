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

import com.niffy.logforwarder.client.parser.SettingsParser;

public class SettingManager {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(SettingManager.class);

	// ===========================================================
	// Fields
	// ===========================================================
	private ArrayList<Setting> mSettings = new ArrayList<Setting>();
	private String mSettingFile;
	private AtomicInteger mSettingIDToUse;
	private DeviceManager mDeviceManager;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SettingManager() {

	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public void setDeviceManager(final DeviceManager pDeviceManager) {
		this.mDeviceManager = pDeviceManager;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public void readInSettingFile(final String pSettingsFilePath) {
		log.debug("Reading in settings file: {}", pSettingsFilePath);
		this.mSettingFile = pSettingsFilePath;
		InputSource is = null;
		try {
			InputStream inputStream = new FileInputStream(pSettingsFilePath);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
		} catch (FileNotFoundException e) {
			log.error("File not found: {}", pSettingsFilePath, e);
			return;
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException: {}", pSettingsFilePath, e);
			return;
		}
		try {
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();
			final XMLReader xr = sp.getXMLReader();
			final SettingsParser parser = new SettingsParser();
			xr.setContentHandler(parser);
			xr.parse(is);
			this.mSettings = parser.getSettings();
			this.mSettingIDToUse = new AtomicInteger(parser.getHighestID());
		} catch (final SAXException e) {
			log.error("SAXException loading devices", e);
		} catch (final ParserConfigurationException e) {
			log.error("ParserConfigurationException loading devices", e);
		} catch (final IOException e) {
			log.error("IOException loading devices", e);
		}
	}

	/**
	 * Loop settings for debug mode
	 */
	public void loopSettings() {
		log.debug("Looping Settings: {}", this.mSettings.size());
		for (Setting setting : this.mSettings) {
			Object[] array = { setting.getID(), setting.getName(), setting.getBuffer(), setting.getServerPort(),
					setting.getStoragePath(), setting.getFileNamePath(), setting.getSDCard() };
			log.debug("Setting. ID: {} Name: {} Buffer: {} Port: {} storagePath: {} FileNamePath: {} SDCard: {} ",
					array);
			ArrayList<Integer> Devices = setting.getDevices();
			for (Integer id : Devices) {
				log.debug("Device: {}", id);
			}
		}
	}

	public void listSettings() {
		StringBuilder builder = new StringBuilder();
		for (Setting setting : this.mSettings) {
			builder.append(" ID: ");
			builder.append(setting.getID());
			builder.append(" Name: ");
			builder.append(setting.getName());
			builder.append(" Buffer: ");
			builder.append(setting.getBuffer());
			builder.append(" Port: ");
			builder.append(setting.getServerPort());
			builder.append(" SDCARD: ");
			builder.append(setting.getSDCard());
			builder.append(" Storage path: ");
			builder.append(setting.getStoragePath());
			builder.append(" Filename path: ");
			builder.append(setting.getFileNamePath());
			builder.append(" Devices count: ");
			builder.append(setting.getDevices().size());
			log.info(builder.toString());
			builder.setLength(0);
		}
	}

	public void createSetting(final String pName, final String pBuffer, final String pPort, final String pStoragePath,
			final String pFileNamePath, final String pSDCard) {
		final int buffer = (pBuffer == null) ? Defaults.BUFFER : Integer.parseInt(pBuffer);
		final int port = (pPort == null) ? Defaults.SERVER_PORT : Integer.parseInt(pPort);
		final boolean sdcard = (pSDCard == null) ? true : Boolean.parseBoolean(pSDCard);
		if (pName == null || pStoragePath == null || pFileNamePath == null || pSDCard == null) {
			log.info("Required attributes not found");
			if (pName == null)
				log.info("Name required");
			if (pStoragePath == null)
				log.info("Storage path required");
			if (pFileNamePath == null)
				log.info("File name of log required");
			if (pSDCard == null)
				log.info("SDcard required");
		} else {
			Setting setting = new Setting();
			setting.setID(this.mSettingIDToUse.incrementAndGet());
			setting.setName(pName);
			setting.setBuffer(buffer);
			setting.setServerPort(port);
			setting.setFileNamePath(pFileNamePath);
			setting.setStoragePath(pStoragePath);
			setting.setSDCard(sdcard);
			this.mSettings.add(setting.getID(), setting);
		}
	}

	public void deleteSetting(final String pID) {
		if (pID == null) {
			log.info("Cannot delete setting if setting profile ID is not passed");
		} else {
			int id = Integer.parseInt(pID);
			Setting setting = this.mSettings.get(id);
			if (setting != null) {
				if (setting.getID() == id) {
					this.mSettings.remove(id);
					log.info("Removed setting profile: {}. REMEMBER TO CALl -w IN SETTTING MODE", id);
				} else {
					log.info("Mismatched setting profile and arraylist index. Found setting profile: {}",
							setting.getID());
				}
			} else {
				log.info("Could not locate setting profile: {}", id);
			}
		}
	}

	public void updateSetting(final String pID, final String pName, final String pBuffer, final String pPort,
			final String pStoragePath, final String pFileNamePath, final String pSDCard) {
		if (pID == null) {
			log.info("Require setting profile ID!");
			return;
		}
		final int id = Integer.parseInt(pID);
		Setting setting = this.mSettings.get(id);
		final int buffer = (pBuffer == null) ? setting.getBuffer() : Integer.parseInt(pBuffer);
		final int port = (pPort == null) ? setting.getServerPort() : Integer.parseInt(pPort);
		final boolean sdcard = (pSDCard == null) ? setting.getSDCard() : Boolean.parseBoolean(pSDCard);
		final String name = (pName == null) ? setting.getName() : pName;
		final String storagepath = (pStoragePath == null) ? setting.getStoragePath() : pStoragePath;
		final String filename = (pFileNamePath == null) ? setting.getFileNamePath() : pFileNamePath;
		if (setting != null) {
			if (setting.getID() == id) {
				if (setting.getName().compareToIgnoreCase(name) != 0)
					setting.setName(name);
				if (setting.getStoragePath().compareToIgnoreCase(storagepath) != 0)
					setting.setStoragePath(storagepath);
				if (setting.getFileNamePath().compareToIgnoreCase(filename) != 0)
					setting.setFileNamePath(filename);
				if (setting.getBuffer() != buffer)
					setting.setBuffer(buffer);
				if (setting.getServerPort() != port)
					setting.setServerPort(port);
				if (setting.getSDCard() != sdcard)
					setting.setSDCard(sdcard);
				log.info("Updated setting profile: {} . REMEMBER TO CALL -w IN SETTTING MODE", id);
			} else {
				log.info("Mismatched setting profile and arraylist index. Found setting profile: {}", setting.getID());
			}
		} else {
			log.info("Could not locate setting profile: {}", id);
		}
	}

	public void addDevicesToSetting(final String pID, final String[] pDevices) {
		if (pID == null) {
			log.info("Require Setting ID!");
			return;
		}
		if (pDevices == null) {
			log.info("Require device IDs!");
			return;
		}
		final int id = Integer.parseInt(pID);
		Setting setting = this.mSettings.get(id);
		if (setting != null) {
			if (setting.getID() == id) {
				for (String did : pDevices) {
					int pDeviceID = Integer.parseInt(did);
					Device device = this.mDeviceManager.getDevice(pDeviceID);
					if (device != null) {
						setting.addDevices(pDeviceID);
						log.info("Added device profile {} to setting profile", pDeviceID);
					} else {
						log.info("Could not locate device profile: {}", pDeviceID);
					}
				}
			} else {
				log.info("Mismatched setting profile and arraylist index. Found setting profile: {}", setting.getID());
			}
			log.info("Finished! Remember to call -w in setting mode to write changes!");
		} else {
			log.info("Could not locate setting profile: {}", id);
		}
	}
	
	/**
	 * Write settings back to settings file. <br>
	 * This will overwrite the settings file. <br>
	 * <b>Currently outputs unformatted.</b>
	 */
	public void writeSettingsToFile() {
		log.info("Writing settings to file");
		SettingsWriter writer = new SettingsWriter(this.mSettingFile);
		writer.write(this.mSettings);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
