package 网络信息安全考试test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import AES.AESCipher;
import AES.Struct;


/**
 * @function 线程池
 * @version 3.0
 * @author hou
 *
 */
public class FileServer {
	private final int TCPPORT = 2021; //端口
	private final int UDPPORT = 2020;
	private static String pukey = null;
	private static String prkey = null;
	public static String publicKey = null;
	private static String secretKey = null;
	
	static final String HOST = "127.0.0.1"; //连接地址
	private List<User> userList = new ArrayList<User>();
	//UDP
	DatagramSocket udpSocket;
	//TCP
	ServerSocket tcpSocket;
	File tfile = new File("");//参数为空 
	String courseFile = tfile.getCanonicalPath() ; 
	ExecutorService executorService; // 线程池
	final int POOL_SIZE = 6; // 单个处理器线程池工作线程数目

	public FileServer() throws IOException {
//		ServerSocket serverSocket = new ServerSocket(0);
//		int p1 = serverSocket.getLocalPort();
//		serverSocket.close();
//		serverSocket = new ServerSocket(0);
//		int p2 = serverSocket.getLocalPort();
//		serverSocket.close();
		//TCP
		userList = UserLoginDao.list(0, 5);
		tcpSocket = new ServerSocket(TCPPORT); // 创建服务器端套接字
		//UDP
		udpSocket = new DatagramSocket(UDPPORT);
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统可用处理器的数目
		// 由JVM根据系统的情况来决定线程的数量
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		System.out.println("Initate system.");
	}

	public static void main(String[] args) throws IOException {
		try {
			new FileServer().servic();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 启动服务
	}

	
	/**
	 * service implements
	 * @throws Exception 
	 */
	public void servic() throws Exception {
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
//		System.out.println("P="+P+" "+"Q="+Q);
	
		BigInteger PP = new BigInteger(P+"");
		BigInteger QQ = new BigInteger(Q+"");
		BigInteger[][] keys = genKey(PP,QQ);
		
		BigInteger[] pu = keys[0];
		BigInteger[] pr = keys[1];
		pukey = pu[0]+","+pu[1];
		prkey = pr[0]+","+pr[1];
		
	
		
		while(true){
			Socket socket = null;
			Scanner in = new Scanner(System.in);
			String st = null;
			String name = null;
			String password = null;
			System.out.println("Menu:");
			System.out.println("	1:[register] [name] [password]");
			System.out.println("	2:[login] [name] [password]");
			System.out.println("	3:[quit]");
			st = in.nextLine();
			String[] split = st.split(" ");
			if(split.length == 1){
				if(st.equals("quit"))
					continue;
			}else if(split.length == 3){
				if(split[0].equals("register")){
					boolean flag = true;
					for(int i=0;i<userList.size();i++){
						if(userList.get(i).getName().equals(split[1])){
							flag = false;
						}
					}
					if(flag == true){
						MD5 md5 = new MD5();
						User mdu = new User(md5.start(split[1]),md5.start(split[2]));
						User user = new User(split[1],split[2]);
						userList.add(user);
						UserLoginDao.register(mdu);
						System.out.println("Registe success.Welcome " + split[1] + "!");
						while (true) {
							try {
								socket = tcpSocket.accept(); // 等待用户连接
								PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
								DatagramPacket dataPacket = new DatagramPacket(new byte[1024],1024);
//								udpSocket.receive(dataPacket); //接受一个空文件来获取客户端UDP的地址和端口
								Handler handler = new Handler(socket,dataPacket,user.getName());
								executorService.execute(handler); 
								while(true){
									String msg = in.nextLine();
									if(msg.equals("send")){
										publicKey = handler.getPubKey();
										System.out.println("Please input the secret Key:");
										Scanner sc = new Scanner(System.in);
										secretKey = sc.nextLine();
										System.out.println(publicKey);
										String message1 = Rencrypt(secretKey,publicKey);
										String signature = "Hello";
										String message2 = Rencrypt(signature,prkey);
										handler.setSecretKey(secretKey);
										String op = "send"+" "+message1+" "+message2+" "+pukey;
										pw.println(op);
										continue;
									}
									
									if(handler.getSpecial()==1){
										List<Struct> sList = new ArrayList<Struct>();
										AESCipher ac = new AESCipher();
										sList = ac.encryptFile(secretKey, "D://"+user.getName()+".txt");
										pw.println("special " + sList.size());
										for(Struct struc:sList){
											pw.println(struc.str+" "+struc.len);
										}
										continue;
									}
									pw.println(msg);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}else{
						System.out.println("The name has registed.");
					}
				}else if(split[0].equals("login")){
						boolean flag = false;
						for(int i=0;i<userList.size();i++){
							MD5 md5 = new MD5();
							if(userList.get(i).getName().equals(md5.start(split[1]))&&userList.get(i).getPassword().equals(md5.start(split[2]))){
								flag = true;
							}
						}
						if(flag == true){
							User user = new User(split[1],split[2]);
							System.out.println("Login success.Welcome " + split[1] + "!");
							while (true) {
								try {
									socket = tcpSocket.accept(); // 等待用户连接
									PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
//									pw.println(courseFile); //将文件根目录输出给客户端
									DatagramPacket dataPacket = new DatagramPacket(new byte[1024],1024);
//									udpSocket.receive(dataPacket); //接受一个空文件来获取客户端UDP的地址和端口
									Handler handler = new Handler(socket,dataPacket,user.getName());
									executorService.execute(handler);
									while(true){
										String msg = in.nextLine();
										if(msg.equals("send")){
											publicKey = handler.getPubKey();
											System.out.println("Please input the secret Key:");
											Scanner sc = new Scanner(System.in);
											secretKey = sc.nextLine();
											System.out.println(publicKey);
											String message1 = Rencrypt(secretKey,publicKey);
											String signature = "Hello";
											String message2 = Rencrypt(signature,prkey);
											handler.setSecretKey(secretKey);
											String op = "send"+" "+message1+" "+message2+" "+pukey;
											pw.println(op);
											continue;
										}
										
										if(handler.getSpecial()==1){
											List<Struct> sList = new ArrayList<Struct>();
											AESCipher ac = new AESCipher();
											sList = ac.encryptFile(secretKey, "D://"+user.getName()+".txt");
											pw.println("special " + sList.size());
											handler.setSpecial(0);
											for(Struct struc:sList){
												pw.println(struc.str+" "+struc.len);
											}
											continue;
										}
										pw.println(msg);
										
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else{
							System.out.println("Wrong password or Wrong name.");
						}
				}
				continue;
			}else{
				System.out.println("Wrong format.");
			}
		}

	}
	
	public static boolean checkPrime(int n){
		for(int i=2;i<=n/2;i++){
			if(n%i==0){
				return false;
			}
		}
		return true;
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

		// 公钥  私钥
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

	public static BigInteger getD(int e,int n){
	int k = -1;
	int d = 1;
	while(k!=0){
		int m = e*d-1;
		k = m%n;
//		System.out.println("这是第"+d+"次运算,m为："+m+"k为："+k);
		d++;
	}
//	System.out.println(d);
	BigInteger result = new BigInteger((int)(d-1)+"");
//	System.out.println(result);
	return result;
	}
	
	/**
	 * 
	 * @param str
	 * @param pukey
	 * @return
	 */
	public static String Rencrypt(String str,String pukey){
//		System.out.println("Ҫ���ܵ�����Ϊ��"+str);
		char[] ch = str.toCharArray();
		String msg = "msg";
		for(int i=0;i<ch.length;i++){
//			System.out.println(ch[i]);
//			System.out.println(">>>>>>>>>>>>>>>>>Encrypting>>>>>>>>>>>>>>>>>>>>");
			msg = msg+","+rencrypt(ch[i],pukey);
		}
//		System.out.println(msg);
		return msg;
		
	}

	/**
	 * 
	 * @param ch
	 * @param pukey
	 * @return
	 */
	public static String rencrypt(char ch,String pukey){
		String[] str = pukey.split(",");
		int e = Integer.parseInt(str[1]);
		int n = Integer.parseInt(str[0]);
		BigInteger E = new BigInteger(e+"");
		BigInteger N = new BigInteger(n+"");
		BigInteger C = new BigInteger((int)ch+"");
//		System.out.println("ch="+C);
		BigInteger result = expMode(C,E,N);
		return result+"";

	}
	/**
	 * ���������������Ȼ��Գ��������ȡģ
		(base ^ exponent) mod n
	 * @param base
	 * @param exponent
	 * @param n
	 * @return
	 */
	public static BigInteger expMode(BigInteger base, BigInteger exponent, BigInteger n){
		char[] binaryArray = new StringBuilder(exponent.toString(2)).reverse().toString().toCharArray() ;
		int r = binaryArray.length ;
		List<BigInteger> baseArray = new ArrayList<BigInteger>() ;
		
		BigInteger preBase = base ;
		baseArray.add(preBase);
		for(int i = 0 ; i < r - 1 ; i ++){
			BigInteger nextBase = preBase.multiply(preBase).mod(n) ;
			baseArray.add(nextBase) ;
	        preBase = nextBase ;
		}
		BigInteger a_w_b = multi(baseArray.toArray(new BigInteger[baseArray.size()]), binaryArray, n) ;
		return a_w_b.mod(n) ;
	}
	
	
	private static BigInteger multi(BigInteger[] array, char[] bin_array, BigInteger n){
	    BigInteger result = BigInteger.ONE ;
	    for(int index = 0 ; index < array.length ; index ++){
	        BigInteger a = array[index] ;
	        if(bin_array[index] == '0'){
	            continue ;
	        }
	        result = result.multiply(a) ;
	        result = result.mod(n) ;
	    }
	    return result ;
	}
	
	/**
	 * 
	 * @param str
	 * @param prkey
	 * @return
	 */
	public static String Rdcrypt(String str,String prkey){
//		System.out.println("Ҫ���ܵ�����Ϊ��"+str);
		String[] message = str.split(",");
		String msg = "msg";
		for(int i=1;i<message.length;i++){
			System.out.println(">>>>>>>>>>>>>Decrypting>>>>>>>>>>>>>>>>>>>>>>>");
			msg = msg+","+rdcrypt(message[i],prkey);
		}
		msg = asciiToString(msg);
		return msg;
		
	}

	/**
	 * 
	 * @param ch
	 * @param pukey
	 * @return
	 */
	public static String rdcrypt(String ch,String prkey){
		String[] str = prkey.split(",");
		int d = Integer.parseInt(str[1]);
		int n = Integer.parseInt(str[0]);
//		System.out.println((int)ch);
		BigInteger D = new BigInteger(d+"");
		BigInteger N = new BigInteger(n+"");
		BigInteger num = new BigInteger(Integer.parseInt(ch)+"");
//		System.out.println("ch="+num);
		BigInteger result = expMode(num,D,N);
		
		return result+"";
	}
	
	public static String asciiToString(String value){
		StringBuffer sb = new StringBuffer();
		String[] chars = value.split(",");
		for(int i=1;i<chars.length;i++)
			sb.append((char)Integer.parseInt(chars[i]));
		return sb.toString();
	}
}
