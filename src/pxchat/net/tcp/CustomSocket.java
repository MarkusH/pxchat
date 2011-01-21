package pxchat.net.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class implements a socket that is able to read and write encrypted
 * objects to a TCP socket using object input and object output streams.
 * 
 * @author Markus Döllinger
 */
public class CustomSocket {

	/**
	 * A thread that waits for incoming encrypted data. The data will be
	 * decrypted and passed as an object to the callback method of the custom
	 * socket.
	 * 
	 * @author Markus Döllinger
	 */
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
				readCallback(object);
			} catch (Exception e) {
				readCallback(e);
			}
		}
	}

	/**
	 * A thread that writes outgoing data. The data will be encrypted in advance.
	 * 
	 * @author Markus Döllinger
	 */
	class WriteThread extends Thread {

		public WriteThread() {
			this.setDaemon(true);
		}

		@Override
		public void run() {
			lock.lock();

			if (cOut == null) {
				try {
					cOut = new CipherOutputStream(socket.getOutputStream(), cipherOut);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			while (!outgoing.isEmpty()) {
				try {
					ObjectOutputStream stream = null;
					stream = new ObjectOutputStream(cOut);

					Object o = outgoing.poll();
					stream.writeObject(o);

				} catch (IOException e) {
					e.printStackTrace();
					outgoing.clear();
				}
			}

			writeThread = null;

			lock.unlock();

			if (tcpClientListener != null)
				tcpClientListener.clientClearToSend(CustomSocket.this);
		}
	}

	/**
	 * A lock that is used to only only start a write thread once
	 */
	private ReentrantLock lock = new ReentrantLock(true);

	protected Socket socket = null;
	private ReadThread readThread = null;
	private boolean closing = false;

	protected boolean connected = false;
	protected boolean sendDisconnectedEvent = false;
	private Cipher cipherIn;
	private Cipher cipherOut;

	private CipherInputStream cIn;

	private CipherOutputStream cOut;
	protected TCPClientListener tcpClientListener;

	private ConcurrentLinkedQueue<Object> outgoing = new ConcurrentLinkedQueue<Object>();
	private Thread writeThread = null;

	/**
	 * Gets called from a server when a client connects. Do not call this
	 * constructor yourself!
	 * 
	 * @param socket The client socket connected to the server.
	 * @param tcpClientListener The listener waiting for socket events.
	 */
	protected CustomSocket(Socket socket, TCPClientListener tcpClientListener) {
		this.socket = socket;
		this.tcpClientListener = tcpClientListener;
		this.connected = true;
		initCipher();

		doRead();
	}

	/**
	 * Constructs a new custom socket with a specified listener. No connection
	 * is established.
	 */
	protected CustomSocket(TCPClientListener tcpClientListener) {
		this.tcpClientListener = tcpClientListener;
		initCipher();
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 */
	public synchronized void close() throws IOException {
		if (socket != null) {
			if (this.connected) {
				this.closing = true;

				// try to send outgoing data for at most 10ms
				if (writeThread != null) {
					try {
						writeThread.join(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						writeThread = null;
					}
				}
				if (readThread != null) {
					readThread.interrupt();
					readThread = null;
				}
				socket.close();

				Thread.yield();

				this.socket = null;
				this.connected = false;

				doDisconnect();
			}
		}
	}

	/**
	 * Sends the disconnected event to the listener
	 */
	private synchronized void doDisconnect() {
		if (sendDisconnectedEvent)
			return;

		if (tcpClientListener != null)
			tcpClientListener.clientDisconnect(this);

		sendDisconnectedEvent = true;
	}

	/**
	 * Starts a new thread that reads from the input stream.
	 */
	protected void doRead() {
		if (isConnected()) {
			readThread = new ReadThread();
			readThread.start();
		}
	}

	/**
	 * @return The remote address this socket is connected to.
	 */
	public String getRemoteAddress() {
		return socket != null ? socket.getRemoteSocketAddress().toString() : "";
	}

	/**
	 * Initialized the encryption / decryption facility.
	 */
	protected void initCipher() {
		try {
			cipherOut = Cipher.getInstance("RC4");
			cipherOut.init(Cipher.DECRYPT_MODE, new SecretKeySpec("8C63612E8D4DB".getBytes(), "RC4"));
			cipherIn = Cipher.getInstance("RC4");
			cipherIn.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("8C63612E8D4DB".getBytes(), "RC4"));
			cIn = null;
			cOut = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return <code>true</code> if the socket is connected, <code>false</code> otherwise
	 */
	public boolean isConnected() {
		return ((socket != null) && this.connected);
	}

	/**
	 * The read callback in case of an error. The error may simply be a disconnect.
	 * 
	 * @param e The exception
	 */
	private void readCallback(Exception e) {
		if (this.closing || e instanceof EOFException || e instanceof SocketException) {
			this.closing = false;
			this.connected = false;
		} else {
			try {
				close();
			} catch (Exception e1) {
			}
		}
		doDisconnect();
	}

	/**
	 * The read callback in the case that data arrived.
	 * 
	 * @param object The incoming data
	 */
	private void readCallback(Object object) {
		if (tcpClientListener != null)
			tcpClientListener.clientRead(this, object);
		doRead();
	}

	/**
	 * Encrypts and write the object to the output stream of the socket.
	 * 
	 * @param object The data to send
	 * @return boolean returns false if the client is not connected, otherwise
	 *         true
	 * @throws IOException
	 */
	public boolean writeObject(Object object) throws IOException {

		if (!isConnected())
			return false;

		lock.lock();
		outgoing.add(object);

		if (writeThread == null) {
			writeThread = new WriteThread();
			writeThread.start();
		} else {
			// Comes here when the previous lock thread was started but did not
			// invoke
			// its run() method due to overhead
			if (!writeThread.isAlive()) {
				// Should never come here
				System.out.println("-------------------not alive-------------");
				writeThread = new WriteThread();
				writeThread.start();
			}
		}
		lock.unlock();
		return true;
	}

}
