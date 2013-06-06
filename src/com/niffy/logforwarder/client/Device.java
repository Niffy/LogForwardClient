package com.niffy.logforwarder.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Device {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(Device.class);

	// ===========================================================
	// Fields
	// ===========================================================
	protected int mID = -1;
	protected String mName = "unknown";
	protected String mAddress;
	protected int mPort = -1;
	protected String mFileName;

	// ===========================================================
	// Constructors
	// ===========================================================

	public Device() {
		
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public int getID() {
		return mID;
	}

	public void setID(int pID) {
		this.mID = pID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String pName) {
		this.mName = pName;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String pAddress) {
		this.mAddress = pAddress;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int pPort) {
		this.mPort = pPort;
	}

	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String pFileName) {
		this.mFileName = pFileName;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
