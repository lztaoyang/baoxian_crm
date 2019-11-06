package com.lazhu.core.util;

import java.security.MessageDigest;

public class ComplexMD5Util {


	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	private static String MD5EncodeT(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}
	/**
	 * 两次md5加密
	 * @param origin
	 * @return
	 */
	public static String MD5Encode(String origin){
		String m = MD5EncodeT(origin,"UTF-8");
		m = MD5EncodeT(new StringBuffer().append(m.substring(0, 20)).append(m).append(m.substring(20)).toString(),"UTF-8");
		return "v2_"+m;
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	//set mobile_md5 = concat('v2_',md5(concat(left(mobile_md5,20),mobile_md5,right(mobile_md5,12))))
	
	public static void main(String[] s){
		System.out.println(MD5Encode("15072378149"));
	}
}