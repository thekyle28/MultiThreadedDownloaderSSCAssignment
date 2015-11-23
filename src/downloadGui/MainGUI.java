package downloadGui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.BoxLayout;

import downloader.FileDownloader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JScrollPane;

import java.awt.Scrollbar;

import javax.swing.JScrollBar;

public class MainGUI extends JFrame {

	private JPanel contentPane;
	private JTextField urlTxt;
	private JTextField saveLocationTxt;
	private JTextField filterTxt;
	private JTextField threadsTxt;
	private static JList<String> list;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI frame = new MainGUI();
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1053, 603);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		northPanel.add(panel_1);

		JLabel lblNewLabel = new JLabel("Webpage URL");
		panel_1.add(lblNewLabel);

		urlTxt = new JTextField();
		panel_1.add(urlTxt);
		urlTxt.setColumns(30);

		JPanel panel = new JPanel();
		panel_1.add(panel);

		JLabel lblNewLabel_2 = new JLabel("Filter");
		panel.add(lblNewLabel_2);

		filterTxt = new JTextField();
		panel.add(filterTxt);
		filterTxt.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Save Location");
		panel_1.add(lblNewLabel_1);

		saveLocationTxt = new JTextField();
		panel_1.add(saveLocationTxt);
		saveLocationTxt.setColumns(30);

		JButton btnNewButton = new JButton("Browse");
		panel_1.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					saveLocationTxt.setText(chooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});

		JPanel panel_2 = new JPanel();
		northPanel.add(panel_2);

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3);



		JPanel panel_4 = new JPanel();
		panel_3.add(panel_4);

		JLabel lblNewLabel_3 = new JLabel("Number of Threads");
		panel_4.add(lblNewLabel_3);

		threadsTxt = new JTextField();
		panel_4.add(threadsTxt);
		threadsTxt.setColumns(10);
		
		//runs when the download button is clicked.
		JButton downloadButton = new JButton("Download");
		downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new DownloaderGUI(list.getSelectedValuesList()).setVisible(true);
			}
			
		});
		
		JButton getFiles = new JButton("Get Files");
		getFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (makeChecks()) {
					String url = urlTxt.getText();
					String saveLoc = saveLocationTxt.getText();
					int numThreads = Integer.parseInt(threadsTxt.getText());
					String filter = filterTxt.getText();

					FileDownloader fd = new FileDownloader(url, saveLoc,
							numThreads, filter, MainGUI.this);
					try {
						fd.download();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel_3.add(getFiles);
		panel_3.add(downloadButton);
				
						list = new JList<String>();
						contentPane.add(list, BorderLayout.CENTER);
	}
	
	/**
	 * Checks several conditions have been met before attempting to download the
	 * files from the URL.
	 * 
	 * @return whether or not all of the checks have been passed.
	 */
	private boolean makeChecks() {
		// check that the number of threads has been entered greater than 0
		// and that there are no characters.
		if (!threadsTxt.getText().equals("")
				&& threadsTxt.getText().replaceAll("[a-z]", "")
						.equals(threadsTxt.getText())) {
			return true;
		}
		System.out.println("Please check everything");
		return false;

	}
	
	public void setList(Object[] images, Object[] linkArray){
		 list.setModel(new AbstractListModel() {
			//sets the values of the Jlist from the subjects arrayList in the main class. 
			Object[] files = concat(images, linkArray);
			Object[] values = files;
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) { 
				return values[index];
			}
		});
	}
	public Object[] concat(Object[] a, Object[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   Object[] c= new Object[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}

}
