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
import java.util.concurrent.atomic.AtomicInteger;

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
	public ArrayList<Device> DEVICES = new ArrayList<Device>();
	public ArrayList<Setting> SETTINGS = new ArrayList<Setting>();
	public Requester REQUESTER;
	public Options COMMAND_OPTIONS;
	public Options SETTING_OPTIONS;
	public Options DEVICE_OPTIONS;
	public AtomicInteger SETTING_ID_TO_USE;
	public AtomicInteger DEVICE_ID_TO_USE;
	public String SETTING_FILE;
	public String DEVICE_FILE;

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

	public final static String SETTINGMODE = "settingmode";
	public final static String CREATE = "create";
	public final static String CREATE_OPT = "c";
	public final static String DELETE = "delete";
	public final static String DELETE_OPT = "d";
	public final static String UPDATE = "update";
	public final static String UPDATE_OPT = "u";
	public final static String ID = "id";
	public final static String DEVICEID = "deviceid";
	public final static String NAME = "name";
	public final static String NAME_OPT = "n";
	public final static String BUFFERSIZE = "buffer";
	public final static String BUFFERSIZE_OPT = "b";
	public final static String SERVERPORT = "port";
	public final static String SERVERPORT_OPT = "p";
	public final static String STORAGEPATH = "storagepath";
	public final static String FILENAMEPATH = "filenamepath";
	public final static String SDCARD = "sdcard";
	public final static String WRITE = "write";
	public final static String WRITE_OPT = "w";

	public final static String DEVICEMODE = "devicemode";
	public final static String IP = "ip";
	public final static String FILENAME = "filename";

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");

		Option app_version = new Option(VERSION_OPT, VERSION, false, "Get build version, which is a date and time");
		Option devicelist = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withLongOpt(DEVICES_LIST).withDescription("XML file with devices").create(DEVICES_LIST_OPT);
		Option settings = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withLongOpt(SETTINGS_INPUT).withDescription("XML file with settings").create(SETTINGS_INPUT_OPT);
		options.addOption(devicelist);
		options.addOption(settings);
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
				.withLongOpt(SHUTDOWN_SINGLE).withDescription("Shutdown collection services on a device")
				.create(SHUTDOWN_SINGLE_OPT);
		Option createSetting = OptionBuilder.isRequired(false).withDescription("Enter setting mode")
				.create(SETTINGMODE);
		Option deviceMode = OptionBuilder.isRequired(false).withDescription("Enter device mode").create(DEVICEMODE);
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
		options.addOption(createSetting);
		options.addOption(deviceMode);
		options.addOption(quit);

		return options;
	}

	@SuppressWarnings("static-access")
	private static Options createSettingOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");
		Option list = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(LIST)
				.withDescription("List all devices").create(LIST_OPT);
		Option create = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(CREATE)
				.withDescription("Create a new setting profile").create(CREATE_OPT);
		Option delete = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(DELETE)
				.withDescription("Delete a setting profile").create(DELETE_OPT);
		Option update = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(UPDATE)
				.withDescription("Update a setting profile").create(UPDATE_OPT);
		Option id = OptionBuilder.hasArg(true).withArgName("setting id(int)").isRequired(false)
				.withDescription("Setting profile ID").create(ID);
		Option deviceid = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withDescription("device ID").create(DEVICEID);
		Option name = OptionBuilder.hasArg(true).withArgName("String").isRequired(false).withLongOpt(NAME)
				.withDescription("Set profile name").create(NAME_OPT);
		Option buffer = OptionBuilder.hasArg(true).withArgName("int").isRequired(false).withLongOpt(BUFFERSIZE)
				.withDescription("Buffer size for selectors?").create(BUFFERSIZE_OPT);
		Option port = OptionBuilder.hasArg(true).withArgName("int").isRequired(false).withLongOpt(SERVERPORT)
				.withDescription("The default port to connect to the devices").create(SERVERPORT_OPT);
		Option storage = OptionBuilder.hasArg(true).withArgName("string").isRequired(false)
				.withDescription("Where to store the logs collected").create(STORAGEPATH);
		Option filenamepath = OptionBuilder.hasArg(true).withArgName("string").isRequired(false)
				.withDescription("Name and path of log on devices").create(FILENAMEPATH);
		Option sdcard = OptionBuilder.hasArg(true).withArgName("boolean").isRequired(false)
				.withDescription("Is the log on external storage(Sdcard)").create(SDCARD);
		Option quit = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(QUIT).withDescription("Quit")
				.create(QUIT_OPT);
		Option write = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(WRITE)
				.withDescription("Write settings to file?").create(WRITE_OPT);
		options.addOption(list);
		options.addOption(create);
		options.addOption(delete);
		options.addOption(update);
		options.addOption(id);
		options.addOption(deviceid);
		options.addOption(name);
		options.addOption(buffer);
		options.addOption(port);
		options.addOption(storage);
		options.addOption(filenamepath);
		options.addOption(sdcard);
		options.addOption(write);
		options.addOption(quit);
		options.addOption(help);
		return options;
	}

	@SuppressWarnings("static-access")
	private static Options createDeviceOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");
		Option list = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(LIST)
				.withDescription("List all devices").create(LIST_OPT);
		Option create = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(CREATE)
				.withDescription("Create a new setting profile").create(CREATE_OPT);
		Option delete = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(DELETE)
				.withDescription("Delete a setting profile").create(DELETE_OPT);
		Option update = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(UPDATE)
				.withDescription("Update a setting profile").create(UPDATE_OPT);
		Option deviceid = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withDescription("device ID").create(DEVICEID);
		Option name = OptionBuilder.hasArg(true).withArgName("String").isRequired(false).withLongOpt(NAME)
				.withDescription("Set profile name").create(NAME_OPT);
		Option ip = OptionBuilder.hasArg(true).withArgName("ip<String>").isRequired(false).withDescription("device IP")
				.create(IP);
		Option port = OptionBuilder.hasArg(true).withArgName("int").isRequired(false).withLongOpt(SERVERPORT)
				.withDescription("The port of the device service").create(SERVERPORT_OPT);
		Option filename = OptionBuilder.hasArg(true).withArgName("string").isRequired(false)
				.withDescription("Name of log for devices").create(FILENAME);
		Option quit = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(QUIT).withDescription("Quit")
				.create(QUIT_OPT);
		Option write = OptionBuilder.hasArg(false).isRequired(false).withLongOpt(WRITE)
				.withDescription("Write settings to file?").create(WRITE_OPT);
		options.addOption(list);
		options.addOption(create);
		options.addOption(delete);
		options.addOption(update);
		options.addOption(deviceid);
		options.addOption(name);
		options.addOption(ip);
		options.addOption(port);
		options.addOption(filename);
		options.addOption(write);
		options.addOption(quit);
		options.addOption(help);
		return options;
	}

	private static void showHelp(Options options) {
		HelpFormatter h = new HelpFormatter();
		h.printHelp(HELP, options);
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
			} else if (cmd.hasOption(VERSION_OPT)) {
				log.info("Build: {}", Client.class.getPackage().getImplementationVersion());
			} else {
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
		this.SETTING_FILE = pSettingFile;
		this.DEVICE_FILE = pDeviceFile;
		this.COMMAND_OPTIONS = createCommandOptions();
		this.SETTING_OPTIONS = createSettingOptions();
		this.DEVICE_OPTIONS = createDeviceOptions();
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
		this.REQUESTER = new Requester(CLIENT_SELECTOR, DEVICES, SETTINGS.get(0), VERSIONCODE);
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
			this.DEVICE_ID_TO_USE = new AtomicInteger(parser.getHighestID());
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
			this.SETTING_ID_TO_USE = new AtomicInteger(parser.getHighestID());
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
		for (Device device : this.DEVICES) {
			Object[] array = { device.getID(), device.getName(), device.getAddress(), device.getPort(),
					device.getFileName() };
			log.debug("Device ID: {} Name: {} Address: {}  Port: {} Filename: {}", array);
		}
	}

	protected void loopSettings() {
		log.debug("Looping Settings: {}", this.SETTINGS.size());
		for (Setting setting : this.SETTINGS) {
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
			} else if (cmd.hasOption(COLLECT_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(COLLECT_SINGLE_OPT);
				if (pDeviceString != null) {
					this.collectSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", COLLECT_SINGLE);
				}
			} else if (cmd.hasOption(COLLECT_ALL_OPT)) {
				this.REQUESTER.getAll();
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.deleteSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
			} else if (cmd.hasOption(DEL_ALL_OPT)) {
				this.REQUESTER.deleteAll();
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.deleteSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
			} else if (cmd.hasOption(DEL_ALL_OPT)) {
				this.REQUESTER.deleteAll();
			} else if (cmd.hasOption(SETTINGMODE)) {
				this.settingMode();
			} else if (cmd.hasOption(DEVICEMODE)) {
				this.deviceMode();
			} else if (cmd.hasOption(LIST_OPT)) {
				this.listDevices();
			} else if (cmd.hasOption(VERSION_OPT)) {
				log.info("Build: {}", Client.class.getPackage().getImplementationVersion());
			} else if (cmd.hasOption(QUIT)) {
				log.info("QUIT");
				System.exit(0);
			} else {
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.COMMAND_OPTIONS);
		}
	}

	protected void settingMode() {
		log.info("Entered setting mode");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			while (true) {
				System.out.print("SettingMode: ");
				String[] pInput = in.readLine().split(" ");
				boolean continute = this.processSettingInput(pInput);
				if (!continute)
					break;
			}
		} catch (IOException e) {
			log.error("Error reading in setting input");
		}
	}

	protected boolean processSettingInput(final String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.SETTING_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.SETTING_OPTIONS);
			} else if (cmd.hasOption(LIST_OPT)) {
				this.listSettings();
			} else if (cmd.hasOption(QUIT)) {
				log.info("Quiting setting mode");
				return false;
			} else if (cmd.hasOption(CREATE_OPT)) {
				String pName = cmd.getOptionValue(NAME_OPT).trim();
				String pBuffer = cmd.getOptionValue(BUFFERSIZE_OPT).trim();
				String pPort = cmd.getOptionValue(SERVERPORT_OPT).trim();
				String pStoragePath = cmd.getOptionValue(STORAGEPATH).trim();
				String pFileNamePath = cmd.getOptionValue(FILENAMEPATH).trim();
				String pSDCard = cmd.getOptionValue(SDCARD).trim();
				this.createSetting(pName, pBuffer, pPort, pStoragePath, pFileNamePath, pSDCard);
			} else if (cmd.hasOption(DELETE_OPT)) {
				String pID = cmd.getOptionValue(ID).trim();
				this.deleteSetting(pID);
			} else if (cmd.hasOption(UPDATE_OPT)) {
				String pID = cmd.getOptionValue(ID).trim();
				String pName = cmd.getOptionValue(NAME_OPT).trim();
				String pBuffer = cmd.getOptionValue(BUFFERSIZE_OPT).trim();
				String pPort = cmd.getOptionValue(SERVERPORT_OPT).trim();
				String pStoragePath = cmd.getOptionValue(STORAGEPATH).trim();
				String pFileNamePath = cmd.getOptionValue(FILENAMEPATH).trim();
				String pSDCard = cmd.getOptionValue(SDCARD).trim();
				this.updateSetting(pID, pName, pBuffer, pPort, pStoragePath, pFileNamePath, pSDCard);
			} else if (cmd.hasOption(WRITE_OPT)) {
				this.writeSettings();
			} else {
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.SETTING_OPTIONS);
		}
		return true;
	}

	protected void deviceMode() {
		log.info("Entered device mode");
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			while (true) {
				System.out.print("DeviceMode: ");
				String[] pInput = in.readLine().split(" ");
				boolean continute = this.processDeviceInput(pInput);
				if (!continute)
					break;
			}
		} catch (IOException e) {
			log.error("Error reading in device input");
		}
	}

	protected boolean processDeviceInput(final String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.DEVICE_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.DEVICE_OPTIONS);
			} else if (cmd.hasOption(LIST_OPT)) {
				this.listDevices();
			} else if (cmd.hasOption(QUIT)) {
				log.info("Quiting device mode");
				return false;
			} else if (cmd.hasOption(CREATE_OPT)) {
				String pName = cmd.getOptionValue(NAME_OPT).trim();
				String pIP = cmd.getOptionValue(IP).trim();
				String pPort = cmd.getOptionValue(SERVERPORT_OPT).trim();
				String pFileName = cmd.getOptionValue(FILENAME).trim();
				this.createDevice(pName, pIP, pPort, pFileName);
			} else if (cmd.hasOption(DELETE_OPT)) {
				String pID = cmd.getOptionValue(DEVICEID).trim();
				this.deleteDevice(pID);
			} else if (cmd.hasOption(UPDATE_OPT)) {
				String pID = cmd.getOptionValue(DEVICEID).trim();
				String pName = cmd.getOptionValue(NAME_OPT).trim();
				String pIP = cmd.getOptionValue(IP).trim();
				String pPort = cmd.getOptionValue(SERVERPORT_OPT).trim();
				String pFileName = cmd.getOptionValue(FILENAME).trim();
				this.updateDevice(pID, pName, pIP, pPort, pFileName);
			} else if (cmd.hasOption(WRITE_OPT)) {
				this.writeDevices();
			} else {
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.DEVICE_OPTIONS);
		}
		return true;
	}

	protected Device getDevice(final int pDeviceNumber) {
		for (Device device : this.DEVICES) {
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

	protected void listDevices() {
		StringBuilder builder = new StringBuilder();
		for (Device device : this.DEVICES) {
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

	protected void listSettings() {
		StringBuilder builder = new StringBuilder();
		for (Setting setting : this.SETTINGS) {
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

	protected void createSetting(final String pName, final String pBuffer, final String pPort,
			final String pStoragePath, final String pFileNamePath, final String pSDCard) {
		final int buffer = (pBuffer == null) ? this.BUFFER : Integer.parseInt(pBuffer);
		final int port = (pPort == null) ? this.SERVER_PORT : Integer.parseInt(pPort);
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
			setting.setID(this.SETTING_ID_TO_USE.incrementAndGet());
			setting.setName(pName);
			setting.setBuffer(buffer);
			setting.setServerPort(port);
			setting.setFileNamePath(pFileNamePath);
			setting.setStoragePath(pStoragePath);
			setting.setSDCard(sdcard);
			this.SETTINGS.add(setting.getID(), setting);
		}
	}

	protected void deleteSetting(final String pID) {
		if (pID == null) {
			log.info("Cannot delete setting if setting profile ID is not passed");
		} else {
			int id = Integer.parseInt(pID);
			Setting setting = this.SETTINGS.get(id);
			if (setting != null) {
				if (setting.getID() == id) {
					this.SETTINGS.remove(id);
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

	protected void updateSetting(final String pID, final String pName, final String pBuffer, final String pPort,
			final String pStoragePath, final String pFileNamePath, final String pSDCard) {
		if (pID == null) {
			log.info("Require setting profile ID!");
			return;
		}
		final int id = Integer.parseInt(pID);
		Setting setting = this.SETTINGS.get(id);
		final int buffer = (pBuffer == null) ? setting.getBuffer() : Integer.parseInt(pBuffer);
		final int port = (pPort == null) ? setting.getServerPort() : Integer.parseInt(pPort);
		final boolean sdcard = (pSDCard == null) ? setting.getSDCard() : Boolean.parseBoolean(pSDCard);

		if (setting != null) {
			if (setting.getID() == id) {
				if (setting.getName().compareToIgnoreCase(pName) != 0)
					setting.setName(pName);
				if (setting.getStoragePath().compareToIgnoreCase(pStoragePath) != 0)
					setting.setStoragePath(pStoragePath);
				if (setting.getFileNamePath().compareToIgnoreCase(pFileNamePath) != 0)
					setting.setFileNamePath(pFileNamePath);
				if (setting.getBuffer() != buffer)
					setting.setBuffer(buffer);
				if (setting.getServerPort() != port)
					setting.setServerPort(port);
				if (setting.getSDCard() != sdcard)
					setting.setSDCard(sdcard);
				log.info("Updated setting profile: {}. REMEMBER TO CALL -w IN SETTTING MODE", id);
			} else {
				log.info("Mismatched setting profile and arraylist index. Found setting profile: {}", setting.getID());
			}
		} else {
			log.info("Could not locate setting profile: {}", id);
		}
	}

	protected void createDevice(final String pName, final String pIP, final String pPort, final String pFileName) {
		final int port = (pPort == null) ? this.PORT : Integer.parseInt(pPort);
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
			device.setID(this.DEVICE_ID_TO_USE.incrementAndGet());
			device.setName(pName);
			device.setAddress(pIP);
			device.setFileName(pFileName);
			device.setPort(port);
			this.DEVICES.add(device.getID(), device);
		}
	}

	protected void deleteDevice(final String pID) {
		if (pID == null) {
			log.info("Cannot delete device if device ID is not passed");
		} else {
			/*
			 * TODO locate device and delete, also search settings for device to delete as well.
			 */
		}
	}

	protected void updateDevice(final String pID, final String pName, final String pIP, final String pPort,
			final String pFileName) {
		/*
		 * TODO locate device then determine new values then update setting
		 */
	}

	protected void writeSettings() {
		log.info("Writing settings to file");
		SettingsWriter writer = new SettingsWriter(this.SETTING_FILE);
		writer.write(this.SETTINGS);
	}

	protected void writeDevices() {

	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
