/**
 * 
 */
package pxchat.server;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pxchat.util.XMLUtil;

/**
 * @author markus
 *
 */
public class ServerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Started pxchat server...");

		File file = new File("server.xml");
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			Node node = doc.getDocumentElement();

			Node config = XMLUtil.getChildByName(node, "config");
			
//			int port = 
//			
//			NodeList list = node.getChildNodes();
//			if (list != null) {
//				for (int i = 0; i < list.getLength(); i++) {
//					System.out.println(list.item(i).getNodeName());
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
