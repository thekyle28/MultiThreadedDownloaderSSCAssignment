package downloadGui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import downloader.FileDownloader;

public class DownloaderGUI extends JFrame {

	private JPanel contentPane;
	private static JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(List<String> args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DownloaderGUI frame = new DownloaderGUI(args, null);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param fd 
	 * @param objects 
	 */
	public DownloaderGUI(List<String> list, FileDownloader fd) {
		setBounds(100, 100, 953, 660);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("Download progress");
		panel.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblNewLabel.setBackground(Color.LIGHT_GRAY);
		
		table = new JTable();
		table.setBackground(Color.WHITE);
		//create a default table model for the table
		DefaultTableModel model = new DefaultTableModel();
		
		//create a two dimensional array version of the file array passed
		Object[][] files2D = new Object[list.size()][2];
		for(int i = 0; i < list.size(); i++){
			files2D[i][0] = list.get(i);
			files2D[i][1] = "Queued";
		}
		
		String [] columnIdentifiers = new String[] {"File", "Download Status"};
		
		model.setDataVector(files2D, columnIdentifiers);
		table.setModel(model);
		contentPane.add(table, BorderLayout.CENTER);
		
		try {
			fd.download(list);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void setStatus(String fileName, String status){
		System.out.println("Comparing " + fileName + " to...");
		for (int i = 0; i < table.getRowCount(); i++) {
			
			System.out.println(table.getValueAt(i, 1).toString());
			
			if(table.getValueAt(i, 0).toString().equals(fileName)){
				System.out.println("I'm here!");

				table.setValueAt(status, i, 1);		
			}
		}

	}

	

}

