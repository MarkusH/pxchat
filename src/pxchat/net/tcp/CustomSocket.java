package pxchat.net.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class CustomSocket {
	
	private Socket socket;
	private ReadThread readThread = null;

	private boolean closing = false;
	protected boolean connected = false;
	
	private Cipher cipherIn;
	private Cipher cipherOut;
	private CipherInputStream cIn;
	private CipherOutputStream cout;
	
	
	public synchronized void close() throws IOException {
		if (socket != null) {
			if (this.connected) {
				this.closing = true;

				if (readThread != null) {
					readThread.interrupt();
					readThread = null;
				}
				socket.close();

				Thread.yield();
			}
		}
	}
	
	public synchronized boolean writeObject(Object object) throws IOException {
		if (isConnected()) {
			if (cout == null)
				cout = new CipherOutputStream(socket.getOutputStream(), cipherOut);
			ObjectOutputStream stream = new ObjectOutputStream(cout);
			stream.writeObject(object);
//			stream.flush();

			return true;
		}
		return false;
	}

	protected synchronized void doRead() {
		if (isConnected()) {
			readThread = new ReadThread();
			readThread.start();
		}
	}
	
	private synchronized void readCallback(Object object) {
//		clientCallbacks.clientRead(this, object);
		doRead();
	}

	private synchronized void readCallback(Exception e) {
		if (this.closing || e instanceof EOFException || e instanceof SocketException) {
			this.closing = false;
			this.connected = false;
//			clientCallbacks.clientDisconnect(this);
		} else {
		}
	}

	class ReadThread extends Thread {

		Object object = null;

		public ReadThread() {
			this.setDaemon(true);
		}

		@Override
		public void run() {
			try {
				if (cIn == null)
					cIn = new CipherInputStream(socket.getInputStream(), cipherIn);
				ObjectInputStream stream = new ObjectInputStream(cIn);
				object = stream.readObject();
			} catch (Exception e) {
				readCallback(e);
				return;
			}
			readCallback(object);
		}
	}
	
	public boolean isConnected() {
		return ((socket != null) && this.connected);
	}

	public String getRemoteAddress() {
		return socket != null ? socket.getRemoteSocketAddress().toString() : "";
	}
}
