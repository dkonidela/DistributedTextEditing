import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. Server Class which handles
 *         all the requests from the peers
 * */
public class Server extends UnicastRemoteObject implements serverInterface,
		Serializable {
	// nodelist stores the IPs of the registered peers.
	ArrayList<String> nodeList = new ArrayList<String>();
	// key is filename which is unique and respective document object as value
	HashMap<String, Document> hashMap = new HashMap<String, Document>();

	protected Server() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws RemoteException {
		// Server Registry Created.
		Server obj = new Server();
		Registry registry = LocateRegistry.createRegistry(7000);
		registry.rebind("server", obj);
		System.out.println("Server Started");
	}

	/*
	 * Client/Peers are added to the Nodelist.
	 * 
	 * @see serverInterface#connect(java.lang.String)
	 */
	@Override
	public String connect(String Ipaddress) {

		if (nodeList.contains(Ipaddress)) {
			return "Client already in the List";
		} else {
			nodeList.add(Ipaddress);
			System.out
					.println("New Client with IP " + Ipaddress + " connected");
			return "connected";
		}
	}

	/*
	 * Returns the Documents at server to peers
	 * 
	 * @see serverInterface#viewdocument()
	 */
	@Override
	public HashMap<String, Document> viewdocument() {

		return hashMap;
	}

	/*
	 * Documents are created at the location specified in SystemPara Class.
	 * 
	 * @see serverInterface#createDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public void createDocument(String docName, String IpAddress) {

		String docName1 = docName + ".txt";

		File file = new File(SystemPara.LINUX_SERVER_PATH, docName1);
		boolean isfileCreated = false;
		try {
			isfileCreated = file.createNewFile();
			System.out.println("Is file created " + isfileCreated);

			if (isfileCreated) {

				Document doc = new Document(file, docName1, IpAddress);
				doc.setLastSavedIp(IpAddress);
				doc.currentUsers.add(IpAddress);
				hashMap.put(docName, doc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * A new peer is joined to already existing document. CureentUsers of doc is
	 * updated.
	 * 
	 * @see serverInterface#joinToDocument(java.lang.String, java.lang.String)
	 */

	public Document joinToDocument(String docNametojoin, String IpAddress) {

		if (hashMap.containsKey(docNametojoin)) {

			System.out.println("Server contains the file");
			ArrayList<String> currentUsers = hashMap.get(docNametojoin).currentUsers;

			if (!currentUsers.contains(IpAddress)) {
				hashMap.get(docNametojoin).currentUsers.add(IpAddress);
				if (hashMap.get(docNametojoin).currentUsers.contains(IpAddress))
					System.out.println(IpAddress + "Added to the document"
							+ docNametojoin);

			}

			return hashMap.get(docNametojoin);
		} else {
			return null;
		}

	}

	// Document saved at server.
	public void synchronize(String docName, String textArea, String SavedbyIp) {
		String fileName = docName + ".txt";

		// savedFile is the new will from synchronize which replaces already
		// existing file at server
		try {
			File savedFile = new File(SystemPara.LINUX_SERVER_PATH, fileName);
			PrintWriter log = new PrintWriter(new FileWriter(savedFile));
			log.println(textArea);
			log.close();

			// System.out
			// .println("In synchronize method and printing last IP who synchronized to server");
			// System.out.println(SavedbyIp);

			if (!hashMap.containsKey(docName)) {
				hashMap.put(docName, new Document(savedFile, fileName,
						SavedbyIp));
				hashMap.get(docName).setLastSavedIp(SavedbyIp);
				if (!hashMap.get(docName).currentUsers.contains(SavedbyIp))
					hashMap.get(docName).currentUsers.add(SavedbyIp);
			} else {
				hashMap.get(docName).setLastSavedIp(SavedbyIp);
				if (!hashMap.get(docName).currentUsers.contains(SavedbyIp))
					hashMap.get(docName).currentUsers.add(SavedbyIp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Check for the Path given to save file.");
		}
	}

	// This method is written for Secondary server. Does Nothing in Main Server.
	@Override
	public void updateDocuments(ArrayList<String> nodelist,
			HashMap<String, Document> hashmap) throws RemoteException,
			NotBoundException {
		// TODO Auto-generated method stub

	}

	/*
	 * Removes the peer from current Users of the document.
	 * 
	 * @see serverInterface#closeDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public void closeDocument(String docName, String IpAddress)
			throws RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		this.hashMap.get(docName).currentUsers.remove(IpAddress);

	}

	/*
	 * Disconnects the peer from Server
	 * 
	 * @see serverInterface#disconnectUser(java.lang.String)
	 */
	@Override
	public void disconnectUser(String IpAddress) throws RemoteException,
			NotBoundException {
		// TODO Auto-generated method stub
		if (nodeList.contains(IpAddress))
			this.nodeList.remove(IpAddress);

	}

	/*
	 * Sends the content of existing file to requested peer.
	 * 
	 * @see serverInterface#openFile(java.lang.String, java.lang.String)
	 */
	@Override
	public String openFile(String fileName, String Ip) throws RemoteException,
			NotBoundException {

		String filenametxt = fileName + ".txt";
		String text = "";

		ArrayList<String> line = new ArrayList<String>();
		Scanner fileReader;
		try {
			System.out.println(filenametxt);
			fileReader = new Scanner(new File(
					"/home/stu12/s12/drk4074/Dsfinalproj/filesatserver/"
							+ filenametxt));

			while (fileReader.hasNext()) {

				String item = fileReader.nextLine();
				line.add(item);
			}
			for (int i = 0; i < line.size(); i++) {
				text = text + line.get(i) + "\n";
			}
			fileReader.close();

		} catch (FileNotFoundException e) {

			System.out.println("File Path not found");
		}

		if (!this.hashMap.get(fileName).currentUsers.contains(Ip)) {
			this.hashMap.get(fileName).currentUsers.add(Ip);
		}

		return text;

	}
}
