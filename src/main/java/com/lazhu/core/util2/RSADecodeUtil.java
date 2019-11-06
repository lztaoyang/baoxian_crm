package com.lazhu.core.util2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;

/**
 * RSA加密解密工具类
 * 
 * @author naxj
 * @see 使用方法参看test*方法 主要test3()
 */
public class RSADecodeUtil {

	private static String privateKey = "";

	private static RSADecodeUtil rsaDecode = null;

	private RSADecodeUtil() {

	}

	private static void init() {
		rsaDecode = new RSADecodeUtil();
	}

	public static RSADecodeUtil getInstance() {
		if (rsaDecode == null) {
			init();
		}
		return rsaDecode;
	}

	/**
	 * 设置密钥
	 * 
	 * @param privateKeyString
	 * @return 传入密钥
	 */
	public void setPrivateKey(String privateKeyString) {
		privateKey = privateKeyString;
	}

	/**
	 * 校验秘钥是否合法
	 */
	public Boolean autoPrivateKey(String privateKeyString) {
		try {
			Object pri = createPrivateKeyByKeyString(privateKeyString);
			if (pri == null) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 生成私钥对象
	 * 
	 * @param keyString
	 *            密钥字符串（经过base64编码）
	 * @return
	 */
	private RSAPrivateKey createPrivateKeyByKeyString(String keyString)
			throws Exception {
		byte[] keyBytes = new BASE64Decoder().decodeBuffer(keyString);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return (RSAPrivateKey) privateKey;
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
	private String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
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
	public String decryptByPrivateKeyString(String data, String pKeyString)
			throws Exception {
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
	// private static String key = null;
	// private static String readPribKey(){
	// if (key==null||"".equals(key)){
	// String priKeyFile =
	// RSAEncodeUtil.class.getResource("/").getFile()+"prikey.d";
	// if (priKeyFile.startsWith("/")){
	// priKeyFile = priKeyFile.substring(1);
	// }
	// priKeyFile = priKeyFile.replaceAll("%20", " ");
	// key = readTxtFile(priKeyFile);
	// }
	// return key;
	// }
	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String data) throws Exception {
		// String prikey = readPribKey();
		// if ("\r\ntest".equals(privateKey)) {
		// return "13812345678";
		// }
		String ming = "";
		try {
			ming = decryptInner(privateKey, data);
		} catch (Exception e) {
			// ming = decryptInner(privateKey,data);
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
	public String decryptInner(String pubKey, String data) throws Exception {

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

	@SuppressWarnings("unused")
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
	 * 加密字符串
	 */
	public String RSADecode(String str) throws Exception {
		return decrypt(str);
	}

	/**
	 * 验证是否为真的验证码
	 */
	public Boolean test(String str) throws Exception {
		RSAEncodeUtil rsaen = RSAEncodeUtil.getInstance();
		String sourStr = "15072378149";
		String enStr = rsaen.rsaEncode(sourStr);
		if (autoPrivateKey(str)) {
			if (sourStr.equals(decryptByPrivateKey(enStr,
					createPrivateKeyByKeyString(str)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 验证是否有私钥
	 */
	public Boolean hasPK() throws Exception {
		RSAEncodeUtil rsaen = RSAEncodeUtil.getInstance();
		RSADecodeUtil rsade = RSADecodeUtil.getInstance();
		String sourStr = "15072378149";
		String enStr = rsaen.rsaEncode(sourStr);
		try {
			if (sourStr.equals(rsade.RSADecode(enStr)))
				return true;
		} catch (Exception e) {
		}
		return false;
	}
}
