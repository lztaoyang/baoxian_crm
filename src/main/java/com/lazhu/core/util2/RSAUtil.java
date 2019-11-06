package com.lazhu.core.util2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import sun.misc.*;

/**
 * RSA加密解密工具类
 * 
 * @author naxj
 * @see 使用方法参看test*方法 主要test3()
 */
public class RSAUtil {

	/**
	 * 公钥 生产环境部署前此变量需与私钥同时重新生成
	 */
	private static final String PUBLIC_KEY_STRING = "";
	/**
	 * 私钥 生产环境此变量动态加载
	 */
	private static String PRIVATE_KEY_STRING = "";

	/**
	 * 设置密钥
	 * 
	 * @param privateKeyString
	 * @return true/false 传入密钥字符串是否合法
	 */
	public static Boolean setPrivateKey(String privateKeyString) {
		try {
			Object pri = RSAUtil.createPrivateKeyByKeyString(privateKeyString);
			if (pri == null) {
				return Boolean.valueOf(false);
			}
		} catch (Exception e) {
			return Boolean.valueOf(false);
		}
		RSAUtil.PRIVATE_KEY_STRING = privateKeyString;
		return Boolean.valueOf(true);
	}

	/**
	 * 生成公钥和私钥
	 * 
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public static HashMap<String, Object> getKeys()
			throws NoSuchAlgorithmException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(512);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		map.put("public", publicKey);
		map.put("private", privateKey);
		return map;
	}

	/**
	 * 得到密钥字符串（经过base64编码）
	 * 
	 * @return
	 */
	public static String getKeyString(Key key) throws Exception {
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
	public static RSAPublicKey createPublicKeyByKeyString(String keyString)
			throws Exception {
		byte[] keyBytes = new BASE64Decoder().decodeBuffer(keyString);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return (RSAPublicKey) publicKey;
	}

	/**
	 * 生成私钥对象
	 * 
	 * @param keyString
	 *            密钥字符串（经过base64编码）
	 * @return
	 */
	public static RSAPrivateKey createPrivateKeyByKeyString(String keyString)
			throws Exception {
		byte[] keyBytes = new BASE64Decoder().decodeBuffer(keyString);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return (RSAPrivateKey) privateKey;
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
	public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
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
	public static String encryptByPublicKeyString(String data, String pKeyString)
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
	public static String encrypt(String data) throws Exception {
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
	 * 私钥解密
	 * 
	 * @param data
	 * @param privateKey
	 *            私钥对象
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data,
			RSAPrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		// System.err.println(bcd.length);
		// 如果密文长度大于模长则要分组解密
		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr));
		}
		return ming;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param pKeyString
	 *            私钥字符串 （经过base64编码）
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKeyString(String data,
			String pKeyString) throws Exception {
		RSAPrivateKey privateKey = createPrivateKeyByKeyString(pKeyString);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		// System.err.println(bcd.length);
		// 如果密文长度大于模长则要分组解密
		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr));
		}
		return ming;
	}

	// 20160313后的密钥
	private static String key = null;

	private static String readPribKey() {
		if (key == null || "".equals(key)) {
			String pubKeyFile = RSAUtil.class.getResource(".").getFile()
					+ "prikey.d";
			// if (pubKeyFile.startsWith("/")){
			// pubKeyFile = pubKeyFile.substring(1);
			// }
			key = readTxtFile(pubKeyFile);
		}
		return key;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data) throws Exception {
		String pubkey = readPribKey();
		String ming = "";
		try {
			ming = decryptInner(pubkey, data);
		} catch (Exception e) {
			ming = decryptInner(PRIVATE_KEY_STRING, data);
		}
		return ming;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decryptInner(String pubKey, String data)
			throws Exception {

		String privateKeyStringMM = pubKey;// mw[0] + "\r\n" + mw[1] + "\r\n" +
											// mw[2] + "\r\n" + mw[3] + "\r\n" +
											// mw[4] + "\r\n" + mw[5] + "\r\n" +
											// mw[6];
		privateKeyStringMM = privateKeyStringMM.replace(" ", "+");
		RSAPrivateKey privateKey = createPrivateKeyByKeyString(privateKeyStringMM);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		// System.err.println(bcd.length);
		// 如果密文长度大于模长则要分组解密
		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr));
		}
		return ming;
	}

	/**
	 * ASCII码转BCD码
	 * 
	 */
	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	public static byte asc_to_bcd(byte asc) {
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
	public static String bcd2Str(byte[] bytes) {
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
	public static String[] splitString(String string, int len) {
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
	public static byte[][] splitArray(byte[] data, int len) {
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
	 * 生成公私钥
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	public static void creatKeyPair(String filePath) throws Exception {
		HashMap<String, Object> map = RSAUtil.getKeys();
		// 生成公钥和私钥
		RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
		RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
		// 得到密钥的base64编码字符串
		String pubKeyStr = RSAUtil.getKeyString(publicKey);
		String priKeyStr = RSAUtil.getKeyString(privateKey);
		FileUtil.writeFile(filePath + "/private.key", priKeyStr);
		FileUtil.writeFile(filePath + "/public.key", pubKeyStr);
	}

	private static String readTxtFile(String filePath) {
		String result = "";
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);

				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					result = result + "\r\n" + lineTxt;
					// System.out.println(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 测试生成密钥对
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void test2() throws Exception {
		HashMap<String, Object> map = RSAUtil.getKeys();
		// 生成公钥和私钥
		RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
		RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
		// 得到密钥的base64编码字符串
		String pubKeyStr = RSAUtil.getKeyString(publicKey);
		String priKeyStr = RSAUtil.getKeyString(privateKey);
		System.err.println("公钥:" + pubKeyStr);
		System.err.println("私钥:" + priKeyStr);
	}
	
	/**
	 * 测试加密解密
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void test3() throws Exception {
		String privateKeyString = "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAtHRMOpCJXFgzINS6xyL72axPrZhN"
				+ "\r\n"
				+ "mtOQROU/RTlcUa21NVxQx2bC1AeB7Q6ncqQdeE4IsIKlgX5aFMgjY2jU9QIDAQABAkEAhqfh2jHQ"
				+ "\r\n"
				+ "zl/ihzU2yzKHzL6QFnH4Nvh4R3Dp7bYH9cnu/wvc3XJZVLlUGYgm90nh0kbL59pUiOCUE5/9QOI6"
				+ "\r\n"
				+ "AQIhAPDEo11gSnepmptdaozK8GPwp4LcbMdBVlWalqTv64hRAiEAv97YtzegAZdLT2iWqJ/TFeQS"
				+ "\r\n"
				+ "aPCSWTABrFgVWOyE/WUCIGejHPbqpeNQXD6YPW8RQ9yhEFnh6+jxFOqQOlwQ8f+hAiEAmS2u9G7L"
				+ "\r\n"
				+ "JhoEPMwx40NdgQ+JBxBwthVWcKINwOVqsuUCIQCmVcl1VIvBQ0qlyacwURxtPvdliB9T/uBYXiOI"
				+ "\r\n" + "xyZrmA==";

		// 设置密钥
		Boolean privateFlag = RSAUtil.setPrivateKey(privateKeyString);
		// 明文
		String resource = "18627185716";
		String target = null;
		String resource2 = null;
		// 加密
		target = RSAUtil.encrypt(resource);
		// 解密
		resource2 = RSAUtil.decrypt(target);
		System.err.println("私钥合法?:" + privateFlag.toString());
		System.err.println("明文:" + resource);
		System.err.println("密文:" + target);
		System.err.println("解密明文:" + resource2);
	}

	/**
	 * 测试设置官钥
	 * 
	 * @throws Exception
	 */
	public static void test4() throws Exception {
		String privateKeyString = "";
		if (RSAUtil.setPrivateKey(privateKeyString)) {
			System.err.println("私钥合法");
		} else {
			System.err.println("私钥不合法");
		}
	}


	/**
	 * 测试全部
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void test1() throws Exception {
		// 明文
		String resource = "15114553381";
		String target = null;
		String resource2 = null;
		HashMap<String, Object> map = RSAUtil.getKeys();
		// 生成公钥和私钥
		RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
		RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
		// 得到密钥的base64编码字符串
		String pubKeyStr = RSAUtil.getKeyString(publicKey);
		String priKeyStr = RSAUtil.getKeyString(privateKey);
		// 加密
		target = RSAUtil.encryptByPublicKeyString(resource, pubKeyStr);
		// 解密
		resource2 = RSAUtil.decryptByPrivateKeyString(target, priKeyStr);
		System.err.println("公钥:" + pubKeyStr);
		System.err.println("私钥:" + priKeyStr);
		System.err.println("明文:" + resource);
		System.err.println("密文:" + target);
		System.err.println("解密明文:" + resource2);

	}
	
	public static void main(String[] s) throws Exception {
		//creatKeyPair("d:/dkwg");
		test1();
	}
}
