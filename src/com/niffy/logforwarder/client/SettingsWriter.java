package com.niffy.logforwarder.client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.logforwarder.client.tags.SettingsTags;

public class SettingsWriter {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(SettingsWriter.class);

	// ===========================================================
	// Fields
	// ===========================================================
	private final String FILEPATH;
	

	// ===========================================================
	// Constructors
	// ===========================================================

	public SettingsWriter(final String pFilePath) {
		this.FILEPATH = pFilePath;
	}

	public void write(final ArrayList<Setting> pSettings) {
		XMLOutputFactory factory;
		XMLStreamWriter writer;
		try {
			factory = XMLOutputFactory.newInstance();
			writer = factory.createXMLStreamWriter(new FileWriter(this.FILEPATH));
			try {
				writer.writeStartDocument();
				writer.writeStartElement(SettingsTags.SETTINGS_SETTINGS);
				for (Setting setting : pSettings) {
					this.writeSetting(writer,setting);
				}
				writer.writeEndElement();
				writer.writeEndDocument();
			} catch (XMLStreamException e) {
				log.error("Error writing to XML file.", e);
			}
		} catch (XMLStreamException e1) {
			log.info("Error creating settings XML, please check log");
			log.error("XMLStreamException", e1);
		} catch (IOException e1) {
			log.info("Error creating settings XML, please check log");
			log.error("IOException", e1);
		}
	}

	protected void writeSetting(XMLStreamWriter writer, final Setting pSetting) throws XMLStreamException {
		writer.writeStartElement(SettingsTags.SETTINGS_SETTING);
		writer.writeStartElement(SettingsTags.SETTINGS_ID);
		writer.writeCharacters(String.valueOf(pSetting.getID()));
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_NAME);
		writer.writeCharacters(pSetting.getName());
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_BUFFER);
		writer.writeCharacters(String.valueOf(pSetting.getBuffer()));
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_SERVER_PORT);
		writer.writeCharacters(String.valueOf(pSetting.getServerPort()));
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_STORAGE_PATH);
		writer.writeCharacters(pSetting.getStoragePath());
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_FILENAME_PATH);
		writer.writeCharacters(pSetting.getFileNamePath());
		writer.writeEndElement();
		writer.writeStartElement(SettingsTags.SETTINGS_SDCARD);
		writer.writeCharacters(String.valueOf(pSetting.getSDCard()));
		writer.writeEndElement();
		//TODO devices
		writer.writeEndElement();
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
