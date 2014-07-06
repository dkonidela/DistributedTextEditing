import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni. The Editor GUI class is to
 *         display the GUI for each document.
 * */
public class EditorGUI {
	// An handle of the SmartFrame class
	public SmartFrame editorFrame;

	private String docName;
	private int caretPos;
	private File savedFile;
	private Document currentDocument;
	private Client client;

	public int inc_times;
	public int dec_times;

	/**
	 * Network test
	 * */

	/**
	 * Constructor with NO network access
	 * 
	 * @throws UnknownHostException
	 * */
	public EditorGUI(String docName, Client client) throws UnknownHostException {
		this.client = null;
		this.docName = docName;
		this.client = client;
		this.editorFrame = new SmartFrame(docName);
		this.menuController();
		this.textAreaController();
		this.editorFrame.frame.setVisible(true);

	}

	/**
	 * Constructor with network access
	 * 
	 * @throws UnknownHostException
	 * */
	public EditorGUI(String docName, Document document, Client client)
			throws UnknownHostException {
		this(docName, client);
		this.currentDocument = document;

	}

	/**
	 * Listener to control the menu bar and the the window close
	 * */
	private void menuController() {

		/**
		 * Sync to server menu item listener
		 * */
		ActionListener syncServer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Put your function there
				/**
				 * Save one copy to the server
				 * */
				try {
					// System.out.println("Inside Synchronize action");
					client.synchronize(docName, editorFrame.textArea.getText());
				} catch (Exception ex) {
					System.out.println("Sync to server failed...");
				}

				/**
				 * Save one copy to local machine
				 * */
				textContentToFile();

			}
		};

		/**
		 * SAVE to local menu item listener
		 * */
		ActionListener saveLocal = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Put your function there
				/**
				 * Save the content in the text area to a file
				 * */
				textContentToFile();
			}
		};

		ActionListener closeDoc = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Put your function there
				/**
				 * The same thing will happen as you click CLOSE on window
				 * */
				System.out.println("Inside the close menu");
				client.closeDocument(docName);
				editorFrame.frame.setVisible(false);

			}
		};

		// The node leave the system
		ActionListener leaveSys = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				client.leaveFromServer();
			}
		};

		/**
		 * CLOSE the window
		 * */
		WindowAdapter closeWindow = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Put your code there when you close the window
				// System.out.println("Windows is closing!!! Add your code here!");
				/**
				 * The action when click CLOSE
				 * */
				// System.out.println("Inside the close menu");
				client.closeDocument(docName);
				editorFrame.frame.setVisible(false);

			}
		};

		this.editorFrame.serverSync(syncServer);
		this.editorFrame.saveLocal(saveLocal);
		this.editorFrame.closeDoc(closeDoc);
		this.editorFrame.leaveSys(leaveSys);
		this.editorFrame.closeWindow(closeWindow);

	}

	private void textAreaController() {
		/**
		 * Need to use the function implement the content sync with each other
		 * */
		this.editorFrame.textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {

				if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					dec_times++;
				} else {
					inc_times++;
				}

				int keyword = e.getKeyChar();
				client.sendTextToPeers(keyword, caretPos, docName);

			}
		});

		/**
		 * Show the line number and column number to the Label bar
		 * */
		this.editorFrame.textArea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				JTextArea editArea = (JTextArea) e.getSource();

				int lineNum = 1;
				int columnNum = 1;

				try {
					caretPos = editArea.getCaretPosition();
					lineNum = editArea.getLineOfOffset(caretPos) + 1;
					columnNum = caretPos
							- editArea.getLineStartOffset(lineNum - 1);
				} catch (Exception ex) {
					System.out.println("Exception");
				}

				editorFrame.status.setText("Line: " + lineNum + "    Column: "
						+ columnNum);
			}
		});
	}

	/**
	 * Save the file to local disk somewhere
	 * */
	private void textContentToFile() {
		if (this.savedFile == null) {
			this.savedFile = new File(SystemPara.WIN_LOCAL_PATH, this.docName
					+ ".txt");

		}

		try {
			PrintWriter log = new PrintWriter(new FileWriter(this.savedFile));
			log.println(this.editorFrame.textArea.getText());
			log.close();
		} catch (Exception e) {
			System.out.println("Maybe text is empty.");
		}
	}

	/**
	 * Check the file is exist
	 * */
	public static boolean isFileExist(String docName) {
		if (new File(SystemPara.WIN_LOCAL_PATH, docName + ".txt").exists())
			return true;
		else
			return false;
	}

	/**
	 * Read text from exist file
	 * */
	public static String getFileContent(String docName) {
		File textFile = new File(SystemPara.WIN_LOCAL_PATH, docName + ".txt");
		StringBuilder stringBuf = new StringBuilder();

		try {
			BufferedReader bufr = new BufferedReader(new FileReader(textFile));
			String line = null;
			while ((line = bufr.readLine()) != null) {
				stringBuf.append(line + "\n");
			}
			bufr.close();
		} catch (Exception e) {
			System.out.println("Error happend...");
		}

		return stringBuf.toString();
	}

}
