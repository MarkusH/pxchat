package pxchat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This static class contains the client configuration. It provides a mapping of
 * keys to values and a list of connection profiles. The configuration file can
 * be loaded by calling {@link #init(String)} with a proper file name. By
 * calling {@link #save()}, the current configuration state is saved to the file
 * this class was initialized with.
 * 
 * @author Markus Döllinger
 */
public final class Config {

	/**
	 * The config file.
	 */
	private static File file = null;

	/**
	 * A key value mapping.
	 */
	private static HashMap<String, String> config = new HashMap<String, String>();

	/**
	 * A list of profiles.
	 */
	private static Vector<Profile> profiles = new Vector<Config.Profile>();

	/**
	 * Private default constructor.
	 */
	private Config() {
	}

	/**
	 * Saves the config the the file it was loaded from.
	 */
	public static void save() {
		try {
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = b.newDocument();

			Node nClient = doc.createElement("client");
			doc.appendChild(nClient);

			Node nConfig = doc.createElement("config");
			nClient.appendChild(nConfig);

			// save key value mapping
			for (String key : config.keySet()) {
				Element setting = doc.createElement("setting");
				setting.setAttribute("key", key);
				setting.setAttribute("value", config.get(key));
				nConfig.appendChild(setting);
			}

			Node nProfiles = doc.createElement("profiles");
			nClient.appendChild(nProfiles);

			// save profiles
			for (Profile prof : profiles) {
				Element profile = doc.createElement("profile");
				profile.setAttribute("name", prof.name);
				profile.setAttribute("host", prof.host);
				profile.setAttribute("port", prof.port);
				profile.setAttribute("username", prof.userName);
				profile.setAttribute("password", prof.password);
				nProfiles.appendChild(profile);
			}

			// save document to file
			FileOutputStream fos = new FileOutputStream(file);
			OutputFormat of = new OutputFormat("XML", "UTF-8", true);
			of.setIndent(4);
			of.setLineWidth(0);
			of.setLineSeparator("\r\n");
			of.setDoctype(null, "../xml/client.dtd");
			XMLSerializer serializer = new XMLSerializer(fos, of);
			serializer.asDOMSerializer();
			serializer.serialize(doc.getDocumentElement());
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the value associated with the key.
	 * 
	 * @param key The key
	 * @return The associated value
	 */
	public static String get(String key) {
		return config.get(key);
	}

	/**
	 * Puts a new key value pair into the mapping. If the key is already
	 * available, its value is replaced.
	 * 
	 * @param key The key
	 * @param value The associated value
	 */
	public static void put(String key, String value) {
		config.put(key, value);
	}

	/**
	 * Returns an array of profiles.
	 * 
	 * @return The profile array
	 */
	public static Profile[] getProfiles() {
		return profiles.toArray(new Profile[profiles.size()]);
	}

	/**
	 * Returns the default profile. The default profile is the profile with the
	 * name specified in the key value mapping with the key defaultProfile.
	 * 
	 * @return The default profile
	 */
	public static Profile getDefaultProfile() {
		for (Profile prof : profiles) {
			if (prof.equals(config.get("defaultProfile")))
				return prof;
		}
		return null;
	}

	/**
	 * Adds a profile the the list of profiles.
	 * 
	 * @param profile The new profile.
	 */
	public static void addProfile(Profile profile) {
		profiles.add(profile);
	}

	/**
	 * This class represents a connection profile.
	 * 
	 * @author Markus Döllinger
	 */
	public static class Profile {
		private String name;
		private String host;
		private String port;
		private String userName;
		private String password;

		/**
		 * Constructs a new profile with the specified values.
		 * 
		 * @param name The name of the profile
		 * @param host The host
		 * @param port The port
		 * @param userName The user name
		 * @param password The password
		 */
		public Profile(String name, String host, String port, String userName, String password) {
			this.name = name;
			this.host = host;
			this.port = port;
			this.userName = userName;
			this.password = password;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return name == null ? "null" : name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof String)
				return name.equals(obj);

			if (obj instanceof Profile) {
				Profile that = (Profile) obj;
				return this.name.equals(that.name) && this.host.equals(that.host) && this.port
						.equals(that.port) && this.userName.equals(that.userName) && this.password
						.equals(that.password);
			}
			return false;
		}

		/**
		 * @return the host
		 */
		public String getHost() {
			return host;
		}

		/**
		 * @param host the host to set
		 */
		public void setHost(String host) {
			this.host = host;
		}

		/**
		 * @return the port
		 */
		public String getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(String port) {
			this.port = port;
		}

		/**
		 * @return the userName
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * @param userName the userName to set
		 */
		public void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * @param password the password to set
		 */
		public void setPassword(String password) {
			this.password = password;
		}
	}

	/**
	 * Loads the client config from the specified file.
	 * 
	 * @param fileName The config file
	 */
	public static void init(String fileName) {
		config.clear();
		profiles.clear();
		config.put("defaultLanguage", Locale.getDefault().getLanguage() +
				(Locale.getDefault().getCountry() != ""
					? "_" + Locale.getDefault().getCountry()
					: ""));
		config.put("defaultProfile", "");

		file = new File(fileName);
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
			Document document = builder.parse(file);
			Node node = document.getDocumentElement();

			Node configNode = XMLUtil.getChildByName(node, "config");
			NodeList listConfig = configNode.getChildNodes();
			if (listConfig != null) {
				for (int i = 0; i < listConfig.getLength(); i++) {
					if (listConfig.item(i).getNodeName().equals("setting")) {
						config.put(XMLUtil.getAttributeValue(listConfig.item(i), "key"), XMLUtil
								.getAttributeValue(listConfig.item(i), "value"));
					}
				}
			}
			Node profilesNode = XMLUtil.getChildByName(node, "profiles");
			NodeList listProfiles = profilesNode.getChildNodes();
			if (listProfiles != null) {
				for (int i = 0; i < listProfiles.getLength(); i++) {
					if (listProfiles.item(i).getNodeName().equals("profile")) {
						String host = XMLUtil.getAttributeValue(listProfiles.item(i), "host");
						String port = XMLUtil.getAttributeValue(listProfiles.item(i), "port");
						String userName = XMLUtil.getAttributeValue(listProfiles.item(i),
								"username");
						String password = XMLUtil.getAttributeValue(listProfiles.item(i),
								"password");
						String name = XMLUtil.getAttributeValue(listProfiles.item(i), "name");
						profiles.add(new Profile(name, host, port, userName, password));
					}
				}
			}
			boolean hasDefault = false;
			for (Profile prof : profiles) {
				if (prof.equals(config.get("defaultProfile"))) {
					hasDefault = true;
					break;
				}
			}
			if (!hasDefault) {
				profiles
						.add(new Profile(config.get("defaultProfile"), "localhost", "12345", "", ""));
			}
		} catch (Exception e) {
			System.out.println("An error ocurred loading the config file");
		}
	}
}
