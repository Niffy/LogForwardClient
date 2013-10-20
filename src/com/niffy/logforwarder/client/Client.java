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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.niffy.logforwarder.client.parser.DeviceParser;
import com.niffy.logforwarder.client.parser.SettingsParser;
import com.niffy.logforwarder.lib.logmanagement.ILogManager;
import com.niffy.logforwarder.lib.logmanagement.LogManagerClient;

public class Client {
	private final static Logger log = LoggerFactory.getLogger(Client.class);

	public CustomClientSelector CLIENT_SELECTOR;
	public InetSocketAddress ADDRESS;
	public int PORT = 1006;
	public int BUFFER = 20971520;
	public int SERVER_PORT = 1088;
	public ILogManager LOG_MANAGER;
	public int VERSIONCODE = 0;
	public HashMap<String, Device> DEVICES = new HashMap<String, Device>();
	public HashMap<String, Setting> SETTINGS = new HashMap<String, Setting>();
	public Requester REQUESTER;
	public Options COMMAND_OPTIONS;

	public final static String DEVICES_LIST = "devicelist";
	public final static String DEVICES_LIST_OPT = "dl";
	public final static String SETTINGS_INPUT = "settings";
	public final static String SETTINGS_INPUT_OPT = "s";
	public final static String LIST = "list";
	public final static String LIST_OPT = "ls";
	public final static String COLLECT_ALL = "collectall";
	public final static String COLLECT_ALL_OPT = "ca";
	public final static String COLLECT_SINGLE = "collectsingle";
	public final static String COLLECT_SINGLE_OPT = "cs";
	public final static String DEL_ALL = "delall";
	public final static String DEL_ALL_OPT = "dl";
	public final static String DEL_SINGLE = "delsingle";
	public final static String DEL_SINGLE_OPT = "ds";
	public final static String SHUTDOWN_ALL = "shutdownall";
	public final static String SHUTDOWN_ALL_OPT = "sda";
	public final static String SHUTDOWN_SINGLE = "shutdownsingle";
	public final static String SHUTDOWN_SINGLE_OPT = "sds";
	public final static String QUIT = "quit";
	public final static String QUIT_OPT = "q";
	public final static String VERSION = "version";
	public final static String VERSION_OPT = "v";
	public final static String HELP = "help";
	public final static String HELP_OPT = "h";

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");

		Option app_version = new Option(VERSION_OPT, VERSION, false, "Get build version, which is a date and time");
		Option devicelist = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withLongOpt(DEVICES_LIST).withDescription("XML file with devices").create(DEVICES_LIST_OPT);
		Option settings = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withLongOpt(SETTINGS_INPUT).withDescription("XML file with settings").create(SETTINGS_INPUT_OPT);
		Option list = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(LIST)
				.withDescription("List all devices").create(LIST_OPT);
		options.addOption(devicelist);
		options.addOption(settings);
		options.addOption(list);
		options.addOption(help);
		options.addOption(app_version);
		return options;
	}

	@SuppressWarnings("static-access")
	private static Options createCommandOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");
		Option list = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(LIST)
				.withDescription("List all devices").create(LIST_OPT);
		Option collectall = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(COLLECT_ALL)
				.withDescription("Collect all logs from connected devices").create(COLLECT_ALL_OPT);
		Option collectsingle = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withLongOpt(COLLECT_SINGLE).withDescription("Collect log from single device")
				.create(COLLECT_SINGLE_OPT);
		Option deleteall = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(DEL_ALL)
				.withDescription("Delete all logs from connected devices").create(DEL_ALL_OPT);
		Option deletesingle = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withLongOpt(DEL_SINGLE).withDescription("Delete log from single device").create(DEL_SINGLE_OPT);
		Option shutdownall = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(SHUTDOWN_ALL)
				.withDescription("Shutdown collection services on all devices").create(SHUTDOWN_ALL_OPT);
		Option shutdownsingle = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withLongOpt(SHUTDOWN_SINGLE).withDescription("Shutdown collection services on a device").create(SHUTDOWN_SINGLE_OPT);
		Option quit = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(QUIT).withDescription("Quit")
				.create(QUIT_OPT);
		options.addOption(help);
		options.addOption(list);
		options.addOption(collectall);
		options.addOption(collectsingle);
		options.addOption(deleteall);
		options.addOption(deletesingle);
		options.addOption(shutdownall);
		options.addOption(shutdownsingle);
		options.addOption(quit);

		return options;
	}

	private static void showHelp(Options options) {
		HelpFormatter h = new HelpFormatter();
		h.printHelp(HELP, options);
		System.exit(-1);
	}

	public static void main(String[] args) {
		Options options = createOptions();
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(options);
			} else if (cmd.hasOption(DEVICES_LIST_OPT)) {
				String pDevices = cmd.getOptionValue(DEVICES_LIST_OPT);
				String pSettings = cmd.getOptionValue(SETTINGS_INPUT_OPT);
				execute(pDevices, pSettings);
			} else if (cmd.hasOption(SETTINGS_INPUT_OPT)) {
				String pDevices = cmd.getOptionValue(DEVICES_LIST_OPT);
				String pSettings = cmd.getOptionValue(SETTINGS_INPUT_OPT);
				execute(pDevices, pSettings);
			} else if (cmd.hasOption(LIST_OPT)) {
				log.info("Not supported yet!");
			} else if (cmd.hasOption(VERSION_OPT)) {
				log.info("Build: {}", Client.class.getPackage().getImplementationVersion());
			}else{
				log.info("No commands selected");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", args, e);
			showHelp(options);
		}
	}

	public static void execute(String pDevices, String pSettings) {
		if (pDevices != null && pSettings != null) {
			pDevices = pDevices.trim();
			pSettings = pSettings.trim();
			log.info("Using device file: {}", pDevices);
			log.info("Using setting file: {}", pSettings);
			new Client(pDevices, pSettings);
		} else {
			log.info("Could not read inputs, check they are all present. Or call help");
		}
	}

	public Client(final String pDeviceFile, final String pSettingFile) {
		String version = this.getClass().getPackage().getImplementationVersion();
		log.info("Started LogForwardClient Version: {}", version);
		this.COMMAND_OPTIONS = createCommandOptions();

		this.readInSettings(pDeviceFile, pSettingFile);
		this.LOG_MANAGER = new LogManagerClient(this.VERSIONCODE);
		this.ADDRESS = new InetSocketAddress(this.PORT);
		try {
			this.CLIENT_SELECTOR = new CustomClientSelector("Client Selector", this.ADDRESS, this.BUFFER,
					this.SERVER_PORT, this.LOG_MANAGER);
			new Thread(this.CLIENT_SELECTOR).start();
		} catch (IOException e) {
			log.error("Error creating selector", e);
		}
		this.REQUESTER = new Requester(CLIENT_SELECTOR, DEVICES, SETTINGS.get("Isometric world"), VERSIONCODE);
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
		log.debug("Reading in devices");
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
		log.debug("Reading in settings");
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
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			while (true) {
				System.out.print("main Input: ");
				String[] pInput = in.readLine().split(" ");
				this.readfromline(pInput);
			}
		} catch (IOException e) {
			log.error("Error reading in input");
		}
	}

	protected void readfromline(String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.COMMAND_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.COMMAND_OPTIONS);
			} else if (cmd.hasOption(COLLECT_ALL_OPT)) {
				this.REQUESTER.getAll();
				return;
			} else if (cmd.hasOption(COLLECT_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(COLLECT_SINGLE_OPT);
				if (pDeviceString != null) {
					this.collectSingle(pDeviceString);
				}else{
					log.info("No device ID supplied with command: {}", COLLECT_SINGLE);
				}
				return;
			} else if (cmd.hasOption(COLLECT_ALL_OPT)) {
				this.REQUESTER.getAll();
				return;
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.deleteSingle(pDeviceString);
				}else{
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
				return;
			} else if (cmd.hasOption(DEL_ALL_OPT)) {
				this.REQUESTER.deleteAll();
				return;
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.deleteSingle(pDeviceString);
				}else{
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
				return;
			} else if (cmd.hasOption(DEL_ALL_OPT)) {
				this.REQUESTER.deleteAll();
				return;
			} else if (cmd.hasOption(LIST_OPT)) {
				log.info("Not supported yet!");
				return;
			} else if (cmd.hasOption(VERSION_OPT)) {
				log.info("Build: {}", Client.class.getPackage().getImplementationVersion());
				return;
			}else if(cmd.hasOption(QUIT)){
				log.info("QUIT");
				System.exit(0);
			}else{
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.COMMAND_OPTIONS);
		}
	}

	protected Device getDevice(final int pDeviceNumber) {
		Iterator<Map.Entry<String, Device>> entries = this.DEVICES.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Device> entry = entries.next();
			Device device = entry.getValue();
			if (device.getID() == pDeviceNumber) {
				return device;
			}
		}
		log.warn("Could not find device: {} in device list", pDeviceNumber);
		return null;
	}

	protected void collectSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.collectSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	protected void collectSingle(final int pDevice) {
		Device pDeviceObj = this.getDevice(pDevice);
		this.collectSingle(pDeviceObj);
	}

	protected void collectSingle(final Device pDevice) {
		if (pDevice != null) {
			this.REQUESTER.getSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	protected void deleteSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.deleteSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	protected void deleteSingle(final int pDevice) {
		Device pDeviceObj = this.getDevice(pDevice);
		this.deleteSingle(pDeviceObj);
	}

	protected void deleteSingle(final Device pDevice) {
		if (pDevice != null) {
			this.REQUESTER.deleteSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	protected void shutdownSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.deleteSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	protected void shutdownSingle(final int pDevice) {
		Device pDeviceObj = this.getDevice(pDevice);
		this.deleteSingle(pDeviceObj);
	}

	protected void shutdownSingle(final Device pDevice) {
		if (pDevice != null) {
			this.REQUESTER.shutdownSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
