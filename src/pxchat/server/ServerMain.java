/**
 * 
 */
package pxchat.server;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pxchat.net.Client;
import pxchat.net.tcp.TCPClientListener;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPServerListener;
import pxchat.net.tcp.TCPServer;
import pxchat.util.XMLUtil;

/**
 * @author Markus Döllinger
 * 
 */
public class ServerMain {

	private static HashMap<String, String> authList = new HashMap<String, String>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Started pxchat server...");

		File file = new File("xml/server.xml");
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException e) throws SAXException {
					System.out.println("Warning validating the config file:");
					e.printStackTrace();
				}

				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					System.out.println("Fatal error validating the config file:");
					e.printStackTrace();
					System.exit(0);
				}

				@Override
				public void error(SAXParseException e) throws SAXException {
					throw e;
				}
			});
			doc = builder.parse(file);
			Node node = doc.getDocumentElement();

			Node config = XMLUtil.getChildByName(node, "config");

			int port = Integer.valueOf(XMLUtil.getAttributeValue(XMLUtil.getChildByName(config,
					"port"), "number"));

			System.out.println(port);

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

			System.out.println(authList);

			TCPServer server = new TCPServer(new TCPServerListener() {

				@Override
				public void clientRead(CustomSocket client, Object data) {
					System.out.println("Server> " + client + " read " + data);
				}

				@Override
				public void clientDisconnect(CustomSocket client) {
					System.out.println("Server> " + client + " disconnected.");
				}

				@Override
				public void clientConnecting(CustomSocket client) {
					System.out.println("Server> " + client + " connecting.");
				}

				@Override
				public void clientConnect(CustomSocket client) {
					System.out.println("Server> " + client + " connected.");

				}
			});
			server.listen(port);

			Thread.sleep(1000);

			// Client client = Client.getInstance();
			// client.connect("localhost", port);
			// Thread.sleep(1000);
			// client.disconnect();
			// Thread.sleep(1000);
			// server.close();
			// Thread.sleep(1000);

			Thread.sleep(30000);

			server.close();

		} catch (Exception e) {
			System.out.println("An error ocurred loading the config file");
			e.printStackTrace();
			return;
		}
	}

}
