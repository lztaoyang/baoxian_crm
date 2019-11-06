package com.lazhu.ecp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.apache.commons.io.FilenameUtils;

public class FileTool {

	/**
	 * 图片的后缀
	 */
	public static final String[] image_ext = new String[] { "jpg", "jpeg",
			"gif", "png", "bmp" };

	/**
	 * 是否是支持的图片类型
	 * 
	 * @param type
	 *            文件类型
	 * @param fileExt
	 *            文件格式
	 * @return
	 */
	public static boolean isSupportExtension(String fileType, String fileExt) {
		if ("image".equals(fileType)) {
			for (int i = 0; i < image_ext.length; i++) {
				if (image_ext[i].equals(fileExt.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getUploadPath(String webType) {
		String uploadPath = "/u/";
		String[] paths = webType.split("_");
		for (int i = 0; i < paths.length; i++) {
			uploadPath = uploadPath + paths[i];
			if (i < paths.length - 1) {
				uploadPath = uploadPath + "/";
			}
		}
		return uploadPath;
	}

	/*
	 * public static String generateFilename(String webType, String fileExt) {
	 * DateFormat MONTH_FORMAT = new SimpleDateFormat("/yyyyMM/ddHHmmss");
	 * String path = getUploadPath(webType); return path +
	 * MONTH_FORMAT.format(new Date()) + RandomStringUtils.random(4,
	 * Num62.N36_CHARS) + "." + fileExt; }
	 */

	public static File getUniqueFile(final File file) {
		if (!file.exists())
			return file;

		File tmpFile = new File(file.getAbsolutePath());
		File parentDir = tmpFile.getParentFile();
		int count = 1;
		String extension = FilenameUtils.getExtension(tmpFile.getName());
		String baseName = FilenameUtils.getBaseName(tmpFile.getName());
		do {
			tmpFile = new File(parentDir, baseName + "(" + count++ + ")."
					+ extension);
		} while (tmpFile.exists());
		return tmpFile;
	}

	/**
	 * 将文本写入指定的文件
	 * 
	 * @param path
	 *            写入路径
	 * @param content
	 *            写入内容
	 */
	public static void writeToFile(String path, String content) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter(file, true);
			pw = new PrintWriter(fw, true);
			pw.println(content);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static long getFileSizes(File f) {// 取得文件大小
		long s = 0;
		try {
			if (f.exists()) {
				FileInputStream fis = null;
				fis = new FileInputStream(f);
				s = fis.available();
			} else {
				f.createNewFile();
				System.out.println("文件不存在");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	// 递归
	public static long getFileSize(File f) {// 取得文件夹大小
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getlist(File f) {// 递归求取目录文件个数
		long size = 0;
		File flist[] = f.listFiles();
		size = flist.length;
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getlist(flist[i]);
				size--;
			}
		}
		return size;

	}

	public static void main(String args[]) {
		FileTool g = new FileTool();
		long startTime = System.currentTimeMillis();
		try {
			long l = 0;
			String path = "C:\\WINDOWS";
			File ff = new File(path);
			if (ff.isDirectory()) { // 如果路径是文件夹的时候
				System.out.println("文件个数           " + g.getlist(ff));
				System.out.println("目录");
				l = g.getFileSize(ff);
				System.out.println(path + "目录的大小为：" + g.FormetFileSize(l));
			} else {
				System.out.println("     文件个数           1");
				System.out.println("文件");
				l = g.getFileSizes(ff);
				System.out.println(path + "文件的大小为：" + g.FormetFileSize(l));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("总共花费时间为：" + (endTime - startTime) + "毫秒...");
	}

}
