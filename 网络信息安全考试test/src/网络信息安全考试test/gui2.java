package 网络信息安全考试test;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class gui2 {

	public JFrame frame2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui2 window = new gui2();
					window.frame2.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame2 = new JFrame();
		frame2.setBounds(100, 100, 450, 300);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 225, 180);
		frame2.getContentPane().add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
		
		JButton btnFlush = new JButton("flush");
		btnFlush.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				////get key
				textArea.setText(textArea.getText()+"\r\n"+"asd");
			}
		});
		btnFlush.setBounds(70, 212, 93, 23);
		frame2.getContentPane().add(btnFlush);
		
		JButton btnScreenshot = new JButton("screenshot");
		btnScreenshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				///screenshot
			}
		});
		btnScreenshot.setBounds(295, 212, 93, 23);
		frame2.getContentPane().add(btnScreenshot);
		
		
		
	}
}
