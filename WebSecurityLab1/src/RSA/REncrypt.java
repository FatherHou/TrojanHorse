package RSA;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class REncrypt {
	
	/**
	 * 
	 * @param str
	 * @param pukey
	 * @return
	 */
	public static String Rencrypt(String str,String pukey){
//		System.out.println("要加密的数据为："+str);
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
		BigInteger result = REncrypt.expMode(C,E,N);
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
}
