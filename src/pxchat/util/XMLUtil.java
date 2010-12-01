package pxchat.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides methods helping to parse XML files
 * using a DOM parser.
 * 
 * @author Markus Döllinger
 */
public class XMLUtil {

	/**
	 * Returns the first child of a node with a specified name.
	 * @param node		The parent node.
	 * @param name		The name of the child.
	 * @return			The first child of <code>node</code> 
	 * 					named <code>name</code>.
	 */
	public static Node getChildByName(Node node, String name) {
		try {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeName().equals(name)) {
					return list.item(i);
				}
			}
			return null;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns the text content of a node or an empty string
	 * if an error occurs. 
	 * @param node		The node.
	 * @return			The text content or an empty string.
	 */
	public static String getTextContent(Node node) {
		return getTextContent(node, "");
	}
	
	/**
	 * Returns the text content of a node or an empty string
	 * if an error occurs. 
	 * @param node				The node.
	 * @param defaultValue		The default value.
	 * @return					The text content or the default value.
	 */
	public static String getTextContent(Node node, String defaultValue) {
		try {
			String content = null;
			if (node != null) {
				content = node.getTextContent();
			}
			return content != null ? content : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns an attribute value of a specified node. If this attribute does
	 * not exits an empty string will be returned.
	 * @param node			The node containing the attribute.
	 * @param attribute		The name of the attribute.
	 * @return				The attribute value.
	 */
	public static String getAttributeValue(Node node, String attribute) {
		return getAttributeValue(node, attribute, "");
	}
	
	/**
	 * Returns an attribute value of a specified node. If this attribute does
	 * not exits a specified default value will be returned.
	 * @param node			The node containing the attribute.
	 * @param attribute		The name of the attribute.
	 * @param defaultValue	The value returned if the attribute does not exist.
	 * @return				The attribute value.
	 */
	public static String getAttributeValue(Node node, String attribute, String defaultValue) {
		try {
			return node.getAttributes().getNamedItem(attribute).getTextContent();
		} catch (Exception e) {
			return defaultValue;
		}
	}
}