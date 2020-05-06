package AES;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import cipher.Cipher;

public class AESCipher implements Cipher {

	/**
	 * Decrypt a text
	 * 
	 * @param ciphertextList
	 * @param key
	 * @return plaintext
	 */
	@Override
	public String decryptText(List<Struct> ciphertextList, String key) {
		StringBuilder sb = new StringBuilder();
		for (Struct temp : ciphertextList) {
			String str = decrypt(temp.str, key);
			sb.append(str.substring(0, temp.len));
		}
		return sb.toString();
	}

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
	@Override
	public void decryptFile(List<Struct> ciphertextList, String key, String fileName) {
		try {
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			for (Struct temp : ciphertextList) {
				String str = decrypt(temp.str, key);
				byte[] data = Base64.getDecoder().decode(str);
				out.write(data, 0, temp.len);
				out.flush();
			}
			out.close();
			System.err.println("Finish writing file!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * External decryption interface public decryption api
	 * 
	 * @param encryptedText
	 *            encrypted text
	 * @param key
	 *            key
	 * @return decrypted text
	 */
	public String decrypt(String encryptedText, String key) {
		short[][] initialTextState = transfer(Base64Util.decodeToShorts(encryptedText));
		short[][] initialKeyState = transfer(transferToShorts(key));

		short[][] decryptState = coreDecrypt(initialTextState, initialKeyState);
		String plaintext = getOrigin(decryptState);
		return plaintext;
	}

	/**
	 * Restore the final decrypted short array to a string transfer state into string
	 * 
	 * @param decryptState
	 *            decrypt state
	 * @return plaintext string
	 */
	private String getOrigin(short[][] decryptState) {
		StringBuilder builder = new StringBuilder();
		for (short[] shorts : decryptState) {
			for (short s : shorts) {
				builder.append(String.valueOf((char) s));
			}
		}
		return builder.toString();
	}

	/**
	 * Decryption logic: multiplexing the core functions by extracting the reversible operation into an invertible matrix
	 * 
	 * @param encryptedTextState
	 *            initial encrypted text state
	 * @param keyState
	 *            initial key state
	 * @return decrypted state
	 */
	private short[][] coreDecrypt(short[][] encryptedTextState, short[][] keyState) {
		// obtain raw round keys
		short[][] rawRoundKeys = generateRoundKeys(keyState);

		// make it easier to obtain a whole block of round key in a round transformation
		short[][][] roundKeys = transfer(rawRoundKeys);

		for (int i = 1; i < roundKeys.length - 1; i++) {
			roundKeys[i] = mixColumns(roundKeys[i], AESConstants.INVERSE_CX);
		}

		short[][][] inverseRoundKeys = inverseRoundKeys(roundKeys);
		return coreEncrypt(encryptedTextState, inverseRoundKeys, AESConstants.INVERSE_SUBSTITUTE_BOX,
				AESConstants.INVERSE_CX, AESConstants.INVERSE_SHIFTING_TABLE);
	}

	/**
	 * [Decryption] Reverses the decryption extended key array to facilitate multiplexing of core encryption operations.
	 * 
	 * @param roundKeys
	 *            Decrypt extended key array
	 * @return Reversed decryption extended key array
	 */
	private short[][][] inverseRoundKeys(short[][][] roundKeys) {
		short[][][] result = new short[roundKeys.length][4][4];
		int length = roundKeys.length;
		for (int i = 0; i < roundKeys.length; i++) {
			result[i] = roundKeys[length - 1 - i];
		}
		return result;
	}

	/**
	 * Encrypt a text
	 * 
	 * @param plaintext
	 * @param key
	 * @return list
	 */
	@Override
	public List<Struct> encryptText(String plaintext, String key) {
		List<Struct> list = new ArrayList<Struct>();
		int i = 0;
		while (true) {
			if (plaintext.length() - i == 0) {
				break;
			} else if (plaintext.length() - i < 16) {
				String str = plaintext.substring(i);
				int len = plaintext.length() - i;
				for (int j = 0; j < 16 - len; j++)
					str += " ";
				list.add(new Struct(str, len));
				break;
			}
			String str = plaintext.substring(i, i + 16);
			list.add(new Struct(str, 16));
			i = i + 16;
		}
		for (Struct temp : list) {
			temp.str = encrypt(temp.str, key);
		}
		return list;
	}

	/**
	 * Encrypt a file
	 * 
	 * @param key
	 * @param fileName
	 * @return list file's encryption list
	 */
	@Override
	public List<Struct> encryptFile(String key, String fileName) {
		File file = new File(fileName);
		List<Struct> list = new ArrayList<Struct>();
		try {
			DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			byte[] data = new byte[10];
			int len = 0;
			while ((len = fis.read(data)) != -1) {
				list.add(new Struct(Base64Util.encode(data), len));
			}
			for (Struct temp : list) {
				temp.str = encrypt(temp.str, key);
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * External encryption interface  public encryption api
	 * 
	 * @param plaintext
	 *            text to be encrypted
	 * @param key
	 *            key
	 * @return encryption result
	 */
	public String encrypt(String plaintext, String key) {
		// transfer plaintext and key from one-dimension matrix
		// to (data.length / 4) x 4 matrix
		short[][] initialPTState = transfer(transferToShorts(plaintext));
		short[][] initialKeyState = transfer(transferToShorts(key));

		// obtain raw round keys
		short[][] rawRoundKeys = generateRoundKeys(initialKeyState);

		// make it easier to obtain a whole block of round key in a round transformation
		short[][][] roundKeys = transfer(rawRoundKeys);

		short[][] finalState = coreEncrypt(initialPTState, roundKeys, AESConstants.SUBSTITUTE_BOX, AESConstants.CX,
				AESConstants.SHIFTING_TABLE);
		return Base64Util.encode(transfer2Bytes(finalState));
	}

	/**
	 * The AES core operation enables the method to be multiplexed in the encryption/decryption operation by extracting the reversible operation into a reversible matrix as a parameter.
	 * 
	 * @param initialPTState
	 *          State array of plaintext or ciphertext
	 * @param roundKeys
	 *            Add/decrypt the round key array to use
	 * @param substituteTable
	 *            Add/decrypt the S box to be used
	 * @param mixColumnTable
	 *            An array used to replace a polynomial in a column mix
	 * @param shiftingTable
	 *            An array of the number of bits used in the row transform to determine the left shift between words
	 * @return Add/decrypt result
	 */
	private short[][] coreEncrypt(short[][] initialPTState, short[][][] roundKeys, short[][] substituteTable,
			short[][] mixColumnTable, short[][] shiftingTable) {

		//Initial round key plus, XOR operation
		short[][] state = xor(roundKeys[0], initialPTState);

		// Processing the first nine rounds
		for (int i = 0; i < 9; i++) {
			//Replace the byte of the status array with the byte at the corresponding position in the S box
			state = substituteState(state, substituteTable);

			//Row shift transformation
			state = shiftRows(state, shiftingTable);

			//Column hybrid transformation
			state = mixColumns(state, mixColumnTable);

			// Round key plus transformation
			state = xor(roundKeys[i + 1], state);
		}

		// Handling the last round
		state = substituteState(state, substituteTable);

		state = shiftRows(state, shiftingTable);

		state = xor(roundKeys[roundKeys.length - 1], state);
		return state;
	}

	/**
	 * Convert the encrypted state array into a byte array for Base64 encoding
	 * 
	 * @param finalState
	 *            State array obtained after encryption
	 * @return Byte array corresponding to the state array
	 */
	private byte[] transfer2Bytes(short[][] finalState) {
		byte[] result = new byte[finalState.length * 4];
		for (int i = 0; i < finalState.length; i++) {
			for (int j = 0; j < 4; j++) {
				result[i * 4 + j] = (byte) (finalState[i][j] & 0xff);
			}
		}
		return result;
	}

	/**
	 * Column-mixed transformation: state array and polynomial equivalence matrix for matrix multiplication on finite field GF(2)
	 * 
	 * @param state
	 *           State array
	 * @param table
	 *           Polynomial equivalence matrix
	 * @return New state after column blend transformation
	 */
	private short[][] mixColumns(short[][] state, short[][] table) {
		short[][] result = new short[state.length][4];
		for (int i = 0; i < state.length; i++) {
			result[i] = matrixMultiply(state[i], table);
		}
		return result;
	}

	/**
	 * Multiplication operation of a word and polynomial equivalent array on the finite field GF(2) multiplication between a word of a state and a
	 * irreducible polynomial <tt>C(x)=03x^3+01x^2+01^2+01x+02</tt> which is
	 * replaced as a constant table <tt>AESConstants.CX</tt> (aes-128: 4x4 x 4x1 =
	 * 4x1)
	 * 
	 * @param aWord
	 *            a word of a state
	 * @return multiplication result, a new word
	 */
	private short[] matrixMultiply(short[] aWord, short[][] table) {
		short[] result = new short[4];
		for (int i = 0; i < 4; i++) {
			result[i] = wordMultiply(table[i], aWord);
		}
		return result;
	}

	/**
	 * Multiplication operation of two words on the finite field GF(2)  multiplication between two words
	 * 
	 * @param firstWord
	 *            first operand
	 * @param secondWord
	 *            second operand
	 * @return multiplication result, a byte actually
	 */
	private short wordMultiply(short[] firstWord, short[] secondWord) {
		short result = 0;
		for (int i = 0; i < 4; i++) {
			result ^= multiply(firstWord[i], secondWord[i]);
		}
		return result;
	}

	/**
	 * The multiplication operation on the finite field GF(2) is converted into a multiplication operation on the finite field GF(2) by decomposing the operands.  multiplication in finite field
	 * GF(2^8)
	 * 
	 * @param a
	 *            an operand of this kind of multiplication
	 * @param b
	 *            another operand of this kind of multiplication
	 * @return multiplication result
	 */
	private short multiply(short a, short b) {
		short temp = 0;
		while (b != 0) {
			if ((b & 0x01) == 1) {
				temp ^= a;
			}
			a <<= 1;
			if ((a & 0x100) > 0) {
				/*
				 * judge if a is greater than 0x80, if then subtract a irreducible polynomial
				 * which can be substituted by 0x1b cause addition and subtraction are
				 * equivalent in this case it's okay to xor 0x1b
				 */
				a ^= 0x1b;
			}
			b >>= 1;
		}
		return (short) (temp & 0xff);
	}

	/**
	 *Round key extension: expands the master key of one state length into a round key array of <tt>rounds + 1</tt> state lengths generation of round keys
	 * 
	 * @param originalKey
	 *            original cipher key
	 * @return round keys
	 */
	private short[][] generateRoundKeys(short[][] originalKey) {
		short[][] roundKeys = new short[44][4];
		int keyWordCount = originalKey.length;
		// 1. copy the original cipher words into the first four words of the roundKeys
		System.arraycopy(originalKey, 0, roundKeys, 0, keyWordCount);
		// 2. extension from previous word
		for (int i = keyWordCount; i < keyWordCount * 11; i++) {
			short[] temp = roundKeys[i - 1];
			if (i % keyWordCount == 0) {
				temp = xor(substituteWord(leftShift(temp)), AESConstants.R_CON[i / keyWordCount]);
			}
			roundKeys[i] = xor(roundKeys[i - keyWordCount], temp);
		}
		return roundKeys;
	}

	/**
	 *State substitution: word substitution for each word in the state substitute value of a state array using byte as unit
	 * 
	 * @param state
	 *            state array to be substituted
	 * @return substitution result, a new state array
	 */
	private short[][] substituteState(short[][] state, short[][] substituteTable) {
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < 4; j++) {
				state[i][j] = substituteByte(state[i][j], substituteTable);
			}
		}
		return state;
	}

	/**
	 * Word substitution: byte substitution for each byte in the word substitute all bytes in a word through SBox
	 * 
	 * @param aWord
	 *            a word, aka 4 bytes
	 * @return substitution result, a new and disrupted word
	 */
	private short[] substituteWord(short[] aWord) {
		for (int i = 0; i < 4; i++) {
			aWord[i] = substituteByte(aWord[i], AESConstants.SUBSTITUTE_BOX);
		}
		return aWord;
	}

	/**
	 * Byte substitution: Take the upper four bits and the lower four bits of a word as the row number and column number of the S box respectively, and replace the original byte by the byte number in the S box by the row and column number. substitute value of a byte
	 * through <tt>SBox</tt>
	 * 
	 * @param originalByte
	 *            byte to be substituted
	 * @return substitution result, a new byte
	 */
	private short substituteByte(short originalByte, short[][] substituteTable) {
		// low 4 bits in a originByte
		int low4Bits = originalByte & 0x000f;
		// high 4 bits in a originByte
		int high4Bits = (originalByte >> 4) & 0x000f;
		// obtain value in <tt>AESConstants.SUBSTITUTE_BOX</tt>
		return substituteTable[high4Bits][low4Bits];
	}

	/**
	 * Row shift transformation: rotates the state of the row to the left, and the left shift rule is defined in <tt>shiftingTable</tt> row shifting operation,
	 * rotate over N which is defined in <tt>AESConstants.SHIFTING_TABLE</tt> bytes
	 * of corresponding rows
	 * 
	 * @param state
	 *            state array of the original plaintext
	 * @return a new state array
	 */
	private static short[][] shiftRows(short[][] state, short[][] shiftingTable) {
		short[][] result = new short[state.length][4];
		for (int j = 0; j < 4; j++) { // local byte in a word
			for (int i = 0; i < state.length; i++) { // local word
				result[i][j] = state[shiftingTable[i][j]][j];
			}
		}
		return result;
	}

	/**
	 * Cycle left by 1 unit in bytes, <tt>LEFT_SHIFT_TABLE</tt> determines the rules for moving loop left shift a byte in a
	 * word, LEFT_SHIFT_TABLE decides how to move
	 * 
	 * @param aWord
	 *            Operation word
	 * @return Loop left shift operation result
	 */
	private short[] leftShift(short[] aWord) {
		short[] result = new short[4];
		for (int i = 0; i < 4; i++) {
			result[i] = aWord[AESConstants.LEFT_SHIFT_TABLE[i]];
		}
		return result;
	}

	/**
	 * Convert a two-dimensional array into a three-dimensional array, which is convenient for obtaining the round key matrix of Nk x 4 by round subscript in the round transformation.  make it easier to process several
	 * specific blocks of the origin arrays
	 * 
	 * @param origin
	 *           2D round key array
	 * @return 3D round key array
	 */
	private short[][][] transfer(short[][] origin) {
		short[][][] result = new short[origin.length / 4][4][4];
		for (int i = 0; i < origin.length / 4; i++) {
			short[][] temp = new short[4][4];
			System.arraycopy(origin, i * 4, temp, 0, 4);
			result[i] = temp;
		}
		return result;
	}

	/**
	 * Convert the <tt>1 x modeSize</tt> matrix to a <tt>Nb x 4</tt> state matrix, usually used to get the initial state array of plaintext and keys transfer
	 * short[] to short[][], usually return an initial state
	 * 
	 * @param origin
	 *            origin array short[]
	 * @return transferred array
	 */
	private short[][] transfer(short[] origin) {
		short[][] result = new short[origin.length / 4][4];
		for (int i = 0; i < result.length; i++) {
			System.arraycopy(origin, i * 4, result[i], 0, 4);
		}
		return result;
	}

	/**
	 * Addition on the finite field GF(2), XOR operation of the <tt>modeSize</tt> bit xor corresponding byte in two state xor corresponding byte in two state
	 * arrays
	 * 
	 * @param first
	 *            first operand
	 * @param second
	 *            second operand
	 * @return xor result
	 */
	private short[][] xor(short[][] first, short[][] second) {
		short[][] result = new short[first.length][4];
		int length = first.length;
		for (short i = 0; i < length; i++) {
			for (short j = 0; j < length; j++) {
				result[i][j] = (short) (first[i][j] ^ second[i][j]);
			}
		}
		return result;
	}

	/**
	 * XOR operation between two words xor corresponding byte in two words
	 * 
	 * @param first
	 *            first operand
	 * @param second
	 *            second operand
	 * @return xor result
	 */
	private short[] xor(short[] first, short[] second) {
		short[] result = new short[4];
		for (short i = 0; i < 4; i++) {
			result[i] = (short) (first[i] ^ second[i]);
		}
		return result;
	}

	/**
	 * Convert a string to a one-dimensional short array transfer a string to short array
	 * 
	 * @param string
	 *            target string to be process
	 * @return a short array
	 */
	public static short[] transferToShorts(String string) {
		byte[] bytes = string.getBytes();
		int length = bytes.length;
		short[] shorts = new short[length];
		for (int i = 0; i < length; i++) {
			shorts[i] = bytes[i];
		}
		return shorts;
	}

	public String ascToString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append((char) data[i]);
		}
		return sb.toString();
	}

	public byte[] StringToAsc(String msg) {
		char[] message = new char[msg.length()];
		byte[] data = new byte[msg.length()];
		for (int i = 0; i < msg.length(); i++) {
			message[i] = msg.charAt(i);
		}
		for (int i = 0; i < msg.length(); i++) {
			int j = message[i];
			data[i] = (byte) j;
		}
		return data;
	}

}
