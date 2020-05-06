package 网络信息安全考试test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import AES.Struct;
import AES.AESCipher;
import cipher.Cipher;

/**
 * @function 服务器池
 * @version 3.0
 * @author hou
 *
 */
public class Handler implements Runnable { // 负责与单个客户通信的线程
	private Socket tcpSocket;
	//UDP
	private DatagramSocket udpSocket;
	private DatagramPacket udpPacket;
	static final String HOST = "127.0.0.1"; //连接地址
	BufferedReader br;
	BufferedWriter bw;
	PrintWriter pw;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 
	String userFile = courseFile;
	String pubKey = null;
	String name = null;
	String secretKey = null;
	String specialKey = "F10"; 
	int special = 0;
	

	
	

	/**
	 * @return the special
	 */
	public int getSpecial() {
		return special;
	}

	/**
	 * @param special the special to set
	 */
	public void setSpecial(int special) {
		this.special = special;
	}

	/**
	 * @return the pubKey
	 */
	public String getPubKey() {
		return pubKey;
	}

	/**
	 * @param pubKey the pubKey to set
	 */
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Handler(Socket tcpsocket,DatagramPacket packet, String name) throws SocketException,IOException {
		this.tcpSocket = tcpsocket;
		this.udpPacket = packet;
		this.name = name;
		udpSocket = new DatagramSocket();
	}

	public void initStream() throws IOException { // 初始化输入输出流对象方法
		br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
		bw = new BufferedWriter(
				new OutputStreamWriter(tcpSocket.getOutputStream()));
		pw = new PrintWriter(bw, true);
	}

	public void run() { // 执行的内容
		try {
			System.out.println("新连接，连接地址：" + tcpSocket.getInetAddress() + "："
					+ tcpSocket.getPort()); //客户端信息
			initStream(); // 初始化输入输出流对象
			String info = null; //接收用户输入的信息
			File file = new File("D://" + name + ".txt");
			int count = 1;
			while ((info = br.readLine()) != null) {
				
				String[] split = info.split(" ");
				if(split[0].equals("Key")){
					pubKey = split[1];
					this.setPubKey(pubKey);
					System.out.println("SecretKey exchanged successfully!");
				}else if(split[0].equals("screen")){
					System.out.println(info); //输出用户发送的消息
					int length = Integer.valueOf(split[1]);
					List<Struct> sList = new ArrayList<Struct>();
					for(int i=0;i<length;i++){
						info = br.readLine();
						split = info.split(" ");
						Struct st = new Struct(split[0],Integer.valueOf(split[1]));
						sList.add(st);
					}
					Cipher ci = new AESCipher();
					String name = "D://hbytest.jpg";
			        String[] split2 = name.split("\\.");
					split2[0] = split2[0] + String.valueOf(count);
					count++;
					split2[1] = split2[0] + "." +split2[1];
					System.out.println("Recieve:"+split2[1]);
					ci.decryptFile(sList, secretKey, split2[1]);
					System.out.println("Recieve Success.");
//					byte[] inputByte = null;
//			        int length = 0;
//			        DataInputStream dis = null;
//			        dis = new DataInputStream(tcpSocket.getInputStream());
//			        String name = "D://hbytest.jpg";
//			        String[] split2 = name.split("\\.");
//					split2[0] = split2[0] + String.valueOf(count);
//					count++;
//					split2[1] = split2[0] + "." +split2[1];
//			        File file = new File(split2[1]);
//			        FileOutputStream fos = new FileOutputStream(file);
//	                inputByte = new byte[1024];
//	                System.out.println("开始接收数据...");
//	                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
//	                	if(length == 4){
//	                		break;
//	                	}
//	                    fos.write(inputByte, 0, length);
//	                    fos.flush();
//	                }
//					ci.decryptFile(sList, "key", "D://hby.jpg");
	                System.out.println("完成接收");
				}else if(split[0].equals("Croken")){
					System.out.println(info); //输出用户发送的消息
					int length = Integer.valueOf(split[1]);
					List<Struct> sList = new ArrayList<Struct>();
					for(int i=0;i<length;i++){
						info = br.readLine();
						split = info.split(" ");
						Struct st = new Struct(split[0],Integer.valueOf(split[1]));
						sList.add(st);
					}
					Cipher ci = new AESCipher();
					String receivestr = ci.decryptText(sList, secretKey);
					String [] info1 = receivestr.split("=");
					String sc = Relect.Relectkey(Integer.valueOf(info1[1]));
					System.out.println(info1[0]+":"+sc);
		            byte[] buff = new byte[]{};
		            String str = info1[0]+":"+sc;
		            if(sc.equals("F10")&&special == 0){
		            	special = 3;
		            }
		            FileOutputStream o = null;
		            if(!file.exists()){
		            	file.createNewFile();
		            	}
	            	buff=str.getBytes();
	            	o=new FileOutputStream(file,true);
	            	o.write(buff);
	            	o.write("\r\n".getBytes());
	            	o.flush();
	            	o.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} finally {
			if (null != tcpSocket) {
				try {
					tcpSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
     * @Name：getFile
     * @Author: hou
     */
    private void getFile(String FilePath) throws IOException{   
    	// 获得指定文件对象
    	File file = new File(FilePath); 
        // 获得该文件夹内的所有文件   
        File[] array = file.listFiles();   
        for(int i=0;i<array.length;i++) //根据类型不同输出
        {   
        	if(array[i].isDirectory()){
        		pw.println("<dir> " + array[i].getName() + " " + array[i].length());
        	}else if(array[i].isFile()){
        		pw.println("<file> " + array[i].getName() + " " + array[i].length());
        	}
        }   
    }
}
