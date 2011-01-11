/**
 * 
 */
package pxchat.server;

import java.io.File;
import java.io.IOException;
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

/**
 * The main class of the server.
 * 
 * @author Markus Döllinger
 * @author Markus Holtermann
 */
public class ServerMain {

	private static String serverList = "http://localhost/servers.php?";

	private static int port = 12345;
	private static HashMap<String, String> authList = new HashMap<String, String>();

	/**
	 * The main entry point of the server.
	 * 
	 * @param args The command line arguments
	 * @throws IOException If the I/O of the server fails
	 * @throws InterruptedException Should never occur
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("Started pxchat server...");

		if (!loadConfig())
			return;

		System.out.println("Listening on Port " + port);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					System.out.println("Delete entry from server list");
					new URL(serverList + "action=del").openStream();
				} catch (Exception e) {
					System.out.println("Could not contact master server");
				}
			}
		});
		
		try {
			System.out.println("Add entry to server list");
			String name = (args.length == 0) ? "pxchat" : args[0];
			String url = serverList + "action=add&name=" + URLEncoder.encode(name, "UTF-8") + "&port=" + port;
			new URL(url).openStream();
		} catch (Exception e) {
			System.out.println("Could not contact master server");
		}

		Server server = new Server();
		server.setAuthList(authList);
		server.listen(port);

		while (true) {
			System.out.println("Connected users: " + server.getUserList());
			Thread.sleep(10000);
		}
	}

	/**
	 * Load the settings data from the configuration file and return true on success.
	 * @return True on success otherwise false
	 */
	private static boolean loadConfig() {
		File file = new File("data/config/server.xml");
		if (file.exists()) {
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
	
				port = Integer.valueOf(XMLUtil.getAttributeValue(XMLUtil.getChildByName(config, "port"),
						"number", "12345"));
				serverList = XMLUtil.getAttributeValue(XMLUtil.getChildByName(config, "serverlist"),
						"url", "http://localhost/servers.php") + "?";
	
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
				System.out.println("An error ocurred loading the config file");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
