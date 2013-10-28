package com.niffy.logforwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.logforwarder.lib.logmanagement.ILogManager;
import com.niffy.logforwarder.lib.logmanagement.LogManagerClient;

/**
 * 
 * @author Paul Robinson
 * @since 28 Oct 2013 15:03:32
 */
public class ClientManager {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(ClientManager.class);

	// ===========================================================
	// Fields
	// ===========================================================
	private DeviceManager mDeviceManager;
	private SettingManager mSettingManager;
	private CustomClientSelector mClientSelector;
	private InetSocketAddress mAddress;
	private ILogManager mLogManager;
	private int VERSIONCODE = 0;
	private Requester mRequester;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ClientManager(final SettingManager pSettingManager, final DeviceManager pDeviceManager) {
		this.mSettingManager = pSettingManager;
		this.mDeviceManager = pDeviceManager;

		this.mLogManager = new LogManagerClient(this.VERSIONCODE);
		this.mAddress = new InetSocketAddress(Defaults.CLIENT_PORT);
		try {
			this.mClientSelector = new CustomClientSelector("Client Selector", this.mAddress, Defaults.BUFFER,
					Defaults.SERVER_PORT, this.mLogManager);
			new Thread(this.mClientSelector).start();
		} catch (IOException e) {
			log.error("Error creating selector", e);
		}
		this.mRequester = new Requester(this.mClientSelector, this.mDeviceManager, this.mSettingManager, VERSIONCODE);
		this.mLogManager.setSelector(mClientSelector);
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
	public void collectAll() {
		this.mRequester.getAll();
	}

	public void collectSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.collectSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	public void collectSingle(final int pDevice) {
		Device pDeviceObj = this.mDeviceManager.getDevice(pDevice);
		this.collectSingle(pDeviceObj);
	}

	public void collectSingle(final Device pDevice) {
		if (pDevice != null) {
			this.mRequester.getSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	public void deleteAll() {
		this.mRequester.deleteAll();
	}

	public void deleteSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.deleteSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	public void deleteSingle(final int pDevice) {
		Device pDeviceObj = this.mDeviceManager.getDevice(pDevice);
		this.deleteSingle(pDeviceObj);
	}

	public void deleteSingle(final Device pDevice) {
		if (pDevice != null) {
			this.mRequester.deleteSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	public void shutdownAll() {
		this.mRequester.shutdownAll();
	}

	public void shutdownSingle(final String pDevice) {
		try {
			final int pDeviceNo = Integer.parseInt(pDevice);
			this.deleteSingle(pDeviceNo);
		} catch (NumberFormatException e) {
			log.error("Could not get a number, going up..^");
			return;
		}
	}

	public void shutdownSingle(final int pDevice) {
		Device pDeviceObj = this.mDeviceManager.getDevice(pDevice);
		this.deleteSingle(pDeviceObj);
	}

	public void shutdownSingle(final Device pDevice) {
		if (pDevice != null) {
			this.mRequester.shutdownSingle(pDevice);
		} else {
			log.info("Could not find a device with that ID");
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
