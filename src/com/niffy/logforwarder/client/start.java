package com.niffy.logforwarder.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niffy.logforwarder.lib.ClientSelector;
import com.niffy.logforwarder.lib.logmanagement.ILogManager;
import com.niffy.logforwarder.lib.logmanagement.LogManagerClient;

public class start {
	private final static Logger log = LoggerFactory.getLogger(start.class);
	public static ClientSelector CLIENT_SELECTOR;
	public static InetSocketAddress ADDRESS;
	public static int PORT = 1006;
	public static int BUFFER = 20971520;
	public static int SERVER_PORT = 1007;
	public static ILogManager LOG_MANAGER;
	public static int VERSION = 0;

	public static void main(String[] args) {
		log.info("Started");
		LOG_MANAGER = new LogManagerClient(VERSION);
		ADDRESS = new InetSocketAddress(PORT);
		try {
			CLIENT_SELECTOR = new ClientSelector("Client Selector", ADDRESS, BUFFER, SERVER_PORT, LOG_MANAGER);
			new Thread(CLIENT_SELECTOR).start();
		} catch (IOException e) {
			log.error("Error creating selector", e);
		}

		// process();
	}
	/*
		public static void process() {
			String CurLine = ""; // Line read from standard in
			InputStreamReader converter = new InputStreamReader(System.in);
			BufferedReader in = new BufferedReader(converter);
			try {
				while (true) {
					System.out.print("main Input: ");
					CurLine = in.readLine();

					if (CurLine.equals("quit")) {
						System.exit(0);
					} else if (CurLine.equals("tcp")) {
						tcpCommand(in);
					} else if (CurLine.equals("udp")) {
						udpCommand(in);
					}
				}
			} catch (IOException e) {
				System.out.println("error");
				e.printStackTrace();
			}
		}

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
