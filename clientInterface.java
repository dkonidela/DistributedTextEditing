import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. Client Interface which
 *         contains all the Peer Remote Methods.
 * */
public interface clientInterface extends Remote {

	/*
	 * Get connected to the Server
	 */
	public String connectServer(String serverIP) throws RemoteException,
			NotBoundException, UnknownHostException;

	/*
	 * view all the documents in the network/Server
	 */
	public void view(Client clientObj) throws RemoteException,
			NotBoundException;

	/*
	 * Join to already opened document
	 */
	public void join(String docNametoJoin, Client clientObj)
			throws RemoteException, NotBoundException;

	/*
	 * get Text and set each Key.
	 */
	public void getText(int keyword, int caretpos, String docName)
			throws RemoteException, NotBoundException;

	/*
	 * get text and set the textArea.
	 */
	public String getTextArea(String docName, String IpAddress)
			throws RemoteException, NotBoundException;

	/*
	 * Enable the editor GUI at peers
	 */
	public void enableEditorAtPeers(String docName) throws RemoteException,
			NotBoundException;

	/*
	 * Remove User from the document
	 */
	public void removeUserFromDoc(String docName, String IpAddress)
			throws RemoteException, NotBoundException;

	/*
	 * Update New peer to the existing document.
	 */
	public void updateNewPeer(String docNametoJoin, String lOCALHOST_IP)
			throws RemoteException, NotBoundException;
}
