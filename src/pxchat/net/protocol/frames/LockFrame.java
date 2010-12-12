package pxchat.net.protocol.frames;

public class LockFrame extends Frame {

	private boolean lock;
	private String owner;
	
	public LockFrame(boolean lock, String owner) {
		this.id = Frame.ID_LOCK;
		this.lock = lock;
		this.owner = owner;
	}
	
	public boolean getLock() {
		return lock;
	}
	
	public String getOwner() {
		return owner;
	}

}
