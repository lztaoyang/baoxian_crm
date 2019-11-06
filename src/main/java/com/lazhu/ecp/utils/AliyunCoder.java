package com.lazhu.ecp.utils;

import com.tg.secrtekey.Decoder;
import com.tg.secrtekey.Encoder;
import com.tg.secrtekey.Model;

/**
 * Date:     2018年9月19日 下午12:15:13
 * @author   hz
 */
public class AliyunCoder {

	//1:首先帮定义全局常量
	//开发模式 public static int MODEL_DEV = 1;
	//生产模式 public static int MODEL_PRODUCE =2;
    public static final Decoder DECODER = new Decoder(Model.MODEL_PRODUCE);
	public static final Encoder ENCODER = new Encoder(Model.MODEL_PRODUCE);
	//2：使用
	//String sec = ENCODER.encode("********")
	//String dec = DECODER.decode("********")
	
	/**
	 * 加密
	 */
	public static String testSimpleEncode(String text) throws Exception {
		return ENCODER.encode(text);
	}
	
	/**
	 * 解密
	 */
	public static String testSimpleDecode(String text) throws Exception {
		return DECODER.decode(text);
	}
	
	public static void main(String[] args) throws Exception {
		String sec = ENCODER.encode("15387178640");
		System.out.println(sec);
		String dec = DECODER.decode(sec);
		System.out.println(dec);
	}
	
}

