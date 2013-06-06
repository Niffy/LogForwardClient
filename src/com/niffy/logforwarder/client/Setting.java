package com.niffy.logforwarder.client;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Setting {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(Setting.class);

	// ===========================================================
	// Fields
	// ===========================================================
	protected String mName;
	protected int mBuffer;
	protected int mServerPort;
	protected String mStoragePath;
	protected String mFileNamePath;
	protected boolean mSDCard;
	protected ArrayList<String> mDevices;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Setting() {
		this.mDevices = new ArrayList<String>();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public String getName() {
		return this.mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public int getBuffer() {
		return this.mBuffer;
	}

	public void setBuffer(int buffer) {
		this.mBuffer = buffer;
	}

	public int getServerPort() {
		return this.mServerPort;
	}

	public void setServerPort(int serverPort) {
		this.mServerPort = serverPort;
	}

	public String getStoragePath() {
		return this.mStoragePath;
	}

	public void setStoragePath(String storagePath) {
		this.mStoragePath = storagePath;
	}

	public String getFileNamePath() {
		return this.mFileNamePath;
	}

	public void setFileNamePath(String fileNamePath) {
		this.mFileNamePath = fileNamePath;
	}

	public boolean getSDCard() {
		return this.mSDCard;
	}

	public void setSDCard(boolean sDCard) {
		this.mSDCard = sDCard;
	}

	public ArrayList<String> getDevices() {
		return this.mDevices;
	}

	public void addDevices(String device) {
		this.mDevices.add(device);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
