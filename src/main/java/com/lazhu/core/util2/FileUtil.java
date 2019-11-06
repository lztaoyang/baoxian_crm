package com.lazhu.core.util2;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	public static void creatFolder(String folderName) {
		File folder = new File(folderName);
		try {
			if (!folder.exists())
				folder.mkdir();
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			e.printStackTrace();
		}
	}

	public static void creatFile(String fileName) {
		File myFilePath = new File(fileName);
		try {
			if (!myFilePath.getParentFile().exists()) {
				myFilePath.getParentFile().mkdirs();
			}
			if (!myFilePath.exists())
				myFilePath.createNewFile();
		} catch (Exception e) {
			System.out.println("新建文件操作出错");
			e.printStackTrace();
		}
	}

	public static void delFolder(String folderName) {
		File delFolderPath = new File(folderName);
		try {
			delFolderPath.delete();
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}

	public static void delFileOfFolder(String folderName) {
		File delfile = new File(folderName);
		File[] files = delfile.listFiles();
		for (int i = 0; i < files.length; i++)
			if (files[i].isDirectory())
				files[i].delete();
	}

	public static void clearFolder(String folderName) {
		File delfilefolder = new File(folderName);
		try {
			if (!delfilefolder.exists()) {
				delfilefolder.delete();
			}
			delfilefolder.mkdir();
		} catch (Exception e) {
			System.out.println("清空目录操作出错");
			e.printStackTrace();
		}
	}

	public static void writeFile(String fileName, String data) {
		File myFile = new File(fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(myFile);
			fw.write(data);
			fw.flush();
		} catch (Exception e) {
			System.out.println("文件写入内容出错");
			e.printStackTrace();

			if (fw != null)
				try {
					fw.close();
				} catch (IOException e1) {
					e.printStackTrace();
				}
		} finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static String getFileString(String fileName) {
		File file = new File(fileName);
		StringBuffer sb = new StringBuffer();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			char[] c = new char[1024];
			int i = 0;
			while ((i = fr.read(c)) > 0) {
				String str = new String(c, 0, i);
				sb.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e1) {
					e.printStackTrace();
				}
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}