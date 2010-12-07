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
import javax.crypto.spec.SecretKeySpec;

/**
 * This class implements a socket that is able to read and write encrypted
 * objects to a TCP socket using object input and object output streams.
 * 
 * @author Markus Döllinger
 */
public class CustomSocket {

	protected Socket socket = null;
	private ReadThread readThread = null;

	private boolean closing = false;
	protected boolean connected = false;

	private Cipher cipherIn;
	private Cipher cipherOut;
	private CipherInputStream cIn;
	private CipherOutputStream cOut;

	protected TCPClientListener tcpClientListener;

	/**
	 * Constructs a new custom socket with a specified listener. No connection
	 * is established.
	 */
	protected CustomSocket(TCPClientListener tcpClientListener) {
		this.tcpClientListener = tcpClientListener;
		initCipher();
	}

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
	 * Initialized the encryption / decryption facility.
	 */
	protected void initCipher() {
		try {
			cipherOut = Cipher.getInstance("RC4");
			cipherOut.init(Cipher.DECRYPT_MODE, new SecretKeySpec("12345678".getBytes(), "RC4"));
			cipherIn = Cipher.getInstance("RC4");
			cipherIn.init(Cipher.ENCRYPT_MODE, new SecretKeySpec("12345678".getBytes(), "RC4"));
			cIn = null;
			cOut = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				if (readThread != null) {
					readThread.interrupt();
					readThread = null;
				}
				socket.close();

				Thread.yield();

				this.socket = null;
				this.connected = false;
			}
		}
	}

	/**
	 * Encrypts and write the object to the output stream of the socket.
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean writeObject(Object object) throws IOException {
		if (isConnected()) {
			if (cOut == null)
				cOut = new CipherOutputStream(socket.getOutputStream(), cipherOut);
			ObjectOutputStream stream = new ObjectOutputStream(cOut);
			stream.writeObject(object);
			// stream.flush();

			return true;
		}
		return false;
	}

	/**
	 * Starts a new thread that reads from the input stream.
	 */
	protected synchronized void doRead() {
		if (isConnected()) {
			readThread = new ReadThread();
			readThread.start();
		}
	}

	private synchronized void readCallback(Object object) {
		if (tcpClientListener != null)
			tcpClientListener.clientRead(this, object);
		doRead();
	}

	private synchronized void readCallback(Exception e) {
		if (this.closing || e instanceof EOFException || e instanceof SocketException) {
			this.closing = false;
			this.connected = false;
			if (tcpClientListener != null)
				tcpClientListener.clientDisconnect(this);
		} else {
			try {
				close();
			} catch (Exception e1) {
			}
			if (tcpClientListener != null)
				tcpClientListener.clientDisconnect(this);
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
