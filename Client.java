import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. Client Class which interacts
 *         with server and other Peers.
 * */
public class Client extends UnicastRemoteObject implements clientInterface,
		Serializable {

	public SyncManager syncManager; // reference for the SyncManager object.
	// Map of Document Name and respective EditorGUI instance.
	public HashMap<String, EditorGUI> EditorforDocument = new HashMap<String, EditorGUI>();
	// Map of Document Name and respective Document object
	HashMap<String, Document> DocumentAtClient = new HashMap<String, Document>();
	// Store the documents from server
	HashMap<String, Document> viewDocumentsFromServer = new HashMap<String, Document>();
	// reference for the EditorGUI Objects
	public EditorGUI newGUI;
	// Hold the IP of Localhost.
	public String LOCALHOST_IP;
	// Thread reference to start the SyncManager Thread.
	Thread t1;

	/*
	 * @Client Constructor
	 */
	protected Client() throws RemoteException {
		super();
		try {
			String HOST_IP = InetAddress.getLocalHost().getHostAddress();
			this.LOCALHOST_IP = HOST_IP;

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	serverInterface serverobj; // serverobject reference to call server Remote
								// Methods.
	Registry registry; // Variable to hold Registry.

	/*
	 * @main method that creates a peer object and calls all other peer methods.
	 */
	public static void main(String[] args) throws RemoteException,
			NotBoundException {

		boolean isConnectedToServer = false;
		Client clientObj = new Client();
		String command[];

		Scanner sc = new Scanner(System.in);
		// Registry created at port 6500.
		Registry clientregistry = LocateRegistry.createRegistry(6500);
		clientregistry.rebind("client", clientObj);

		while (true) {
			try {
				System.out.println("Enter the command");
				String line = sc.nextLine();
				command = line.split(" ");
				// connect command.
				if (command[0].equals("connect")) {
					try {
						System.out.println("Enter the Server IP");
						String serverIP = sc.next();
						if (clientObj.connectServer(serverIP).equals(
								"connected")) {
							isConnectedToServer = true;
							System.out.println("Connected to the Server:-");
						}
					} catch (UnknownHostException e) {
						System.out.println("Please check the Ip address");
					}
				}// view command
				else if (isConnectedToServer == true
						&& command[0].equals("view")) {
					clientObj.view(clientObj);
				}// Create document
				else if (isConnectedToServer == true
						&& command[0].equals("create")) {
					System.out.println("Enter the document name");
					String docName = sc.next();
					clientObj.create(docName, clientObj);

				} // Join Document.
				else if (isConnectedToServer == true
						&& command[0].equals("join")) {

					if (command.length == 1) {
						System.out
								.println("Please enter the document name to Join");
						String docNametoJoin = sc.nextLine();

						clientObj.join(docNametoJoin, clientObj);
					} else if (command.length == 2) {
						String docNametoJoin = command[1];
						clientObj.join(docNametoJoin, clientObj);
					} else {
						System.out.println("Wrong command");
						System.out
								.println("Join command format: join <filename without .txt>");
					}

				} // Open existing document.
				else if (isConnectedToServer == true
						&& command[0].equals("open")) {

					if (command.length == 1) {
						System.out
								.println("Please give me the document Name to Open");
						String fileName = sc.nextLine();
						clientObj.openFileFromServer(fileName, clientObj);
					} else if (command.length == 2) {

						String fileName = command[1];
						clientObj.openFileFromServer(fileName, clientObj);
					} else {
						System.out
								.println("Open command format: open <filename without .txt");
					}
				}
				// Get disconnected from the server
				else if (isConnectedToServer == true
						&& command[0].equals("leave")) {
					clientObj.leaveFromServer();
					isConnectedToServer = false;
					System.exit(0);
				}

				else if (!isConnectedToServer) {
					System.out.println("Not Connected to the Server");
				}

			}

			catch (NotBoundException e) {
				// TODO: handle exception
				System.out.println("Retry to connect Connect to the server.");
				// e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO: handle exception
				System.out.println("Please check the given IP address");
				// e.printStackTrace();
			} catch (ConnectException e) {
				System.out
						.println("Connection not established with remote host");
			}
		}

	}

	/*
	 * Open the document when no one is currently using it. Other peers can join
	 * once the document is opened.
	 */
	private void openFileFromServer(String docName, Client clientObj) {
		// TODO Auto-generated method stub
		try {
			String text = serverobj.openFile(docName, LOCALHOST_IP);
			// for the new client/peer who needs to open the document.
			if (!this.DocumentAtClient.containsKey(docName)) {
				EditorGUI opengui;
				try {
					viewDocumentsFromServer = serverobj.viewdocument();

					this.DocumentAtClient.put(docName,
							viewDocumentsFromServer.get(docName));
					if (!DocumentAtClient.get(docName).currentUsers
							.contains(LOCALHOST_IP)) {
						DocumentAtClient.get(docName).currentUsers
								.add(LOCALHOST_IP);
					}
					opengui = new EditorGUI(docName,
							viewDocumentsFromServer.get(docName), clientObj);
					this.EditorforDocument.put(docName, opengui);
					this.EditorforDocument.get(docName).editorFrame.textArea
							.setText(text);
					this.displayCurrentPeers(docName,
							this.DocumentAtClient.get(docName).currentUsers);

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}// for the client who already have the document.
			else if (!this.EditorforDocument.containsKey(docName)) {

				this.DocumentAtClient.put(docName,
						viewDocumentsFromServer.get(docName));
				if (!DocumentAtClient.get(docName).currentUsers
						.contains(LOCALHOST_IP)) {
					DocumentAtClient.get(docName).currentUsers
							.add(LOCALHOST_IP);

					EditorGUI opengui;
					try {

						opengui = new EditorGUI(docName,
								this.DocumentAtClient.get(docName), clientObj);
						this.EditorforDocument.put(docName, opengui);
						this.EditorforDocument.get(docName).editorFrame.textArea
								.setText(text);
						this.displayCurrentPeers(docName,
								this.DocumentAtClient.get(docName).currentUsers);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block

						e.printStackTrace();
					}

				}
			} else {
				System.out.println("GUI for the same file is already open");
			}

		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotBoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	/*
	 * @noParams. Get Disconnected with the Sever
	 */
	public void leaveFromServer() {
		// TODO Auto-generated method stub
		try {
			serverobj.disconnectUser(LOCALHOST_IP);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Get connected to the Server
	 * 
	 * @see clientInterface#connectServer(java.lang.String)
	 */
	public String connectServer(String serverIP) throws RemoteException,
			NotBoundException, UnknownHostException {
		registry = LocateRegistry.getRegistry(serverIP, 7000);
		serverobj = (serverInterface) registry.lookup("server");
		return serverobj.connect(LOCALHOST_IP);
	}

	/*
	 * view all the documents in the network/Server
	 * 
	 * @see clientInterface#view(Client)
	 */
	public void view(Client clientObj) throws RemoteException,
			NotBoundException {
		viewDocumentsFromServer = serverobj.viewdocument();
		if (viewDocumentsFromServer.isEmpty()) {
			System.out.println("-----------------------------------------");
			System.out.println("No documents at server");
			System.out.println("-----------------------------------------");
		} else {
			Collection<Document> keySet = viewDocumentsFromServer.values();
			Iterator<Document> itr = keySet.iterator();
			System.out
					.println("-----------------------------------------------------------");
			System.out
					.println("Document Name\tDocCreator\tLastSavedBy\tIscurrentlyinuse");
			while (itr.hasNext()) {
				Document doc = itr.next();
				System.out.print(doc.fileName);
				System.out.print("\t");
				System.out.print(doc.fileCreatorIp);
				System.out.print("\t");
				System.out.print(doc.lastSavedIp);
				System.out.print("\t");
				System.out.println(!doc.currentUsers.isEmpty());
				System.out
						.println("----------------------------------------------------------");
			}

		}
	}

	/*
	 * create(java.lang.String, Client). Create document at the server and peer.
	 */
	public void create(String docName1, Client clientObj)
			throws UnknownHostException, RemoteException, NotBoundException {

		boolean isfileCreated = false;
		Document doc = null;

		HashMap<String, Document> hashMap = serverobj.viewdocument();

		// Creating the 1st document of the system
		if (hashMap.isEmpty()) {

			serverobj.createDocument(docName1, LOCALHOST_IP);
			String docName = docName1 + ".txt";
			File file = new File(SystemPara.WIN_LOCAL_PATH, docName);
			try {
				isfileCreated = file.createNewFile();

			} catch (IOException e) {
				// e.printStackTrace();
				System.out
						.println("File not created at client side. Check the given FilePath");
			}
			if (isfileCreated) {
				doc = new Document(file, docName, LOCALHOST_IP);
				doc.setLastSavedIp(LOCALHOST_IP);
				doc.setCurrentUsers(LOCALHOST_IP);
				DocumentAtClient.put(docName1, doc);
				this.EditorforDocument.put(docName1, new EditorGUI(docName1,
						doc, clientObj));
				this.displayCurrentPeers(docName1,
						this.DocumentAtClient.get(docName1).currentUsers);
			} else {
				System.out
						.println("File not created at client side. Check the given FilePath");
			}

		}

		// If there is already a document with the specified Name.
		else {
			if (hashMap.containsKey(docName1)) {
				System.out.println("Document with this name already exists");
				System.out.println("Document Created by"
						+ hashMap.get(docName1).fileCreatorIp);

			}
			// Creating new documents after the first document.
			else {

				serverobj.createDocument(docName1, LOCALHOST_IP);
				String docName = docName1 + ".txt";
				File file = new File(docName);
				try {
					isfileCreated = file.createNewFile();
					System.out.println(isfileCreated);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("File not created at client side");
				}
				if (isfileCreated) {
					doc = new Document(file, docName, LOCALHOST_IP);
					doc.setLastSavedIp(LOCALHOST_IP);
					doc.setCurrentUsers(LOCALHOST_IP);
					DocumentAtClient.put(docName1, doc);
				} else {
					System.out.println("File Not created at client side");
				}
				this.EditorforDocument.put(docName1, new EditorGUI(docName1,
						doc, clientObj));
				this.displayCurrentPeers(docName1,
						this.DocumentAtClient.get(docName1).currentUsers);

			}
		}
	}

	/*
	 * Peer joins to already opened document.
	 * 
	 * @see clientInterface#join(java.lang.String, Client)
	 */
	public void join(String docNametoJoin, Client clientObj)
			throws RemoteException, NotBoundException {
		// getting the current Users of the document
		Document docToJoin = serverobj.joinToDocument(docNametoJoin,
				LOCALHOST_IP);

		// joining to the document
		if (docToJoin != null) {
			try {
				ArrayList<String> currentUsers = docToJoin.getCurrentUsers();
				for (int i = 0; i < currentUsers.size(); i++) {
					if (!currentUsers.get(i).equals(LOCALHOST_IP)) {

						Registry reg = LocateRegistry.getRegistry(
								currentUsers.get(i), 6500);
						clientInterface obj = (clientInterface) reg
								.lookup("client");
						obj.updateNewPeer(docNametoJoin, LOCALHOST_IP);
					}
				}

				this.EditorforDocument.put(docNametoJoin, new EditorGUI(
						docNametoJoin, docToJoin, clientObj));
				this.DocumentAtClient.put(docNametoJoin, docToJoin);

				this.displayCurrentPeers(docNametoJoin,
						this.DocumentAtClient.get(docNametoJoin).currentUsers);

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No documents with that Name");
		}

		String docCreator = this.DocumentAtClient.get(docNametoJoin).fileCreatorIp;

		// getting connected to co-ordinator if localhost is not doc creator and
		// doc creator is not alive.
		if (!this.DocumentAtClient.get(docNametoJoin).currentUsers
				.contains(docCreator)) {

			if (!this.DocumentAtClient.get(docNametoJoin).currentUsers
					.isEmpty()) {

				String Ip = this.DocumentAtClient.get(docNametoJoin).currentUsers
						.get(0);
				if (!Ip.equals(LOCALHOST_IP)) {

					this.syncManager = new SyncManager(clientObj,
							docNametoJoin, docToJoin, Ip);
					t1 = new Thread(syncManager);
					t1.start();
				} else if (!this.DocumentAtClient.get(docNametoJoin).currentUsers
						.get(1).equals(null)) {

					Ip = this.DocumentAtClient.get(docNametoJoin).currentUsers
							.get(1);

					this.syncManager = new SyncManager(clientObj,
							docNametoJoin, docToJoin, Ip);
					// starting synManager thread which gets synchronized with
					// co-ordinator for every 15secs.
					t1 = new Thread(syncManager);
					t1.start();
				}
			}
		}

		// getting connected to co-ordinator if localhost is not doc creator
		// and doc creator is still alive.
		else if (!this.DocumentAtClient.get(docNametoJoin).fileCreatorIp
				.equals(LOCALHOST_IP)) {
			System.out.println("Document creator alive"
					+ docToJoin.fileCreatorIp);
			this.syncManager = new SyncManager(this, docNametoJoin, docToJoin,
					docToJoin.fileCreatorIp);
			// starting synManager thread which gets synchronized with
			// co-ordinator for every 15secs.
			t1 = new Thread(syncManager);
			t1.start();
		}

		// getting connected to co-ordinator if localhost is doc creator

		else if (this.DocumentAtClient.get(docNametoJoin).fileCreatorIp
				.equals(LOCALHOST_IP)) {
			String Ip = this.DocumentAtClient.get(docNametoJoin).currentUsers
					.get(0);
			System.out
					.println("this is joining of creator again and get synchronized."
							+ Ip);
			this.syncManager = new SyncManager(clientObj, docNametoJoin,
					docToJoin, Ip);
			// starting synManager thread which gets synchronized with
			// co-ordinator for every 15secs.
			t1 = new Thread(syncManager);
			t1.start();
		}

	}

	/*
	 * Display the currents Users of document on the Editor infobox of
	 * respective document.
	 */
	private void displayCurrentPeers(String docNametoJoin,
			ArrayList<String> currentUsers) {
		// TODO Auto-generated method stub

		String Ip = "";
		String text = "<html>Peer Info List<br>" + "Current Users:-<br>";
		for (int i = 0; i < currentUsers.size(); i++) {
			Ip = Ip + currentUsers.get(i) + "<br>";
		}
		text = text + Ip + "</html>";
		this.EditorforDocument.get(docNametoJoin).editorFrame.infoBoard
				.setText(text);
	}

	/*
	 * Send Document to server. Back up copy at server.
	 */
	public void synchronize(String docName, String textArea)
			throws RemoteException, NotBoundException, UnknownHostException {
		serverobj.synchronize(docName, textArea, LOCALHOST_IP);
	}

	/*
	 * Each character typed at localhost is sent to other peers who are
	 * currently using the document.
	 */
	public void sendTextToPeers(int keyword, int caretPos, String docName) {

		ArrayList<String> UserstoSendKeyPressed = DocumentAtClient.get(docName).currentUsers;

		if (!UserstoSendKeyPressed.isEmpty()) {
			for (int i = 0; i < UserstoSendKeyPressed.size(); i++) {
				try {
					if (!UserstoSendKeyPressed.get(i).equals(LOCALHOST_IP)) {

						Registry reg = LocateRegistry.getRegistry(
								UserstoSendKeyPressed.get(i), 6500);
						clientInterface obj = (clientInterface) reg
								.lookup("client");
						obj.getText(keyword, caretPos, docName);
					}

				}

				catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotBoundException e2) {
					// e2.printStackTrace();
					System.out.println("In the sendTextToPeers");
					System.out.println("Connection refused to"
							+ UserstoSendKeyPressed.get(i));
				}

			}
		} else {
			System.out.println("No current Users at the document");
		}

	}

	/*
	 * A new Peer is synchronized to the already existing document. Only if the
	 * document is open.
	 */
	public void syncPeerText(String docName, Document currentDocument) {

		ArrayList<String> currentUsers = this.DocumentAtClient.get(docName).currentUsers;
		Registry peerRegistry;
		String textArea = null;
		clientInterface clientRemoteObject = null;
		try {
			for (int i = 0; i < currentUsers.size(); i++) {

				if (!currentUsers.get(i).equals(LOCALHOST_IP)) {
					peerRegistry = LocateRegistry.getRegistry(
							(String) currentUsers.get(i), 6500);
					clientRemoteObject = (clientInterface) peerRegistry
							.lookup("client");
					textArea = clientRemoteObject.getTextArea(docName,
							LOCALHOST_IP);
					break;
				}
			}

			this.EditorforDocument.get(docName).editorFrame.textArea
					.setText(textArea);
			clientRemoteObject.enableEditorAtPeers(docName);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotBoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	/*
	 * When called by remote peers, the current textArea is sent to them. All
	 * other peers uses the text from this method to get synchronized.
	 * 
	 * @see clientInterface#getTextArea(java.lang.String, java.lang.String)
	 */
	public String getTextArea(String docName, String sendToThisIp)
			throws RemoteException, NotBoundException {

		String textArea = "";
		if (this.DocumentAtClient != null && this.EditorforDocument != null) {

			ArrayList<String> currentUsers = this.DocumentAtClient.get(docName).currentUsers;
			if (!currentUsers.contains(sendToThisIp)) {
				this.DocumentAtClient.get(docName).currentUsers
						.add(sendToThisIp);
			}
			textArea = EditorforDocument.get(docName).editorFrame.textArea
					.getText();
		}
		return textArea;

	}

	/*
	 * Remote Method. Always called by other peers to set the typed text in the
	 * other Peer. SyncManager also uses this method to get synchronized.
	 * 
	 * @see clientInterface#getText(int, int, java.lang.String)
	 */

	@Override
	public synchronized void getText(int keyword, int caretpos, String docName)
			throws RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		try {
			int currentcareat = this.EditorforDocument.get(docName).editorFrame.textArea
					.getCaretPosition();
			// Setting the text if the user types a backspace/delete
			if (keyword == 8) {

				if (caretpos < currentcareat) {
					currentcareat = currentcareat
							- Character.toString((char) keyword).length();
				}
				String textArea = this.EditorforDocument.get(docName).editorFrame.textArea
						.getText();

				// Using substring to skip the deleted character.
				if (textArea.length() >= 0) {
					String subText = textArea.substring(0, caretpos)
							+ textArea.substring(caretpos + 1,
									textArea.length());
					this.EditorforDocument.get(docName).editorFrame.textArea
							.setText(subText);

					// replacing the Cursor to previous position after setting
					// the
					// text
					this.EditorforDocument.get(docName).editorFrame.textArea
							.setCaretPosition(currentcareat);
				}

			}

			// Setting the text if the user types any alphabets
			else {

				if (caretpos < currentcareat) {
					currentcareat = currentcareat
							+ Character.toString((char) keyword).length();
				}
				// inserting the text to Editor frame textArea.
				if (caretpos >= 0) {
					this.EditorforDocument.get(docName).editorFrame.textArea
							.insert(Character.toString((char) keyword),
									caretpos);
				}
				// replacing the Cursor to previous position after setting the
				// text
				this.EditorforDocument.get(docName).editorFrame.textArea
						.setCaretPosition(currentcareat);
			}
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("");
		} catch (IllegalArgumentException e) {
			System.out.println("");
		}

	}

	/*
	 * USed for Enabling and disabling the Editors.
	 * 
	 * @see clientInterface#enableEditorAtPeers(java.lang.String)
	 */
	@Override
	public void enableEditorAtPeers(String docName) throws RemoteException,
			NotBoundException {
		// TODO Auto-generated method stub
		this.EditorforDocument.get(docName).editorFrame.textArea
				.setEnabled(true);
		// this.newGUI.editorFrame.textArea.setEnabled(true);

	}

	/*
	 * calls the server close document method and remove user methods in other
	 * peers asking to remove the IP from current Users.
	 */
	public void closeDocument(String docName) {

		ArrayList<String> currentUsers = null;
		// asking sever to remove my ip address from currentusers
		if (this.DocumentAtClient.get(docName).currentUsers
				.contains(LOCALHOST_IP)
				&& this.EditorforDocument.containsKey(docName)) {

			try {
				System.out.println("Asking server to remove" + LOCALHOST_IP);
				serverobj.closeDocument(docName, LOCALHOST_IP);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentUsers = this.DocumentAtClient.get(docName).currentUsers;
		}
		// asking other peers to remove my IP from currents users of document
		// and calling removeUserFromDoc method(remote method of peers).
		if (!currentUsers.isEmpty()) {
			for (int i = 0; i < currentUsers.size(); i++) {
				if (!currentUsers.get(i).equals(LOCALHOST_IP)) {
					System.out.println("Asking to remove" + LOCALHOST_IP
							+ "from current users of " + currentUsers.get(i));
					Registry reg;
					try {
						reg = LocateRegistry.getRegistry(currentUsers.get(i),
								6500);
						clientInterface obj = (clientInterface) reg
								.lookup("client");
						obj.removeUserFromDoc(docName, LOCALHOST_IP);

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		if (this.syncManager != null) {
			this.syncManager.flag = false;
		}

		this.DocumentAtClient.get(docName).currentUsers.remove(LOCALHOST_IP);
		this.EditorforDocument.remove(docName);
	}

	/*
	 * Removes the IPaddress from the currentUsers list in the document
	 * 'docName' and displays it to the Editor if the document is open.
	 * 
	 * @see clientInterface#removeUserFromDoc(java.lang.String,
	 * java.lang.String)
	 */
	public void removeUserFromDoc(String docName, String IpAddress) {

		System.out
				.println("inside remove user from document method and ip to remove"
						+ IpAddress);
		System.out.println(t1);

		if (t1 != null) {

			if (t1.isAlive()) {
				this.syncManager.flag = false;
			}
			Document doc = this.DocumentAtClient.get(docName);
			this.EditorforDocument.get(docName).editorFrame.textArea
					.setEnabled(false);
			doc.currentUsers.remove(IpAddress);
			this.EditorforDocument.get(docName).editorFrame.textArea
					.setEnabled(true);
			this.displayCurrentPeers(docName,
					this.DocumentAtClient.get(docName).currentUsers);
			if (!this.DocumentAtClient.get(docName).currentUsers.get(0).equals(
					LOCALHOST_IP)) {
				this.findNewPeertoSynchronize(docName);
			}

		} else {
			Document doc = this.DocumentAtClient.get(docName);
			this.EditorforDocument.get(docName).editorFrame.textArea
					.setEnabled(false);

			doc.currentUsers.remove(IpAddress);
			this.displayCurrentPeers(docName,
					this.DocumentAtClient.get(docName).currentUsers);
			this.EditorforDocument.get(docName).editorFrame.textArea
					.setEnabled(true);
		}
	}

	/*
	 * finding new co-ordinator to get synchronized frequently
	 */
	private void findNewPeertoSynchronize(String docNametoJoin) {
		// TODO Auto-generated method stub
		try {
			Document docToJoin = this.DocumentAtClient.get(docNametoJoin);
			String Ip = this.DocumentAtClient.get(docNametoJoin).currentUsers
					.get(0);
			System.out.println("IP here is(he will be new coordinator) " + Ip);
			t1 = new Thread(
					new SyncManager(this, docNametoJoin, docToJoin, Ip),
					"synThread");
			t1.start();
		} catch (Exception e) {
			System.out.println("Cating the exception.");
		}
	}

	/*
	 * A new peer is added to the currentUsers of document.
	 * 
	 * @see clientInterface#updateNewPeer(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateNewPeer(String docNametoJoin, String Ip)
			throws RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		System.out.println(docNametoJoin);
		if (!this.DocumentAtClient.get(docNametoJoin).currentUsers.contains(Ip)) {
			this.DocumentAtClient.get(docNametoJoin).currentUsers.add(Ip);
			this.displayCurrentPeers(docNametoJoin,
					this.DocumentAtClient.get(docNametoJoin).currentUsers);

		}
	}

}
