package com.ttv.util;

import java.io.File;
import java.util.Calendar;


public class IndexLogger {
	
	public static String logPath;
	public static String pidPath;
	
	public static void writeLog(String last_time)
	{
		try {
			FileLog.createFileLog(logPath);
			FileLog.writer(last_time);
			} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	public static String readLog()
	{
		String log = "";
		try {
		 File file = new File(logPath);
		 if(file.exists())
		 {
			 FileLog.file = logPath;
			 log = FileLog.read();
			
		 }
		} catch (Exception e) {
			System.out.println("Khoi Tao Log");
		}
		return log;
	}
	
	public static void createPID()
	{
		try {
		 FileLog.createFileLog(pidPath);
		 FileLog.writer(Calendar.getInstance().getTimeInMillis()+"");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Khoi Tao Log PID");
		}
	}
	
	public static void deletePID()
	{
		try {
		 FileLog.file = pidPath;
		 FileLog.delete();
		} catch (Exception e) {
			System.out.println("Xoa Log PID");
		}
	}
	
	public static boolean existPID()
	{
		boolean exist = false;
		try {
			 FileLog.file = pidPath;
			 String log = FileLog.read();
			 long id = Long.parseLong(log);
			 if(id>0) exist = true;
		} catch (Exception e) {
			System.out.println("Khoi Tao Log PID");
		}
		return exist;
	}
	
}
