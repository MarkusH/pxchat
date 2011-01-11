/**
 * 
 */
package pxchat.gui;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author Markus
 *
 */
public class ServerFinder extends JDialog {
	
	
	public static void main(String args[]) {
		new ServerFinder(null);
	}

	private JTable table;
	
	/**
	 * @throws IOException 
	 * 
	 */
	public ServerFinder(JFrame parent) {
		super(parent, "Server Finder");
		setLayout(new BorderLayout());

		table = new JTable();
		table.setCellSelectionEnabled(false);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setModel(new ServerTableModel(new Vector<ServerEntry>()));
		updateEntries();

		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		getContentPane().setSize(150, 200);
		this.pack();

		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(this.getParent());
		this.setVisible(true);
	}

	private void updateEntries() {
		
		Vector<ServerEntry> entries = new Vector<ServerEntry>();
		
		try {
			URL u = new URL("http://localhost/servers.php?&action=list");
			BufferedReader r;
			URLConnection c = u.openConnection();
			c.setConnectTimeout(1000);
			r = new BufferedReader(new InputStreamReader(c.getInputStream()));
	
			String line = null;
			while ((line = r.readLine()) != null) {
				
				Pattern p = Pattern.compile("([^ ]*) ([^ ]*) (.*)");
				Matcher m = p.matcher(line);
				
				System.out.println(m.group(0) + m.group(1) + m.group(2));
				
				int adrIndex = line.indexOf(" ");
				String remote = "";
				if (adrIndex > -1) {
					remote = line.substring(0, adrIndex);
				}
				int pIndex = line.indexOf(" ", adrIndex + 1);
				String port = "";
				if (pIndex > -1) {
					port = line.substring(adrIndex + 1, pIndex);
				}
				int nmIndex = line.indexOf(" ", pIndex + 1);
				String name = "";
				if (nmIndex > -1) {
					name = line.substring(pIndex + 1, nmIndex);
				}
				ServerEntry e = new ServerEntry(name, remote, port);
				entries.add(e);
			}
			
			table.setModel(new ServerTableModel(entries));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

class ServerTableModel implements TableModel {
	
	Vector<ServerEntry> data = new Vector<ServerEntry>();
	
	String[] columns = new String[] { "Name", "Address", "Port" };
	
	public ServerTableModel(Vector<ServerEntry> entries) {
		this.data = entries;
	}
	

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columns[columnIndex];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return data.get(rowIndex).getName();
			case 1:
				return data.get(rowIndex).getRemoteAddress();
			case 2:
				return data.get(rowIndex).getPort();
		}
		return "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {	
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {	
	}
}

class ServerEntry {
	private String name;
	private String remoteAddress;
	private String port;

	public ServerEntry(String name, String remoteAddress, String port) {
		this.name = name;
		this.remoteAddress = remoteAddress;
		this.port = port;
	}

	public String toString() {
		return "[" + name + "@" + remoteAddress + ":" + port + "]";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the remoteAddress
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public boolean equals(Object other) {
		if (!(other instanceof ServerEntry))
			return false;
		
		ServerEntry that = (ServerEntry) other;
		return that.name.equals(this.name) && 
			that.port.equals(this.port) &&
			that.remoteAddress.equals(this.remoteAddress);
	}
}
