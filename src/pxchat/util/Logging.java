/**
 * 
 */
package pxchat.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Markus H.
 *
 */
public class Logging {
	
	private File file;
	private Document doc;
	private Node node;
	private Element participants, start, end, chat;
	String filename;

	/**
	 * 
	 */
	public Logging() {
		filename = "log/log" + getFilenameDateTime() + ".xml";
		try {
			file = new File("log/");
			if (!file.exists())
				file.mkdir();
			else if (!file.isDirectory())
				throw new IOException(file.getAbsolutePath() + " is not a directory");
			FileWriter fw = new FileWriter(filename);
			fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			fw.write("<!DOCTYPE pxchatlog SYSTEM \"../xml/pxchatlog.dtd\">\n");
			fw.write("<?xml-stylesheet type=\"text/xsl\" href=\"pxchatlog.xsl\"?>\n");
			fw.write("<pxchatlog></pxchatlog>");
			fw.close();
			file = new File(filename);
			if (!file.exists())
				throw new IOException("cannot open log file!");
			/**
			 * doc is a reference to the whole document. Used to create elements.
			 */
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			/**
			 * node contains the root-element of `doc`
			 */
			node = doc.getDocumentElement();
			/**
			 * directly create a participants tag. Participants are stored when they join
			 */
			participants = doc.createElement("participants");
			node.appendChild(participants);
			/**
			 * setting the log start
			 */
			start = doc.createElement("start");
			start.setAttribute("date", getLogDate());
			start.setAttribute("time", getLogTime());
			node.appendChild(start);
			/**
			 * all log messages are stored in <chat>
			 */
			chat = doc.createElement("chat");
			node.appendChild(chat);
		} catch (Exception e) {
			System.out.println("An error ocurred loading the log file " + filename);
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * endLog finishes the logging and stores the document
	 */
	public void endLog() {
		end = doc.createElement("end");
		end.setAttribute("date", getLogDate());
		end.setAttribute("time", getLogTime());
		node.insertBefore(end, chat);
		XMLSerializer serializer = new XMLSerializer();
	    try {
			serializer.setOutputCharStream(new java.io.FileWriter(filename));
		    serializer.serialize(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a simple log message to the history
	 * @param message the message to store
	 * @param author the author of the message
	 */
	public void logMessage(String message, String author) {
		Element msg = doc.createElement("message");
		msg.setAttribute("author", author);
		msg.setAttribute("date", getLogDate());
		msg.setAttribute("time", getLogTime());
		msg.appendChild(doc.createTextNode(message));
		chat.appendChild(msg);
	}
	
	private void logSystemMessage(String message) {
		Element msg = doc.createElement("message");
		msg.setAttribute("author", "System");
		msg.setAttribute("date", getLogDate());
		msg.setAttribute("time", getLogTime());
		msg.setAttribute("type", "system");
		msg.appendChild(doc.createTextNode(message));
		chat.appendChild(msg);
	}
	
	private void logParticipant(String user) {
		Element name = doc.createElement("name");
		name.setAttribute("date", getLogDate());
		name.setAttribute("time", getLogTime());
		name.appendChild(doc.createTextNode(user));
		participants.appendChild(name);
	}
	
	/**
	 * Call this function when a user joins the chat
	 * @param user
	 */
	public void logJoin(String user) {
		logSystemMessage(user + " hast joined the chat!");
		logParticipant(user);
	}
	
	/**
	 * Call this function when a user leaves the chat
	 * @param user
	 */
	public void logLeave(String user) {
		logSystemMessage(user + " has left the chat!");
	}
	
	/**
	 * Call this function when a <invitor> has invited <user>
	 * @param user
	 */
	public void logInvite(String invitor, String user) {
		logSystemMessage(invitor + " has invited " + user + "!");
	}
	
	private String formatDateTime(String format) {
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(cal.getTime());
	}

	private String getFilenameDateTime() {
	    return formatDateTime("yyyyMMddHHmmss");
	}

	private String getLogDate() {
	    return formatDateTime("dd/MM/yyyy");
	}

	private String getLogTime() {
	    return formatDateTime("HH:mm:ss");
	}

}
