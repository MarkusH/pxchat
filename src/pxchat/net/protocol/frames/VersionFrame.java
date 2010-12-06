package pxchat.net.protocol.frames;

public class VersionFrame extends Frame {

	private int major;
	private int minor;

	VersionFrame(int major, int minor) {
		this.major = major;
		this.minor = minor;
		this.id = Frame.ID_VERSION;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	boolean isCompatible(VersionFrame ver) {
		if (ver.getMajor() == this.getMajor())
			return true;
		else
			return false;
	}
	
	public static VersionFrame getCurrent() {
		return new VersionFrame(0, 1);
	}

}
