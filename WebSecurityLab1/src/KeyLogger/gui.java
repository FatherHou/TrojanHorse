package KeyLogger;



import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class gui
 {
 
    public static JFrame frame;
    private JTextField txtName;
    private JPasswordField passwordField;
 
   
    /**
     *
 Launch the application.
     */
/*    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                try {
                   gui window = new gui();
                   window.frame.setVisible(true);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
 
    /**
     *Create the application.
     */
    public gui(){
        initialize();
    }
 
    /**
     *Initialize the contents of the frame.
     */
    private void initialize(){
        frame = new JFrame();
        frame.setBounds(100,100,500,370);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
         
        txtName = new JTextField();
        txtName.setBounds(228,71,175,28);
        frame.getContentPane().add(txtName);
        txtName.setColumns(10);
        
        JLabel lblMessage = new JLabel("Please enter your name.");
        lblMessage.setBounds(248,149,151,30);
        frame.getContentPane().add(lblMessage);
         
        JButton btnSubmit = new JButton("login");
        btnSubmit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		String username = txtName.getText();
        		String password = passwordField.getText();
        		
        		if(username.equals("hby")&&password.equals("123")){
        			System.out.println("diaoyong");
        			gui2 f=new gui2();
        		    f.frame2.setVisible(true);
        		    frame.setVisible(false);
        		}else if(username.equals("sc")&&password.equals("234")){
        			System.out.println("diaoyong");
        			gui2 f=new gui2();
        		    f.frame2.setVisible(true);
        		    frame.setVisible(false);
        		}else 
        			lblMessage.setText("login fail!");
        		
        		
        		
        			
        		    
        	}
        });
        btnSubmit.setBounds(112,193,90,30);
        frame.getContentPane().add(btnSubmit);
        
        JButton btnRegister = new JButton("register");
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		    gui2 f=new gui2();
        		    f.frame2.setVisible(true);
        	}
        });
        btnRegister.setBounds(307, 193, 93, 30);
        frame.getContentPane().add(btnRegister);
        
        JLabel lblName = new JLabel("Name:");
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("����", Font.PLAIN, 18));
        lblName.setBounds(148,67,73,30);
        frame.getContentPane().add(lblName);
        
        JLabel lblPassword = new JLabel("password:");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("����", Font.PLAIN, 18));
        lblPassword.setBounds(112, 106, 86, 30);
        frame.getContentPane().add(lblPassword);
        
        
        
        passwordField = new JPasswordField();
        passwordField.setBounds(228, 109, 175, 30);
        frame.getContentPane().add(passwordField);
        
        ImageIcon image=new ImageIcon("index.jpg");
        //System.out.println(image.getDescription());
		JLabel label=new JLabel(image);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(label);
		label.setBounds(0,0,image.getIconWidth(),image.getIconHeight());
		//frame.setVisible(true);
		frame.setResizable(false);
    }
}
