package pxchat.net.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	class WriteThread extends Thread {
		
		public WriteThread() {
			this.setDaemon(true);
		}

		@Override
		public void run() {
			if (cOut == null)
				try {
					cOut = new CipherOutputStream(socket.getOutputStream(), cipherOut);
				} catch (IOException e) {
					e.printStackTrace();
				}

			
			while (!outgoing.isEmpty()) {
				try {
					ObjectOutputStream stream = null;
					try {
						stream = new ObjectOutputStream(cOut);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					Object o = outgoing.poll();
					stream.writeObject(o);
					System.out.println("write " + o);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			writeThread = null;
			
			if (tcpClientListener != null)
				tcpClientListener.clientClearToSend(CustomSocket.this);
		}
	}

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

	// begin test
	private ConcurrentLinkedQueue<Object> outgoing = new ConcurrentLinkedQueue<Object>();

	private Thread writeThread = null;
	// end test

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
	private void doDisconnect() {
		if (sendDisconnectedEvent)
			return;
		
		if (tcpClientListener != null)
			tcpClientListener.clientDisconnect(this);
		
		sendDisconnectedEvent = true;
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

	public String getRemoteAddress() {
		return socket != null ? socket.getRemoteSocketAddress().toString() : "";
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

	public boolean isConnected() {
		return ((socket != null) && this.connected);
	}
	
	private synchronized void readCallback(Exception e) {
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

	private void readCallback(Object object) {
		if (tcpClientListener != null)
			tcpClientListener.clientRead(this, object);
		doRead();
	}

	/**
	 * Encrypts and write the object to the output stream of the socket.
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean writeObject(Object object) throws IOException {
		
		if (!isConnected())
			return false;
		outgoing.add(object);
		
		if (writeThread == null) {
			writeThread = new WriteThread();
			writeThread.start();
		} else {	
			synchronized (writeThread) {
				if (!writeThread.isAlive()) {
					writeThread = new WriteThread();
					writeThread.start();
				}
			}
		}
		return true;
		
//		if (isConnected()) {
//			System.out.println("write... connected");
//			if (cOut == null)
//				cOut = new CipherOutputStream(socket.getOutputStream(), cipherOut);
//			System.out.println("cipher ready");
//			ObjectOutputStream stream = new ObjectOutputStream(cOut);
//			System.out.println("out genereted");
//			stream.writeObject(object);
//			System.out.println("written");
//			// stream.flush();
//
//			return true;
//		}
//		return false;
	}
}
