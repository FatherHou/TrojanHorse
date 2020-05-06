package RSA;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RDecrypt {
	
	/**
	 * 
	 * @param str
	 * @param prkey
	 * @return
	 */
	public static String Rdcrypt(String str,String prkey){

		String[] message = str.split(",");
		String msg = "msg";
		for(int i=1;i<message.length;i++){
//			System.out.println(">>>>>>>>>>>>>Decrypting>>>>>>>>>>>>>>>>>>>>>>>");
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
	
	/**
	 * Super large integers over large power and then modulo oversized integers
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
	
	public static String asciiToString(String value){
		StringBuffer sb = new StringBuffer();
		String[] chars = value.split(",");
		for(int i=1;i<chars.length;i++)
			sb.append((char)Integer.parseInt(chars[i]));
		return sb.toString();
	}
}
