import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. SyncManager class is a
 *         thread runs every 15 seconds to get synchronized with Peers.
 * 
 * */

public class SyncManager implements Runnable {
	Client clientObj; // client Object Reference
	String docNametoJoin; // Document Name on which synchronization is done.
	Document docToJoin; // Document Object Reference
	String LOCALHOST_IP; // Localhost Ip Address
	String fileCreator; // Document creator IP address
	public boolean flag = false; // flag used for looping

	/*
	 * @constructor
	 */
	public SyncManager(Client clientObj, String docNametoJoin,
			Document docToJoin, String Ip) {
		// TODO Auto-generated constructor stub
		this.clientObj = clientObj;
		this.docNametoJoin = docNametoJoin;
		this.fileCreator = Ip;
		try {
			this.LOCALHOST_IP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long time = 0;
		flag = true;
		// Runs for every 15seconds to get synchronized with the co-ordinator
		while (flag) {
			this.clientObj.EditorforDocument.get(docNametoJoin).inc_times = 0;
			this.clientObj.EditorforDocument.get(docNametoJoin).dec_times = 0;

			if (!clientObj.EditorforDocument.get(docNametoJoin).equals(null)) {
				int currentcareat = clientObj.EditorforDocument
						.get(docNametoJoin).editorFrame.textArea
						.getCaretPosition();
				long sec = System.currentTimeMillis() / 15000;
				if (sec != time) {
					Registry peerRegistry;
					try {
						// Connecting with Co-ordinator. Getting his text area
						// and setting in its local Editor.
						peerRegistry = LocateRegistry.getRegistry(fileCreator,
								6500);
						clientInterface clientRemoteObject = (clientInterface) peerRegistry
								.lookup("client");
						String textArea = clientRemoteObject.getTextArea(
								docNametoJoin, LOCALHOST_IP);
						clientObj.EditorforDocument.get(docNametoJoin).editorFrame.textArea
								.setText(textArea);

						// Placing the careat(cursor) Position to the previous
						// value
						// after setting the textarea.
						clientObj.EditorforDocument.get(docNametoJoin).editorFrame.textArea
								.setCaretPosition(currentcareat
										+ this.clientObj.EditorforDocument
												.get(docNametoJoin).inc_times
										- this.clientObj.EditorforDocument
												.get(docNametoJoin).dec_times);
						time = sec;
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						System.out
								.println("Not connected to peers at SyncManager.");
					} catch (IllegalArgumentException e) {
						System.out.println("");
					} catch (Exception e) {
						// System.out
						// .println("File closed so not getting synchronized with co-ordinator.");
						this.flag = false;
					}

				}
			}
		}

	}
}
