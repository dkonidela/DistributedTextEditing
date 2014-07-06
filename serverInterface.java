import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. Server Interface which
 *         contains all the server Remote Methods.
 * */
public interface serverInterface extends Remote {

	/*
	 * Client/Peers are added to the Nodelist.
	 */
	public String connect(String IpAddress) throws RemoteException,
			NotBoundException;

	/*
	 * Returns the Documents at server to peers.
	 */
	public HashMap<String, Document> viewdocument() throws RemoteException,
			NotBoundException;

	/*
	 * Documents are created at the location specified in SystemPara Class.
	 */
	public void createDocument(String docName, String IpAddress)
			throws RemoteException, NotBoundException;

	/*
	 * A new peer is joined to already existing document.
	 */
	public Document joinToDocument(String docNametoJoin, String IpAddress)
			throws RemoteException, NotBoundException;

	/*
	 * Document saved at server.
	 */
	public void synchronize(String docName, String textArea, String SavedbyIp)
			throws RemoteException, NotBoundException;

	/*
	 * New Peers updated to document.
	 */
	public void updateDocuments(ArrayList<String> nodelist,
			HashMap<String, Document> hashmap) throws RemoteException,
			NotBoundException;

	/*
	 * Removes the peer from current Users of the document.
	 */
	public void closeDocument(String docName, String LOCALHOST_IP)
			throws RemoteException, NotBoundException;

	/*
	 * Disconnects the peer from Server
	 */
	public void disconnectUser(String IpAddress) throws RemoteException,
			NotBoundException;

	/*
	 * Sends the content of existing file to requested peer.
	 */
	public String openFile(String fileName, String Ip) throws RemoteException,
			NotBoundException;
}
