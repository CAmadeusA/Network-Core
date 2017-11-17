package com.camadeusa.utility;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

public class Encryption {
	static String ALGORITHM = "Blowfish";
	private static String key = "HJI7UJRIYZ72";
	private static Base64 base = new Base64(true);
	
	public static String encrypt(String strClearText, String strKey) throws Exception {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes("UTF8"), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted = cipher.doFinal(strClearText.getBytes("UTF8"));
			strData = base.encodeAsString(encrypted);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

	public static String decrypt(String strEncrypted, String strKey) throws Exception {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes("UTF8"), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted = cipher.doFinal(base.decodeBase64(strEncrypted));
			strData = new String(decrypted);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return strData;
	}

	public static String getKey() {
		return key;
	}

}
