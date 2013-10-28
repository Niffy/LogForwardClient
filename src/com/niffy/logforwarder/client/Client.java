package com.niffy.logforwarder.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
	private final static Logger log = LoggerFactory.getLogger(Client.class);

	private Options COMMAND_OPTIONS;
	private Options SETTING_OPTIONS;
	private Options DEVICE_OPTIONS;

	private DeviceManager mDeviceManager;
	private SettingManager mSettingManager;
	private ClientManager mClientManager;

	public final static String DEVICES_LIST = "devicefile";
	public final static String SETTINGS_INPUT = "settingsfile";
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
	public final static String DEVICEID_OPT = "did";
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
	public final static String ADD = "add";
	public final static String ADD_OPT = "a";

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP, false, "Help!!");

		Option app_version = new Option(VERSION_OPT, VERSION, false, "Get build version, which is a date and time");
		Option devicelist = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withDescription("XML file with devices").create(DEVICES_LIST);
		Option settings = OptionBuilder.hasArg(true).withArgName("xml file path").isRequired(false)
				.withDescription("XML file with settings").create(SETTINGS_INPUT);
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
		Option delete = OptionBuilder.hasArg(true).isRequired(false).withLongOpt(DELETE)
				.withDescription("Delete a setting profile").withArgName("setting profile id(int)").create(DELETE_OPT);
		Option update = OptionBuilder.hasArg(true).isRequired(false).withLongOpt(UPDATE)
				.withDescription("Update a setting profile").withArgName("setting profile id(int)").create(UPDATE_OPT);
		Option deviceid = OptionBuilder.hasArg(true).withArgName("device id(int)").isRequired(false)
				.withLongOpt(DEVICEID).withDescription("device ID. Max 10").create(DEVICEID_OPT);
		deviceid.setArgs(10);
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
		Option add = OptionBuilder.hasArg(true).isRequired(false).withLongOpt(ADD)
				.withArgName("setting profile id(int)")
				.withDescription("To be used when updating a setting profile with devices to add").create(ADD_OPT);
		options.addOption(list);
		options.addOption(create);
		options.addOption(delete);
		options.addOption(update);
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
		options.addOption(add);
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
		Option delete = OptionBuilder.hasArg(true).isRequired(false).withLongOpt(DELETE)
				.withDescription("Delete a setting profile").withArgName("Device profile id(int)").create(DELETE_OPT);
		Option update = OptionBuilder.hasArg(true).isRequired(false).withLongOpt(UPDATE)
				.withDescription("Update a setting profile").withArgName("Device profile id(int)").create(UPDATE_OPT);
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
			} else if (cmd.hasOption(DEVICES_LIST)) {
				String pDevices = cmd.getOptionValue(DEVICES_LIST);
				String pSettings = cmd.getOptionValue(SETTINGS_INPUT);
				execute(pDevices, pSettings);
			} else if (cmd.hasOption(SETTINGS_INPUT)) {
				String pDevices = cmd.getOptionValue(DEVICES_LIST);
				String pSettings = cmd.getOptionValue(SETTINGS_INPUT);
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
		this.mSettingManager = new SettingManager();
		this.mDeviceManager = new DeviceManager();
		this.mSettingManager.setDeviceManager(this.mDeviceManager);
		this.mDeviceManager.setSettingManager(this.mSettingManager);

		this.COMMAND_OPTIONS = createCommandOptions();
		this.SETTING_OPTIONS = createSettingOptions();
		this.DEVICE_OPTIONS = createDeviceOptions();
		this.mDeviceManager.readInDeviceFile(pDeviceFile);
		this.mSettingManager.readInSettingFile(pSettingFile);
		this.mDeviceManager.loopDevices();
		this.mSettingManager.loopSettings();
		this.mClientManager = new ClientManager(this.mSettingManager, this.mDeviceManager);
		this.mainCommandLoop();
	}

	private void mainCommandLoop() {
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		try {
			while (true) {
				System.out.print("main Input: ");
				String[] pInput = in.readLine().split(" ");
				this.readFromMainCommandLoop(pInput);
			}
		} catch (IOException e) {
			log.error("Error reading in input");
		}
	}

	private void readFromMainCommandLoop(String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.COMMAND_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.COMMAND_OPTIONS);
			} else if (cmd.hasOption(COLLECT_ALL_OPT)) {
				this.mClientManager.collectAll();
			} else if (cmd.hasOption(COLLECT_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(COLLECT_SINGLE_OPT);
				if (pDeviceString != null) {
					this.mClientManager.collectSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", COLLECT_SINGLE);
				}
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.mClientManager.deleteSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
			} else if (cmd.hasOption(DEL_ALL_OPT)) {
				this.mClientManager.deleteAll();
			} else if (cmd.hasOption(DEL_SINGLE_OPT)) {
				final String pDeviceString = cmd.getOptionValue(DEL_SINGLE_OPT);
				if (pDeviceString != null) {
					this.mClientManager.deleteSingle(pDeviceString);
				} else {
					log.info("No device ID supplied with command: {}", DEL_SINGLE);
				}
			} else if (cmd.hasOption(SHUTDOWN_ALL_OPT)) {
				this.mClientManager.shutdownAll();
			} else if (cmd.hasOption(SETTINGMODE)) {
				this.settingMode();
			} else if (cmd.hasOption(DEVICEMODE)) {
				this.deviceMode();
			} else if (cmd.hasOption(LIST_OPT)) {
				this.mDeviceManager.listDevices();
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

	private void settingMode() {
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

	private boolean processSettingInput(final String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.SETTING_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.SETTING_OPTIONS);
			} else if (cmd.hasOption(LIST_OPT)) {
				this.mSettingManager.listSettings();
			} else if (cmd.hasOption(QUIT)) {
				log.info("Quiting setting mode");
				return false;
			} else if (cmd.hasOption(CREATE_OPT)) {
				String pName = cmd.getOptionValue(NAME_OPT);
				String pBuffer = cmd.getOptionValue(BUFFERSIZE_OPT);
				String pPort = cmd.getOptionValue(SERVERPORT_OPT);
				String pStoragePath = cmd.getOptionValue(STORAGEPATH);
				String pFileNamePath = cmd.getOptionValue(FILENAMEPATH);
				String pSDCard = cmd.getOptionValue(SDCARD);
				this.mSettingManager.createSetting(pName, pBuffer, pPort, pStoragePath, pFileNamePath, pSDCard);
			} else if (cmd.hasOption(DELETE_OPT)) {
				String pID = cmd.getOptionValue(DELETE_OPT);
				this.mSettingManager.deleteSetting(pID);
			} else if (cmd.hasOption(UPDATE_OPT)) {
				String pID = cmd.getOptionValue(UPDATE_OPT);
				String pName = cmd.getOptionValue(NAME_OPT);
				String pBuffer = cmd.getOptionValue(BUFFERSIZE_OPT);
				String pPort = cmd.getOptionValue(SERVERPORT_OPT);
				String pStoragePath = cmd.getOptionValue(STORAGEPATH);
				String pFileNamePath = cmd.getOptionValue(FILENAMEPATH);
				String pSDCard = cmd.getOptionValue(SDCARD);
				this.mSettingManager.updateSetting(pID, pName, pBuffer, pPort, pStoragePath, pFileNamePath, pSDCard);
			} else if (cmd.hasOption(WRITE_OPT)) {
				this.mSettingManager.writeSettingsToFile();
			} else if (cmd.hasOption(ADD_OPT)) {
				String pID = cmd.getOptionValue(ADD_OPT);
				String[] pDevices = cmd.getOptionValues(DEVICEID_OPT);
				this.mSettingManager.addDevicesToSetting(pID, pDevices);
			} else {
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.SETTING_OPTIONS);
		}
		return true;
	}

	private void deviceMode() {
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

	private boolean processDeviceInput(final String[] pInput) {
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(this.DEVICE_OPTIONS, pInput);
			if (cmd.hasOption(HELP_OPT)) {
				showHelp(this.DEVICE_OPTIONS);
			} else if (cmd.hasOption(LIST_OPT)) {
				this.mDeviceManager.listDevices();
			} else if (cmd.hasOption(QUIT)) {
				log.info("Quiting device mode");
				return false;
			} else if (cmd.hasOption(CREATE_OPT)) {
				String pName = cmd.getOptionValue(NAME_OPT);
				String pIP = cmd.getOptionValue(IP).trim();
				String pPort = cmd.getOptionValue(SERVERPORT_OPT);
				String pFileName = cmd.getOptionValue(FILENAME);
				this.mDeviceManager.createDevice(pName, pIP, pPort, pFileName);
			} else if (cmd.hasOption(DELETE_OPT)) {
				String pID = cmd.getOptionValue(DELETE_OPT);
				this.mDeviceManager.deleteDevice(pID);
			} else if (cmd.hasOption(UPDATE_OPT)) {
				String pID = cmd.getOptionValue(UPDATE_OPT);
				String pName = cmd.getOptionValue(NAME_OPT);
				String pIP = cmd.getOptionValue(IP);
				String pPort = cmd.getOptionValue(SERVERPORT_OPT);
				String pFileName = cmd.getOptionValue(FILENAME);
				this.mDeviceManager.updateDevice(pID, pName, pIP, pPort, pFileName);
			} else if (cmd.hasOption(WRITE_OPT)) {
				this.mDeviceManager.writeDevices();
			} else {
				log.info("No commands selected, are you putting - before the command?");
			}
		} catch (Exception e) {
			log.error("Error with command input: {}", pInput, e);
			showHelp(this.DEVICE_OPTIONS);
		}
		return true;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
