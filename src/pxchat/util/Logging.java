/**
 * 
 */
package pxchat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

/**
 * @author Markus H.
 * 
 */
public class Logging {

	private File fUser, fMessages;
	private String start, end;
	private Writer oswLog, oswUser, oswMessage;
	private String logfilename, userfilename, msgfilename;
	private static final String encoding = "UTF-8";

	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat sdf;

	/**
	 * 
	 */
	public Logging() {
		String time = getFilenameDateTime();
		logfilename = "log/log" + time + ".xml";
		userfilename = "log/.usr" + time;
		msgfilename = "log/.msg" + time;

		start = "\t<start date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\" />\n";

		try {
			File file = new File("log/");
			if (!file.exists())
				file.mkdir();
			else
				if (!file.isDirectory())
					throw new IOException(file.getAbsolutePath() + " is not a directory");

			fUser = new File(userfilename);
			if (!fUser.exists())
				fUser.createNewFile();
			oswUser = new OutputStreamWriter(new FileOutputStream(fUser),
					encoding);

			fMessages = new File(msgfilename);
			if (!fMessages.exists())
				fMessages.createNewFile();
			oswMessage = new OutputStreamWriter(
					new FileOutputStream(fMessages), encoding);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * endLog finishes the logging and stores the document
	 */
	public void endLog() {
		end = "\t<end date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\" />\n";
		try {
			File fLog = new File(logfilename);
			if (!fLog.exists())
				fLog.createNewFile();
			oswLog = new OutputStreamWriter(new FileOutputStream(fLog),
					encoding);
			/**
			 * build the basic header of a log file
			 */
			oswLog.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
			oswLog.write("<!DOCTYPE pxchatlog SYSTEM \"pxchatlog.dtd\">\n");
			oswLog.write("<?xml-stylesheet href=\"pxchatlog.xsl\" type=\"text/xsl\" ?>\n");
			oswLog.write("<pxchatlog>\n");
			oswLog.flush();

			/**
			 * put all participants in the log This needs a closing of oswUser
			 * first!
			 */
			oswUser.close();
			Scanner scanner;
			oswLog.write("\t<participants>\n");
			scanner = new Scanner(new FileInputStream(fUser), encoding);
			while (scanner.hasNextLine()) {
				oswLog.write(scanner.nextLine() + "\n");
			}
			scanner.close();
			oswLog.write("\t</participants>\n");
			oswLog.flush();
			fUser.delete();

			/**
			 * let us write the duration of this chat
			 */
			oswLog.write(start);
			oswLog.write(end);
			oswLog.flush();

			/**
			 * we can now append the temporary message log. again, we have to
			 * close that OutputStreamWriter first.
			 */
			oswMessage.close();
			oswLog.write("\t<chat>\n");
			scanner = new Scanner(new FileInputStream(fMessages), encoding);
			while (scanner.hasNextLine()) {
				oswLog.write(scanner.nextLine() + "\n");
			}
			scanner.close();
			oswLog.write("\t</chat>\n");

			oswLog.flush();
			fMessages.delete();

			/**
			 * finish the complete log
			 */
			oswLog.write("</pxchatlog>\n");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (oswLog != null)
					oswLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a simple log message to the history
	 * 
	 * @param message the message to store
	 * @param author the author of the message
	 */
	public void logMessage(String message, String author) {
		try {
			oswMessage
					.write("\t\t<message author=\"" + author + "\" date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\">" + message + "</message>\n");
			oswMessage.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logParticipant(String user) {
		try {
			oswUser.write("\t\t<name date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\">" + user + "</name>\n");
			oswUser.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Call this function when a user joins the chat
	 * 
	 * @param user
	 */
	public void logJoin(String user) {
		try {
			oswMessage
					.write("\t\t<join user=\"" + user + "\" date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\" />\n");
			oswMessage.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logParticipant(user);
	}

	/**
	 * Call this function when a user leaves the chat
	 * 
	 * @param user
	 */
	public void logLeave(String user) {
		try {
			oswMessage
					.write("\t\t<leave user=\"" + user + "\" date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\" />\n");
			oswMessage.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Call this function when a <invitor> has invited <user>
	 * 
	 * @param user
	 */
	public void logInvite(String invitor, String user) {
		try {
			oswMessage
					.write("\t\t<invite user1=\"" + invitor + "\" user2=\"" + user + "\" date=\"" + getLogDate() + "\" time=\"" + getLogTime() + "\" />\n");
			oswMessage.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String formatDateTime(String format) {
		sdf = new SimpleDateFormat(format);
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
