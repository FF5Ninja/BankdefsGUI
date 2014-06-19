package cnuphys.bCNU.treegui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * @author Brandon Rusk -  A simple GUI used to iterate through XML files and display within a JTree.
 * Can also detect syntax errors within the .xml, as well as find duplicates.
 */

@SuppressWarnings("serial")
public class SAXJTreeStart extends SAXJTree implements ActionListener {

	JPanel mainPanel = new JPanel();

	/** User must input a FILE directory for "Find File" button
	 * I.E.
	 * C:\Users\Brandon\Documents\Jlab\Bankdefs\FTOF.xml
	 * 
	 * Displays the file as a JTree
	 */
	private static JButton button;
	JTextField field = new JTextField("", 16);

	/** User must input a FOLDER directory for "Scan Folder" button
	* I.E.
	* C:\Users\Brandon\Documents\Jlab\Bankdefs
	* 
	* Scans through the folder of .xml files and detects duplicats tags/nums as well as detecting syntax errors.
	*/
	private static JButton button1;
	JComponent[] allComp = {
			new JLabel("Enter the directory of a file or folder"), field,
			button, button1 };

	public SAXJTreeStart(final JFrame frame) {

		for (JComponent comp : allComp) {
			mainPanel.add(comp);
		}

		// actionlistener for "Find File"
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] s = { field.getText() };
				SAXJTree.main(s);

			}
		});

		// actionlistener for "Scan Folder"
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				frame.setVisible(false);
				frame.dispose();
				String[] s = { field.getText() };
				Readbankdef.main(s);

			}
		});
	}

	public JComponent getMainComponent() {
		return mainPanel;
	}

	/**
	 * Creates the buttons, JFrame, and displays the GUI.
	 */
	
	private static void createAndShowGui() {
		
		button = new JButton("Find File");
		button1 = new JButton("Scan Folder");
		frame = new JFrame();
		example = new SAXJTreeStart(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getRootPane().setDefaultButton(button);
		frame.getContentPane().add(example.getMainComponent());
		frame.pack();
		frame.setSize(250, 150);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}