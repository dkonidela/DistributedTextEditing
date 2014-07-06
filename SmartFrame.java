import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

/**
 * @author Yi Huang, Dileep Konidea, Amit Nadkarni The SmartFrame class for
 *         using the Java AWT class create a Text Editor GUI
 * */
public class SmartFrame {
	public JFrame frame;
	public JTextArea textArea;
	public JLabel status;

	public JLabel infoBoard;

	private JMenuBar bar;
	private JButton serverSync;
	private JButton saveLocal;
	private JButton close;
	private JButton leave;

	public SmartFrame(String name) {
		this.drawFrame(name);
	}

	private void drawFrame(String frameName) {
		this.frame = new JFrame(frameName);
		this.frame.setBounds(350, 100, 700, 500);

		this.bar = new JMenuBar();
		this.serverSync = new JButton("Sync to Server");
		this.saveLocal = new JButton("Save to Local");
		this.close = new JButton("Close Doc");
		this.leave = new JButton("Leave System");

		this.bar.add(serverSync);
		// this.bar.add(Box.createHorizontalGlue());
		this.bar.add(saveLocal);
		this.bar.add(close);

		this.bar.add(Box.createHorizontalGlue());
		this.bar.add(leave);

		this.frame.setJMenuBar(bar);

		this.textArea = new JTextArea();
		this.textArea.setFont(new Font("Courier New", Font.PLAIN, 20));
		// Set the line wrap automatically
		this.textArea.setLineWrap(true);
		this.frame.add(new JScrollPane(textArea));

		this.status = new JLabel();
		status.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.status.setText("Line: 1    Column: 1");
		this.frame.add(status, BorderLayout.SOUTH);

		this.infoBoard = new JLabel();
		infoBoard.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		infoBoard.setBackground(Color.WHITE);
		infoBoard.setForeground(Color.BLUE);
		infoBoard.setOpaque(true);
		infoBoard.setVerticalAlignment(JLabel.TOP);
		infoBoard.setHorizontalAlignment(JLabel.CENTER);
		this.frame.add(infoBoard, BorderLayout.EAST);

		infoBoard.setPreferredSize(new Dimension(145, 0));
		// Only support HTML format
		infoBoard.setText("<html>Peer Info List<br></html>");

		// Do nothing on the windows closing, but use the window listener
		this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	public void saveLocal(ActionListener listener) {
		saveLocal.addActionListener(listener);
	}

	public void serverSync(ActionListener listener) {
		serverSync.addActionListener(listener);
	}

	public void closeDoc(ActionListener listener) {
		close.addActionListener(listener);
	}

	public void leaveSys(ActionListener listener) {
		leave.addActionListener(listener);
	}

	public void closeWindow(WindowAdapter listener) {
		frame.addWindowListener(listener);
	}

}
