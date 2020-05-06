package KeyLogger;

public class Relect {
	
public static String Relectkey(int key){
	 String name = "";
	 if(key == 27 )
		 name = "ESC";
	 if(key >= 112 && key <= 123){
		 int num = key -111;
		 name = 'F'+ String.valueOf(num);
	 }
	 if(key >= 48 && key <= 57){
		 int num = key - 48;
		 name = String.valueOf(num);
	 }
	 if(key >= 65 && key <= 90){
		char num = (char) key;
		name = String.valueOf(num);
	 }
	 if(key == 144)
		 name = "NumLock";
	 if(key >= 96 && key <= 105){
		 int num = key - 96;
		name = "Digital keypad"+String.valueOf(num);
	 }
	 if(key == 107){
		 name = "Digital keypad+";
	 }
	 if(key == 109){
		 name = "Digital keypad-";
	 }
	 if(key == 106){
		 name = "Digital keypad*";
	 }
	 if(key == 111){
		 name = "Digital keypad/";
	 }
	 if(key == 110){
		 name = "Digital keypad.";
	 }
	 if(key == 13){
		 name = "Digital keypad Enter";
	 }
	 if(key == 45){
		 name = "Insert";
	 }
	 if(key == 46){
		 name = "Delete";
	 }
	 if(key == 33){
		 name = "PageUp";
	 }
	 if(key == 34){
		 name = "PageDown";
	 }
	 if(key == 35){
		 name = "End";
	 }
	 if( key == 32){
		 name = "Space";
	 }
	 
	 if(key == 36){
		 name = "Home";
	 }
	 if(key == 37){
		 name = "Digital keypad¡û";
	 }
	 if(key == 38){
		 name = "Digital keypad¡ü";
	 }
	 if(key == 39){
		 name = "Digital keypad¡ú";
	 }
	 if(key == 40){
		 name = "Digital keypad¡ı";
	 }
	 if(key == 9){
		 name = "Tab";
	 }
	 if(key == 20){
		 name = "Caps Lock";
	 }
	 if(key == 160){
		 name = "Shift(Left)";
	 }
	 if(key == 161){
		 name = "Shift(Right)";
	 }
	 if(key == 162){
		 name = "Ctrl(Left)";
	 }
	 if(key == 163){
		 name = "Ctrl(Right)";
	 }
	 if(key == 91){
		 name = "Win(Left)";
	 }
	 if(key == 92){
		 name = "Win(Right)";
	 }
	 if(key == 164){
		 name = "Alt(Left)";
	 }
	 if(key == 165){
		 name = "Alt(Right)";
	 }
	 if(key == 189){
		 name = "-";
	 }
	 if(key == 187){
		 name = "=";
	 }
	 if(key == 8){
		 name = "Backspace";
	 }
	 if(key == 219){
		 name = "[";
	 }
	 if(key == 221){
		 name = "]";
	 }
	 if(key == 220){
		 name = "\\";
	 }
	 if(key == 186){
		 name = ";";
	 }
	 if(key == 222){
		 name = "¡®";
	 }
	 if(key == 188){
		 name = ",";
	 }
	 if(key == 190){
		 name = ".";
	 }
	 if(key == 191){
		 name = "/";
	 }
	 if(key == 13){
		 name = "Enter";
	 }
	 
	 return name;
	 

 }
}
