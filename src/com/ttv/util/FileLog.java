package com.ttv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileLog {
	public static FileWriter fileWriter = null;
	public static String file="";
	
	public static void createFileLog(String file)
	{
		try {
			FileLog.file=file;			
			fileWriter = new  FileWriter(FileLog.file,false);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	public static String read() throws Exception { 		
		FileReader fr = new FileReader(file); 	
		BufferedReader br = new BufferedReader(fr); 
		String content="",s=""; 
		while((s = br.readLine()) != null) { 
			content += s;
		} 
		fr.close();		
		return content;
	} 
	
	public static void writer(String log) throws Exception {
		fileWriter.write(log+"\r\n");
		fileWriter.flush();
		fileWriter.close();
	} 
	
	public static void delete() throws Exception {
		File fileD = new File(file);
		fileD.delete();
	} 
	
	public static void main(String[] args) {
		try {
		//	FileLog.createFileLog("d:/log.txt");
		//	FileLog.writer("Tran The Thang \n");
			FileLog.read();
			} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
}
