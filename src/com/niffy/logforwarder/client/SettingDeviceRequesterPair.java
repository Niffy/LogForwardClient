package com.niffy.logforwarder.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to store a pairing of a request. Every request, be it shutdown,
 * delete or get will have a unique sequence id. We need to match that sequence
 * id response to a device and a setting profile in order to succesfully handle
 * any remaining tasks, such as writing a log file to disk
 * 
 * @author Paul Robinson
 * @since 31 Oct 2013 14:11:46
 */
public class SettingDeviceRequesterPair {
	// ===========================================================
	// Constants
	// ===========================================================
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(SettingDeviceRequesterPair.class);

	// ===========================================================
	// Fields
	// ===========================================================
	private Setting mSetting;
	private Device mDevice;
	private int mSeqence;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SettingDeviceRequesterPair(final Setting pSetting, final Device pDevice, final int pSequence) {
		this.mSetting = pSetting;
		this.mDevice = pDevice;
		this.mSeqence = pSequence;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	/**
	 * @return the mSetting
	 */
	public Setting getSetting() {
		return mSetting;
	}

	/**
	 * @param mSetting
	 *            the mSetting to set
	 */
	public void setSetting(Setting pSetting) {
		this.mSetting = pSetting;
	}

	/**
	 * @return the mDevice
	 */
	public Device getDevice() {
		return mDevice;
	}

	/**
	 * @param mDevice
	 *            the mDevice to set
	 */
	public void setDevice(Device pDevice) {
		this.mDevice = pDevice;
	}

	/**
	 * @return the mSeqence
	 */
	public int getSeqence() {
		return mSeqence;
	}

	/**
	 * @param mSeqence
	 *            the mSeqence to set
	 */
	public void setSeqence(int pSeqence) {
		this.mSeqence = pSeqence;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
