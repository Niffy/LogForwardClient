package com.niffy.logforwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.logforwarder.lib.ClientSelector;
import com.niffy.logforwarder.lib.Data;
import com.niffy.logforwarder.lib.Flag;
import com.niffy.logforwarder.lib.logmanagement.ILogManager;

public class CustomClientSelector extends ClientSelector {
	// ===========================================================
	// Constants
	// ===========================================================
	private final Logger log = LoggerFactory.getLogger(CustomClientSelector.class);

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	public CustomClientSelector(final String pName, final InetSocketAddress pAddress, final int pServerPort,
			final ILogManager pLogManager) throws IOException {
		super(pName, pAddress, pServerPort, pLogManager);
	}

	public CustomClientSelector(final String pName, final InetSocketAddress pAddress, final int pBufferCapacity,
			final int pServerPort, final ILogManager pLogManager) throws IOException {
		super(pName, pAddress, pBufferCapacity, pLogManager);
	}


	@Override
	protected void sendToThread(int pFlag, HashMap<String, Object> pMap) {
		super.sendToThread(pFlag, pMap);
		if(pFlag == Flag.CLIENT_CONNECTED.getNumber()){
			InetSocketAddress pAddress = (InetSocketAddress) pMap.get(Data.IP_INETADDRESS);
			if(pAddress != null){
				this.mLogManager.newClient(pAddress);
			}else{
				log.error("Could not get IP address on client connect to send outwards");
			}
		}
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
