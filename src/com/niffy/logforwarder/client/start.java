package com.niffy.logforwarder.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class start {

	public static NioServer mServer;
	public static NioClient mClient;
	public static UDPServer mUDP;
	public static EchoWorker mWorker;
	public static int mChoice;
	public static InetAddress mLocalAddress;
	public static int mLocalTCPPort;
	public static int mLocalUDPPort;
	public static int mRemoteTCPPort;
	public static int mRemoteUDPPort;
	public static InetAddress mRemoteAddress;

	public static RspHandler handler;
	
	public static ArrayList<host> mHosts = new ArrayList<host>();

	public static void main(String[] args) throws UnknownHostException {
		mChoice = Integer.parseInt(args[0]);
		mLocalAddress = InetAddress.getLocalHost();
		mRemoteAddress = InetAddress.getLocalHost();

		mWorker = new EchoWorker();
		new Thread(mWorker).start();

		if (mChoice == 0) {
			mLocalTCPPort = Integer.parseInt(args[1]);
			mRemoteTCPPort = Integer.parseInt(args[2]);
			if (args.length == 4) {
				mRemoteAddress = InetAddress.getByName(args[3]);
				System.out.println("Using remote address: " + mRemoteAddress.getHostAddress());
			}
			try {
				mServer = new NioServer(mLocalAddress, mRemoteAddress, mLocalTCPPort, mRemoteTCPPort, mWorker);
				new Thread(mServer).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(mChoice == 1) {
			mLocalTCPPort = Integer.parseInt(args[1]);
			mRemoteTCPPort = Integer.parseInt(args[2]);
			if (args.length == 4) {
				mRemoteAddress = InetAddress.getByName(args[3]);
				System.out.println("Using remote address: " + mRemoteAddress.getHostAddress());
			}
			try {
				mClient = new NioClient(mRemoteAddress, mRemoteTCPPort, mLocalTCPPort);
				new Thread(mClient).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (mChoice == 2){
			mLocalUDPPort = Integer.parseInt(args[1]);
			mRemoteUDPPort = Integer.parseInt(args[2]);
			try {
				mUDP = new UDPServer(mLocalUDPPort);
				new Thread(mUDP).start();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		
		handler = new RspHandler();

		process();
	}

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
				}else if (CurLine.equals("udp")){
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
			} else if (CurLine.equals("keys")) {
				viewKeys();
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
	
	public static void udpCommand(BufferedReader in) {
		String CurLine = "";
		try {
			System.out.print("UDP : ");
			CurLine = in.readLine();
			if (CurLine.equals("quit")) {
				System.exit(0);
			} else if (CurLine.equals("send")) {
				System.out.print("Send : ");
				CurLine = in.readLine();
				if (CurLine.equals("up")) {
					return;
				} else {
					byte[] pBytes = CurLine.getBytes();
					sendUDP(in, pBytes);
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
				if(port != -1){
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

	public static void send(BufferedReader in, byte[] pBytes) throws IOException {
		String CurLine = "";
		try {
			System.out.println("Send to whom? Choices : ");
			int count = 0;
			for (host b : mHosts) {
				System.out.println(count + " : host op: " + b.ip + " port: " + b.port);
				count++;
			}
			CurLine = in.readLine();
			if (CurLine.equals("quit")) {
				System.exit(0);
			} else if (CurLine.equals("up")){
				return;
			}else {
				try{
					int index = Integer.valueOf(CurLine);
					host b = mHosts.get(index);
					if(b != null){
						System.out.println("Using: host op: " + b.ip + " port: " + b.port);
						sendTo(pBytes, b.ip, b.port);
					}
				}catch (NumberFormatException e){
					System.out.println("Not a number, going up");
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendTo(byte[] pBytes, String ip, int port)throws IOException{
		if (mChoice == 0) {
			mServer.send(mRemoteAddress.getHostAddress().toString(), pBytes);
		} else if (mChoice == 1) {
			mClient.send(mRemoteAddress.getHostAddress().toString(), mRemoteTCPPort, pBytes, handler);
		} else if (mChoice == 2) {
			mUDP.send(ip, port, pBytes);
		}
	}
	
	public static void connect(String pIP, int pPort) throws IOException {
		if (mChoice == 0) {
			mServer.connect(mRemoteAddress.getHostAddress().toString());
		} else {
			mClient.connect(pIP, pPort);
		}
	}

	public static void viewKeys() {
		if (mChoice == 0) {
			mServer.viewKeys();
		}
	}

	public static void sendUDP(BufferedReader in, byte[] pBytes) throws IOException {
		String CurLine = "";
		try {
			System.out.println("Send to whom?: ");
			CurLine = in.readLine();
			if (CurLine.equals("quit")) {
				System.exit(0);
			} else if (CurLine.equals("up")){
				return;
			}else {
				try{
					int port = getPort(CurLine);
					String ip = getIP(CurLine);
					if(port != -1){
						System.out.println("Found: " + ip + " " + port);
						sendTo(pBytes, ip, port);
					}
				}catch (NumberFormatException e){
					System.out.println("Not a number, going up");
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
}
