package hou;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class Handler implements Runnable { // 负责与单个客户通信的线程
	private Socket tcpSocket;
	//UDP
	private DatagramSocket udpSocket;
	private DatagramPacket udpPacket;
	static final String HOST = "127.0.0.1"; //连接地址
	private final int UDPPORT = 2020;
	BufferedReader br;
	BufferedWriter bw;
	PrintWriter pw;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 
	String userFile = courseFile;

	public Handler(Socket tcpsocket,DatagramPacket packet) throws SocketException,IOException {
		this.tcpSocket = tcpsocket;
		this.udpPacket = packet;
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
//			pw.println(courseFile);
			String info = null; //接收用户输入的信息
			while ((info = br.readLine()) != null) {
				System.out.println(info); //输出用户发送的消息
				if(info.equals("ls")){
			    	File file = new File(userFile);  
			        File[] array = file.listFiles();   
			        for(int i=0;i<array.length;i++)
			        {   
			        	if(array[i].isDirectory()){
			        		pw.println("<dir> " + array[i].getName() + " " + array[i].length());
			        	}else if(array[i].isFile()){
			        		pw.println("<file> " + array[i].getName() + " " + array[i].length());
			        	}
			        	if(i==array.length-1){
			        		pw.println("/n");
			        	}
			        } 
				}else if(info.contains("cd")){
					if(info.length()>2){
						if(info.contains("..")){
//							String tmsg="";
//							char[] msgg=courseFile.toCharArray();
//							for(int i=0;i<courseFile.length();i++){
//								tmsg += msgg[courseFile.length()-1-i];
//							}
//							System.out.println(tmsg);
							if(userFile.equals(courseFile)){
								pw.println("当前已经是根目录");
								pw.println("/n");
							}else{
								File file = new File(userFile); 
								userFile = file.getParent();
								pw.println("courseEnv > OK");
								pw.println("/n");
							}
						}else{
//							System.out.println(msg);
							info=info.replace("cd ", "");
//							System.out.println(msg);
//							String tmsg=tcpMsg.substring(3, tcpMsg.length());
//							System.out.println(tmsg);
					    	String newcourseFile="";
					    	newcourseFile=userFile+"\\"+info;
					    	File file = new File(newcourseFile);
					    	if(file.isDirectory()){
					    		pw.println(info+" > OK");
					    		pw.println("/n");
						    	userFile=newcourseFile;
					    	}else{
					    		pw.println("unknown dir");
					    		pw.println("/n");
					    	}
						}
						}else{
							pw.println("unknown cmd");
						}
				}else if (info.equals("bye")) {
					break; //退出
				}else if(info.contains("get")){
					if(info.length()>3){
					info = info.replace("get ", "") ;
//						byte[] udpmsg=info.getBytes();
//						DatagramPacket dp = new DatagramPacket(new byte[512], 512,new InetSocketAddress(HOST, UDPPORT));
					File file = new File(userFile+"//"+info);
					if(file.isFile()){
						FileInputStream fis = new FileInputStream(file);
//							InputStream is = this.getClass().getResourceAsStream("D://hby2.jpg");
						byte[] buffer = new byte[8192];
						int len = 0;
						while((len = fis.read(buffer, 0, buffer.length)) > 0){
							System.out.println(len);
							DatagramPacket packet = new DatagramPacket(buffer, len,new InetSocketAddress(udpPacket.getAddress(), udpPacket.getPort()));
							udpSocket.send(packet);
						}
					}else{
						pw.println("这不是一个文件");
					}
					continue;
//						System.out.println("udp:"+dp.getAddress() + ":" + dp.getPort() + ">" + info);
//						dp.setData(("udp you said:" + info).getBytes());
//						System.out.println("hby2");
//						udpSocket.send(dp); // 回复数据
//						System.out.println("hby3");
					}else{
						pw.println("unknown cmd");
					}
				}
				else{
//					pw.println(msg); //发送给服务器端
//					System.out.println(br.readLine()); //输出服务器返回的消息
					pw.println("unknown cmd");
				}
				
//				else{
//				pw.println("you said:" + info); //向客户端返回用户发送的消息，println输出完后会自动刷新缓冲区
//				if (info.equals("quit")) { //如果用户输入“quit”就退出
//					break;
//				}
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
}
