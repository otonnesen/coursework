/* 
 * Name: Oliver Tonnesen
 * ID: V00885732
 * Date: January 15, 2018
 * Filename: VigenereCipher.java
 * Details: CSC 115 Assignment 1
 */

public class VigenereCipher implements Cipher {

	private String key;
	public VigenereCipher(String key) {
		this.key = key;
	}

	// Method:		main
	// Description:		Used for internal testing
	// Input:
	// 	args		- Unused
	// Output:		None

	public static void main(String[] args) {

		VigenereCipher vg = new VigenereCipher("abc");

		int[] a = {};
		int[] b = {0};
		int[] c = {0,1,2,3,4,5,6,7};
		String A = "";
		String B = "testing";
		String C = "moretesting";
		String D = "keyone";
		String E = "twokey";
		String F = "abcdef";

		// System.out.println(vg.encrypt(E));

		// vg.setKey("noice");

		// System.out.println(vg.encrypt(E));
		
		// System.out.println(vg.decrypt(vg.encrypt(E)));

		// for(int i: vg.stringToIntArray(D)) {
		// 	System.out.print(i+" ");
		// }

		// System.out.println();

		// for(char i: vg.intArrayToString(c).toCharArray()) {
		// 	System.out.print(i+" ");
		// }

		// vg.dumpArray(c,"result: ");

		// System.out.println(vg.intArrayToString(c));

		vg.dumpArray(c, "result:");

	}

	// Method:		encrypt
	// Description:		Encrypts a string using a simplified Vigenere cipher. All text is limited to lower-case ASCII letters a...z.
	// Input:
	// 	plaintext	- String to be encrypted
	// Output:		Encrypted ciphertext

	public String encrypt(String plaintext) {
		char[] cipher = new char[plaintext.length()];
		for(int i = 0; i < plaintext.length(); i++) {
			cipher[i] = (char)(97 + ((26 + this.stringToIntArray(plaintext)[i] + this.stringToIntArray(this.key)[i%this.key.length()]) % 26));
		}
		return new String(cipher);
	}

	// Method:		decrypt
	// Description:		Decrypts a string using a modified Vigenere cipher. All text is limited to lower-case ASCII letters a...z.
	// Input:
	// 	ciphertext	- encrypted text to be decrypted
	// Output:		Decrypted plaintext
	
	public String decrypt(String ciphertext) {
		char[] plain = new char[ciphertext.length()];
		for(int i = 0; i < ciphertext.length(); i++) {
			plain[i] = (char)(97 + ((26 + this.stringToIntArray(ciphertext)[i] - this.stringToIntArray(this.key)[i%this.key.length()]) % 26));
		}
		return new String(plain);
	}

	// Method:		dumpArray
	// Description:		Prints out the specified text, followed immediately by the (comma-delimited) contents of the array
	// Input:
	// 	array		- Array of integers to be printed
	// 	text		- String to be printed
	// Output:		None

	private void dumpArray(int[] array, String text) {
		System.out.print(text+" ");
		for(int i = 0; i < array.length - 1; i++) {
			System.out.print(i+", ");
		}
		if(array.length > 0) {
			System.out.println(array[array.length - 1]);
		}
	}

	// Method:		intArrayToString
	// Description:		Converts an array of integers with values in the range 0...25 into a string with characters in the range a...z.
	// 			The individual letters are ordered exactly as the corresponding integer values.
	// 			For example, the value 0 in index position i of the array, matches an 'a' as the first letter in the string.
	// Input:
	// 	encodedText	- Array of integers with values between 0 and 25.
	// Output:		String of lower-case letters corresponding to integers in the given array.

	private String intArrayToString(int[] encodedText) {
		char[] tmp = new char[encodedText.length];
		for(int i = 0; i < encodedText.length; i++) {
			tmp[i] = (char)(encodedText[i]+97);
		}
		return new String(tmp);
	}

	// Method:		stringToIntArray
	// Description:		Converts a string into an int array where the values are within the range 0...25.
	// 			The values are matched, in order, to the characters in the string.
	// 			For example, the integer 0 is matched to the letter 'a' and 25 is matched to and 'z'.
	// Input:
	// 	text		- Plantext string consisting of only lower-case ASCII letters.
	// Output:		
	
	private int[] stringToIntArray(String text) {
		int[] tmp = new int[text.length()];
		for(int i = 0; i < text.length(); i++) {
			tmp[i] = (int)(text.toCharArray()[i])-97;
		}
		return tmp;
	}

	// Method:		setKey
	// Description:		Sets the key for a simplified Vigenere cipher. All text is limited to lower-case ASCII letters a...z.
	// Input:
	// 	key		- plaintext key
	// Output:		None

	public void setKey(String key) {
		this.key = key;
	}
	
}
