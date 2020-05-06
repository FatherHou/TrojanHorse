package 网络信息安全考试test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @function 客户端
 * @version 3.0
 * @author hou
 *
 */
public class FileClient {
	static final int TCPPORT = 2021; //连接端口
	static final String HOST = "127.0.0.1"; //连接地址
	static final int UDPPORT = 2020;
	//UDP
	DatagramSocket udpSocket; // 客户端DatagramSocket 
	//TCP
	Socket tcpsocket = new Socket();
	String name=null;
	File tfile = new File("");//参数为空 
//	String courseFile = tfile.getCanonicalPath() ; 
	String recPath = tfile.getCanonicalPath()+"/hby/";
	File recfile = new File(recPath);
	
	
	public FileClient() throws UnknownHostException, IOException {
		//socket = new Socket(HOST, PORT); //创建客户端套接字
		//TCP
		tcpsocket = new Socket();
		tcpsocket.connect(new InetSocketAddress(HOST, TCPPORT));
		System.out.println(HOST+":"+TCPPORT+">连接成功");
		//UDP
		udpSocket = new DatagramSocket(); // 随机可用端口，又称匿名端口
	}

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new FileClient().send();
	}

	/**
	 * send implements
	 * @throws IOException 
	 */
	public void send() throws IOException {

		try {
			//TCP
			//客户端输出流，向服务器发消息
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					tcpsocket.getOutputStream()));
			//客户端输入流，接收服务器消息
			BufferedReader br = new BufferedReader(new InputStreamReader(
					tcpsocket.getInputStream()));
			PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，及时刷新
			Scanner in = new Scanner(System.in); //接受用户信息
			String tcpMsg = null; //接收客户端写入的信息
			//UDP
			DatagramPacket requestPacket = new DatagramPacket(new byte[1024],1024,new InetSocketAddress(HOST, UDPPORT));
			String courseFile = br.readLine(); //接收客户端发送的文件根目录
			File root = new File(courseFile); 
			if(root.isDirectory()){ //判断文件根目录是否存在
				
			}else{
				System.out.println("目录无效");
			}
			 if(!recfile.exists()){
		         
		         recfile.mkdirs();//创建目录
		     }
			while ((tcpMsg = in.next()+in.nextLine()) != null) {
				pw.println(tcpMsg); //发送给服务器端
				udpSocket.send(requestPacket); //发送给服务器端一个数据包方便服务器判断客户端地址和端口
				if (tcpMsg.contains("get")){
					if(tcpMsg.length()>3){
						String s = tcpMsg; // 获取用户输入
						byte[] info = s.getBytes();
						String temrecpath = recPath; //设置本次文件存储路径
						temrecpath = temrecpath + s.replace("get ", "");
						String tempath = courseFile; //设置文件读取的目录
						tempath = tempath + "//" + s.replace("get ", "");
						System.out.println(tempath);
						File newfile = new File(temrecpath);
						File genfile = new File(tempath);
						if(genfile.isFile()){
							System.out.println("开始接收文件:" + s.replace("get ",""));
							System.out.println("文件大小:" + genfile.length() + "B");
							System.out.println("文件路径:" + genfile.getPath());
							FileOutputStream fos = new FileOutputStream(newfile); //在存储路径创建相同文件名的新文件来写入
							while(true){ //循环接收数据包
								DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
								try{
									udpSocket.receive(packet);
									System.out.println("Packet长度:"+packet.getLength());
									fos.write(packet.getData(), 0, packet.getLength()); //将每次包的数据写入到新建的文件中
									byte[] temp=packet.getData();
									if(packet.getLength()!=8192){ //当数据包长度不满时证明后续无数据，结束传输
										System.out.println("文件接收完毕");
										break;
									}
								}catch(Exception e){
									try {
										System.out.println("传输结束");
										fos.flush();
										fos.close();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									break;
								}
							}
						}else{
							System.out.println("unknown file");
						}
//						DatagramPacket inputDp = new DatagramPacket(new byte[512], 512);
////						System.out.println("1");
//						udpSocket.receive(inputDp); // 接受服务器返回的信息
////						System.out.println("2");
//						String udpMsg = new String(inputDp.getData(), 0, inputDp.getLength());
//						System.out.println("UDP:" + inputDp.getAddress() + ":" + inputDp.getPort());
//						System.out.println("back from server:"+udpMsg);
						continue;
					}
				}else if(tcpMsg.contains("cd")){ //在使用cd命令后，更改服务器端发送来的根目录路径
					if(tcpMsg.length()>2){
						if(tcpMsg.contains("..")){
							File file = new File(courseFile); 
							courseFile = file.getParent();
						}else{
							tcpMsg=tcpMsg.replace("cd ", "");
					    	String newcourseFile="";
					    	newcourseFile=courseFile+"\\"+tcpMsg;
					    	File file = new File(newcourseFile);
					    	if(file.isDirectory()){
						    	courseFile=newcourseFile;
					    	}
						}
						}
				}else if (tcpMsg.equals("bye")) {
					break; //退出
				}
				String str = br.readLine(); //在判断完输入的指令后读取客户端返回来的TCP信息
				int i=0;
			       while(str != null){ //循环从输入流中读取
			    	   System.out.println(str);
			    	   str = br.readLine();
			    	   if(str.equals("/n")){ //以/n为结束标志，出现/n就开始下一次输入
			    		   break;
			    	   }
			       }
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != tcpsocket) {
				try {
					tcpsocket.close(); //断开连接
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	
//	/*
//     * @Name：getFile
//     * @Author: hou
//     */
//    private void getFile(String FilePath) throws IOException{   
//    	// 获得指定文件对象
//    	File file = new File(FilePath); 
////      System.out.println(file.getName());
////    	System.out.println(file.getPath());
//        // 获得该文件夹内的所有文件   
//        File[] array = file.listFiles();   
////        System.out.println(array.length);
//        for(int i=0;i<array.length;i++)
//        {   
//        	if(array[i].isDirectory()){
//        		System.out.println("<dir> " + array[i].getName() + " " + array[i].length());
//        	}else if(array[i].isFile()){
//        		System.out.println("<file> " + array[i].getName() + " " + array[i].length());
//        	}
//        }   
//    }
    
	 /*
     * @Name：nextFile
     * @Author: hou
     */
//    private void nextFile(String msg) throws IOException{
//    	String newcourseFile="";
//    	newcourseFile=courseFile+"\\"+msg;
//    	File file = new File(newcourseFile);
//    	if(file.isDirectory()){
//	    	System.out.println(msg+" > OK");
//	    	courseFile=newcourseFile;
//    	}else{
//    		System.out.println("unknown dir");
//    	}
//    }
    

}
