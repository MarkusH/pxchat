package pxchat.net.protocol.frames;

/**
 * This frame is used to lock and unlock the whiteboard.
 * 
 * @author Markus Holtermann
 */
public class LockFrame extends Frame {

	private static final long serialVersionUID = -7351942744915931659L;

	/**
	 * A flag indicating if the whiteboard was locked or unlocked.
	 */
	private boolean lock;

	/**
	 * The user name of the owner of the lock.
	 */
	private String owner;

	/**
	 * Constructs a new lock frame with the specified flag and owner.
	 * 
	 * @param lock <code>true</code> if it was locked, <code>false</code> if it
	 *            was unlocked.
	 * @param owner The username of the owner of the lock
	 */
	public LockFrame(boolean lock, String owner) {
		this.id = Frame.ID_LOCK;
		this.lock = lock;
		this.owner = owner;
	}

	/**
	 * @return The lock
	 */
	public boolean getLock() {
		return lock;
	}

	/**
	 * @return The owner
	 */
	public String getOwner() {
		return owner;
	}

}
