package com.niffy.logforwarder.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.niffy.logforwarder.client.parser.DeviceParser;
import com.niffy.logforwarder.client.parser.SettingsParser;
import com.niffy.logforwarder.lib.logmanagement.ILogManager;
import com.niffy.logforwarder.lib.logmanagement.LogManagerClient;

public class start {
	private final static Logger log = LoggerFactory.getLogger(start.class);
	public CustomClientSelector CLIENT_SELECTOR;
	public InetSocketAddress ADDRESS;
	public int PORT = 1006;
	public int BUFFER = 20971520;
	public int SERVER_PORT = 1088;
	public ILogManager LOG_MANAGER;
	public int VERSION = 0;
	public HashMap<String, Device> DEVICES = new HashMap<String, Device>();
	public HashMap<String, Setting> SETTINGS = new HashMap<String, Setting>();
	public Requester REQUESTER;

	public static void main(String[] args) {
		log.info("Started");
		String pDeviceFile = args[0];
		String pSettingFile = args[1];
		log.info("Device file: {}", pDeviceFile);
		log.info("Setting file: {}", pSettingFile);
		new start(pDeviceFile, pSettingFile);
	}

	public start(final String pDeviceFile, final String pSettingFile) {
		this.readInSettings(pDeviceFile, pSettingFile);
		this.LOG_MANAGER = new LogManagerClient(this.VERSION);
		this.ADDRESS = new InetSocketAddress(this.PORT);
		try {
			this.CLIENT_SELECTOR = new CustomClientSelector("Client Selector", this.ADDRESS, this.BUFFER, this.SERVER_PORT,
					this.LOG_MANAGER);
			new Thread(this.CLIENT_SELECTOR).start();
		} catch (IOException e) {
			log.error("Error creating selector", e);
		}
		this.REQUESTER = new Requester(CLIENT_SELECTOR, DEVICES, SETTINGS.get("Isometric world"), VERSION);
		this.LOG_MANAGER.setSelector(CLIENT_SELECTOR);
		this.process();
	}

	protected void readInSettings(final String pDeviceFile, final String pSettingFile) {
		this.readDevices(pDeviceFile);
		this.readSettings(pSettingFile);
		this.loopDevices();
		this.loopSettings();
	}

	protected void readDevices(final String pPath) {
		InputSource is = null;
		try {
			InputStream inputStream = new FileInputStream(pPath);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
		} catch (FileNotFoundException e) {
			log.error("File not found: {}", pPath, e);
			return;
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException: {}", pPath, e);
			return;
		}

		try {
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();
			final XMLReader xr = sp.getXMLReader();
			final DeviceParser parser = new DeviceParser();
			xr.setContentHandler(parser);
			xr.parse(is);
			this.DEVICES = parser.getDevices();
		} catch (final SAXException e) {
			log.error("SAXException loading devices", e);
		} catch (final ParserConfigurationException e) {
			log.error("ParserConfigurationException loading devices", e);
		} catch (final IOException e) {
			log.error("IOException loading devices", e);
		}
	}

	protected void readSettings(final String pPath) {
		InputSource is = null;
		try {
			InputStream inputStream = new FileInputStream(pPath);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
		} catch (FileNotFoundException e) {
			log.error("File not found: {}", pPath, e);
			return;
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException: {}", pPath, e);
			return;
		}
		try {
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();
			final XMLReader xr = sp.getXMLReader();
			final SettingsParser parser = new SettingsParser();
			xr.setContentHandler(parser);
			xr.parse(is);
			this.SETTINGS = parser.getSettings();
		} catch (final SAXException e) {
			log.error("SAXException loading devices", e);
		} catch (final ParserConfigurationException e) {
			log.error("ParserConfigurationException loading devices", e);
		} catch (final IOException e) {
			log.error("IOException loading devices", e);
		}
	}

	protected void loopDevices() {
		log.debug("Looping Devices: {}", this.DEVICES.size());
		Iterator<Map.Entry<String, Device>> entries = this.DEVICES.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Device> entry = entries.next();
			Device device = entry.getValue();
			Object[] array = { device.getID(), device.getName(), device.getAddress(), device.getPort(),
					device.getFileName() };
			log.debug("Device ID: {} Name: {} Address: {}  Port: {} Filename: {}", array);
		}
	}

	protected void loopSettings() {
		log.debug("Looping Settings: {}", this.SETTINGS.size());
		Iterator<Map.Entry<String, Setting>> entries = this.SETTINGS.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Setting> entry = entries.next();
			Setting setting = entry.getValue();
			Object[] array = { setting.getName(), setting.getBuffer(), setting.getServerPort(),
					setting.getStoragePath(), setting.getFileNamePath(), setting.getSDCard() };
			log.debug("Setting. Name: {} Buffer: {} Port: {} storagePath: {} FileNamePath: {} SDCard: {} ", array);
			ArrayList<String> Devices = setting.getDevices();
			for (String string : Devices) {
				log.debug("Device: {}", string);
			}
		}
	}

	public void process() {
		String CurLine = ""; // Line read from standard in
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			while (true) {
				System.out.print("main Input: ");
				CurLine = in.readLine();

				if (CurLine.equals("quit")) {
					System.exit(0);
				} else if (CurLine.equals("collect-all")) {
					this.REQUESTER.getAll();
				} else if (CurLine.equals("collect-single")) {
					this.collectSingle(in);
				} else if (CurLine.equals("del-all")) {
					this.REQUESTER.deleteAll();
				} else if (CurLine.equals("del-single")) {
					this.deleteSingle(in);
				}
			}
		} catch (IOException e) {
			log.error("Error somewhere: ", e);
		}
	}

	public void collectSingle(BufferedReader in) throws IOException {
		String CurLine = "";
		try {
			System.out.println("Collect from Whom? : ");
			Iterator<Map.Entry<String, Device>> entries = this.DEVICES.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, Device> entry = entries.next();
				Device device = entry.getValue();
				log.info("ID: {} Name: {}", device.getID(), device.getName());
			}
			CurLine = in.readLine();
			if (CurLine.equals("quit")) {
				System.exit(0);
			} else if (CurLine.equals("up")) {
				return;
			} else {
				try {
					int index = Integer.valueOf(CurLine);
					boolean found = false;
					entries = this.DEVICES.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, Device> entry = entries.next();
						Device device = entry.getValue();
						if (device.getID() == index) {
							found = true;
							this.REQUESTER.getSingle(device);
						}
					}
					if (!found) {
						log.warn("Could not find device to request from");
					}
				} catch (NumberFormatException e) {
					log.error("Could not get a number, going up..^");
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteSingle(BufferedReader in) throws IOException {
		String CurLine = "";
		try {
			System.out.println("Delete from Whom? : ");
			Iterator<Map.Entry<String, Device>> entries = this.DEVICES.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String, Device> entry = entries.next();
				Device device = entry.getValue();
				log.info("ID: {} Name: {}", device.getID(), device.getName());
			}
			CurLine = in.readLine();
			if (CurLine.equals("quit")) {
				System.exit(0);
			} else if (CurLine.equals("up")) {
				return;
			} else {
				try {
					int index = Integer.valueOf(CurLine);
					boolean found = false;
					entries = this.DEVICES.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String, Device> entry = entries.next();
						Device device = entry.getValue();
						if (device.getID() == index) {
							found = true;
							this.REQUESTER.deleteSingle(device);
						}
					}
					if (!found) {
						log.warn("Could not find device to request delete from");
					}
				} catch (NumberFormatException e) {
					log.error("Could not get a number, going up..^");
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
			public static void tcpCommand(BufferedReader in) {
				String CurLine = "";
				try {
					System.out.print("TCP : ");
					CurLine = in.readLine();
					if (CurLine.equals("quit")) {
						System.exit(0);
					} else if (CurLine.equals("connect")) {
						connectProcess(in);
					} else if (CurLine.equals("send")) {
						System.out.print("Send : ");
						CurLine = in.readLine();
						if (CurLine.equals("up")) {
							return;
						} else {
							byte[] pBytes = CurLine.getBytes();
							send(in, pBytes);
							return;
						}
					}
				} catch (IOException e) {
					System.out.println("error");
					e.printStackTrace();
				}
			}

			public static void connectProcess(BufferedReader in) {
				String CurLine = "";
				try {
					System.out.print("Connect : ");
					CurLine = in.readLine();
					if (CurLine.equals("quit")) {
						System.exit(0);
					} else {
						int port = getPort(CurLine);
						String ip = getIP(CurLine);
						if (port != -1) {
							System.out.println("Found: " + ip + " " + port);
							host host = new host(ip, port);
							mHosts.add(host);
							connect(ip, port);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public static String getIP(String pLine) {
				int firstColon = pLine.indexOf(":");
				firstColon++;
				int portColon = pLine.lastIndexOf(":");
				portColon--;
				String found = pLine.substring(firstColon, portColon);
				found.trim();
				return found;
			}

			public static int getPort(String pLine) {
				int portColon = pLine.lastIndexOf(":");
				portColon++;
				String found = pLine.substring(portColon);
				found.trim();
				try {
					return Integer.valueOf(found);
				} catch (NumberFormatException er) {
					return -1;
				}
			}

			public static void connect(String pIP, int pPort) throws IOException {
			
			}
		*/
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
