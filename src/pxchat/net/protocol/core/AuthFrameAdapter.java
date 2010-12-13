/**
 * 
 */
package pxchat.net.protocol.core;

import pxchat.net.tcp.CustomSocket;

/**
 * This class extends the frame adapter so that it is possible to store the
 * authentication state of the adapter. It is used only for the server frame
 * adapter.
 * 
 * @author Markus Döllinger
 */
public class AuthFrameAdapter extends FrameAdapter {
	
	private boolean authenticated = false;
	private boolean versionVerified = false;

	/**
	 * Constructs a new authentication frame adapter.
	 * 
	 * @param socket
	 * @param listener
	 */
	public AuthFrameAdapter(CustomSocket socket, FrameAdapterListener listener) {
		super(socket, listener);
	}
	

	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @return the versionVerified
	 */
	public boolean isVersionVerified() {
		return versionVerified;
	}

	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * @param versionVerified the versionVerified to set
	 */
	public void setVersionVerified(boolean versionVerified) {
		this.versionVerified = versionVerified;
	}

}
