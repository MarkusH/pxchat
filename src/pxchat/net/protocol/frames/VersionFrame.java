package pxchat.net.protocol.frames;

/**
 * This frame is used to send the version of the client software to the server.
 * Should the version not be compatible, the connection is closed.
 * 
 * @author Robert Waury
 */
public class VersionFrame extends Frame {

	private static final long serialVersionUID = -8803844865253355320L;

	/**
	 * Creates a new frame with the current version of the program.
	 * 
	 * @return The current version
	 */
	public static VersionFrame getCurrent() {
		return new VersionFrame(2, 0);
	}

	/**
	 * The major number of the version.
	 */
	private int major;

	/**
	 * The minor number of the version.
	 */
	private int minor;

	/**
	 * Constructs a new version frame with the specified major and minor
	 * numbers.
	 * 
	 * @param major The major number
	 * @param minor The minor number
	 */
	private VersionFrame(int major, int minor) {
		this.major = major;
		this.minor = minor;
		this.id = Frame.ID_VERSION;
	}

	/**
	 * @return The major number
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return The minor number
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Checks whether the version is compatible with the specified version. A
	 * difference in the minor number does not make two versions incompatible, a
	 * difference in the major numbers does.
	 * 
	 * @param ver The version to check compatibility with.
	 * @return <code>true</code> if the versions are compatible,
	 *         <code>false</code> else
	 */
	public boolean isCompatible(VersionFrame ver) {
		if (ver.getMajor() == this.getMajor())
			return true;
		else
			return false;
	}

}
