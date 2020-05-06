package cipher;

import java.util.List;
import AES.Struct;

public interface Cipher {
	
	/**
	 * Encrypt a text
	 * 
	 * @param plaintext
	 * @param key
	 * @return list
	 */
	public List<Struct> encryptText(String plaintext, String key);

	/**
	 * Decrypt a text
	 * 
	 * @param ciphertextList
	 * @param key
	 * @return plaintext
	 */
	public String decryptText(List<Struct> ciphertextList, String key);
	
	/**
	 * Encrypt a file
	 * 
	 * @param key
	 * @param fileName
	 * @return list file's encryption list
	 */
	public List<Struct> encryptFile(String key, String fileName);
	
	/**
	 * Decrypt a file
	 * 
	 * @param ciphertextList
	 *            ciphertext list
	 * @param key
	 *            key
	 * @param fileName
	 *            file's name
	 */
	public void decryptFile(List<Struct> ciphertextList, String key, String fileName);

}
