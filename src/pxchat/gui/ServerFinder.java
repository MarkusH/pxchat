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
				int adrIndex = line.indexOf(" ");
				String remote = "";
				if (adrIndex > -1) {
					remote = line.substring(0, adrIndex);
				}
				int nmIndex = line.indexOf(" ", adrIndex + 1);
				String name = "";
				if (nmIndex > -1) {
					name = line.substring(adrIndex + 1, nmIndex);
				}
				ServerEntry e = new ServerEntry(name, remote);
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
	
//	ServerEntry[] data = new ServerEntry[] {
//			new ServerEntry("First", "25.156.13.44"),
//			new ServerEntry("Second", "57.6.53.68")
//	};
	
	Vector<ServerEntry> data = new Vector<ServerEntry>();
	
	String[] columns = new String[] { "Name", "Address" };
	
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
		return columnIndex == 0 ? data.get(rowIndex).getName() : data.get(rowIndex).getRemoteAddress();
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

	public ServerEntry(String name, String remoteAddress) {
		this.name = name;
		this.remoteAddress = remoteAddress;
	}

	public String toString() {
		return "[" + name + "@" + remoteAddress + "]";
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
		return that.name.equals(this.name) && that.remoteAddress.equals(this.remoteAddress);
	}
}
