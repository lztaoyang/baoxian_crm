package com.lazhu.core.util2;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * RSA加密解密工具类
 * 
 * @author naxj
 * @see 使用方法参看test*方法 主要test3()
 */
public class RSAEncodeUtil {

	private static RSAEncodeUtil rsaEncode = null;

	private RSAEncodeUtil() {
	}

	private static void init() {
		rsaEncode = new RSAEncodeUtil();
	}

	public static RSAEncodeUtil getInstance() {
		if (rsaEncode == null || PUBLIC_KEY_STRING == null
				|| "".equals(PUBLIC_KEY_STRING)) {
			init();
		}
		return rsaEncode;
	}

	/**
	 * 公钥 生产环境部署前此变量需与私钥同时重新生成
	 */
	private static String PUBLIC_KEY_STRING = "";

	public void setPublicKey(String filePath) {
		String pubKey = "";
		try {
			String pubkey = FileUtil.getFileString(filePath);
			RSAPublicKey key = createPublicKeyByKeyString(pubkey);
			if (key != null) {
				pubKey = pubkey;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		PUBLIC_KEY_STRING = pubKey;
	}

	/**
	 * 得到密钥字符串（经过base64编码）
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		String s = (new BASE64Encoder()).encode(keyBytes);
		return s;
	}

	/**
	 * 生成公钥对象
	 * 
	 * @param keyString
	 *            密钥字符串（经过base64编码）
	 * @return
	 */
	private RSAPublicKey createPublicKeyByKeyString(String keyString)
			throws Exception {
		byte[] keyBytes = new BASE64Decoder().decodeBuffer(keyString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return (RSAPublicKey) publicKey;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param publicKey
	 *            公钥对象
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private String encryptByPublicKey(String data, RSAPublicKey publicKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		// System.err.println("分片加密："+datas.length);
		String mi = "";
		// 如果明文长度大于模长-11则要分组加密
		for (String s : datas) {
			mi += bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param pKeyString
	 *            公钥字符串 （经过base64编码）
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private String encryptByPublicKeyString(String data, String pKeyString)
			throws Exception {
		RSAPublicKey publicKey = createPublicKeyByKeyString(pKeyString);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		// System.err.println("分片加密："+datas.length);
		String mi = "";
		// 如果明文长度大于模长-11则要分组加密
		for (String s : datas) {
			mi += bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String encrypt(String data) throws Exception {
		RSAPublicKey publicKey = createPublicKeyByKeyString(PUBLIC_KEY_STRING);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		// System.err.println("分片加密："+datas.length);
		String mi = "";
		// 如果明文长度大于模长-11则要分组加密
		for (String s : datas) {
			mi += bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}

	/**
	 * ASCII码转BCD码
	 * 
	 */
	@SuppressWarnings("unused")
	private byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	private byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	/**
	 * BCD转字符串
	 */
	private String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	/**
	 * 拆分字符串
	 */
	private String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}

	/**
	 * 拆分数组
	 */
	@SuppressWarnings("unused")
	private byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

	/**
	 * 公钥加密
	 */
	public String rsaEncode(String data) throws Exception {
		return encrypt(data);
	}

}
