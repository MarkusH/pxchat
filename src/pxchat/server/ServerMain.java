/**
 * 
 */
package pxchat.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pxchat.net.Server;
import pxchat.util.XMLUtil;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * The main class of the server.
 * 
 * @author Markus Döllinger
 * @author Markus Holtermann
 */
public class ServerMain implements SignalHandler {
	
	private static final String defaultServerList = "http://localhost/servers.php";
	private static final int defaultPort = 12345;
	private static final String defaultName = "pxchat";
	private static final HashMap<String, String> defaultAuthList = new HashMap<String, String>();

	/**
	 * config values initialized with a default value
	 */
	private static String serverList;
	private static int port;
	private static String name = defaultName;
	private static HashMap<String, String> authList;
	
	private static String configFilename = "data/config/server.xml";
	private static Server server;
	private static UDPServer udpServer = new UDPServer();

	/**
	 * The main entry point of the server.
	 * 
	 * @param args The command line arguments
	 * @throws IOException If the I/O of the server fails
	 * @throws InterruptedException Should never occur
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Started pxchat server...");
		
		if (args.length > 0) {
			configFilename = args[0];
		}

		if (!loadConfig())
			return;

		System.out.println("Listening on Port " + port);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				server.close();
				udpServer.interrupt();
				try {
					System.out.println("Delete entry from server list");
					new URL(serverList + "?action=del&p = defaultPortort=" + port).openStream();
				} catch (Exception e) {
					System.out.println("Could not contact master server");
				}
			}
		});
		
		server = new Server();
		server.setAuthList(authList);
		server.listen(port);
		
		Signal reloadSignal = new Signal("USR2");
		ServerMain instance = new ServerMain();
		Signal.handle(reloadSignal, instance);
		
		udpServer.start();
		
		int count = 0;
		while (true) {
			if (count % 6 == 0) {
				updateServerList(name);
				count = 0;
			}
			System.out.println("Connected users: " + server.getUserList());
			count++;
			Thread.sleep(10000);
		}
	}

	/**
	 * Load the settings data from the configuration file and return true on success.
	 * @return True on success otherwise false
	 */
	private static boolean loadConfig() {
		File file = new File(configFilename);
		serverList = defaultServerList;
		port = defaultPort;
		authList = new HashMap<String, String>(defaultAuthList);
		if (file.exists()) {
			System.out.print("Load config ... ");
			Document doc = null;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
	
				builder.setErrorHandler(new ErrorHandler() {
	
					@Override
					public void error(SAXParseException e) throws SAXException {
						throw e;
					}
	
					@Override
					public void fatalError(SAXParseException e) throws SAXException {
						System.out.println("Fatal error validating the config file:");
						e.printStackTrace();
						System.exit(0);
					}
	
					@Override
					public void warning(SAXParseException e) throws SAXException {
						System.out.println("Warning validating the config file:");
						e.printStackTrace();
					}
				});
				doc = builder.parse(file);
				Node node = doc.getDocumentElement();
	
				Node config = XMLUtil.getChildByName(node, "config");
	
				name = XMLUtil.getAttributeValue(XMLUtil.getChildByName(config, "name"),
						"value", defaultName);
				port = Integer.valueOf(XMLUtil.getAttributeValue(XMLUtil.getChildByName(config, "port"),
						"number", Integer.toString(defaultPort)));
				serverList = XMLUtil.getAttributeValue(XMLUtil.getChildByName(config, "serverlist"),
						"url", defaultServerList);
	
				Node auth = XMLUtil.getChildByName(node, "auth");
	
				NodeList list = auth.getChildNodes();
				if (list != null) {
					for (int i = 0; i < list.getLength(); i++) {
						if (list.item(i).getNodeName().equals("user")) {
							authList.put(XMLUtil.getAttributeValue(list.item(i), "name"), XMLUtil
									.getAttributeValue(list.item(i), "password"));
						}
					}
				}
	
			} catch (Exception e) {
				System.out.println("failed!");
				System.out.println("An error ocurred loading the config file");
				e.printStackTrace();
				return false;
			}
			System.out.println("done!");
		} else {
			System.out.println("No config file exists. Using default data.");
		}
		return true;
	}
	
	private static void updateServerList(String name) {
		try {
			System.out.println("Add entry to server list");
			String url = serverList + "?action=add&name=" + URLEncoder.encode(name, "UTF-8") + "&port=" + port;
			new URL(url).openStream();
		} catch (Exception e) {
			System.out.println("Could not contact master server");
		}
	}
	
	@Override
	public void handle(Signal signal) {
        try {
        	loadConfig();
        	server.setAuthList(authList);
        } catch (Exception e) {
            System.out.println("handle|Signal handler failed, reason " + e.getMessage());
            e.printStackTrace();
        }
    }

	/**
	 * @author Markus Döllinger
	 */
	static class UDPServer extends Thread {

		public void run() {
			DatagramSocket socket = null;

			try {
				socket = new DatagramSocket(1337);
				socket.setSoTimeout(100);
			} catch (SocketException e1) {
				e1.printStackTrace();
			}
			while (!isInterrupted()) {
				DatagramPacket packet = new DatagramPacket(new byte[256], 256);
				try {
					socket.receive(packet);
					byte[] data = new byte[packet.getLength()];
					System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
					System.out.println(new String(data));
					String resp = port + " " + name;
					DatagramPacket response = new DatagramPacket(resp.getBytes(), 
							resp.length(), packet.getAddress(), 1338);
					socket.send(response);
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket.close();
		}
	}
	
}


