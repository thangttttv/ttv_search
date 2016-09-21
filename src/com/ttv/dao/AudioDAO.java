package com.ttv.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.ttv.util.Logger;
import com.ttv.util.UTF8Tool;
import com.ttv.util.VietnameseUtil;

public class AudioDAO {
	
	private Logger logger = new Logger(this.getClass().getName());
	private int limit = 100;
	
	public List<Audio> getAudio(int update_date) {
		Audio audio = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Audio> listNews = new ArrayList<Audio>();
		Connection conn = null;
		try {
			conn = KKTPool.getConnection();
			strSQL = new StringBuffer("SELECT 	id,title,cat_id,author,image,reader,c_listen,c_chapter, "
					+ "c_like,c_dislike,c_download,hit,description,price,STATUS,"
					+ "create_date,update_date,create_user,update_user "
					+ "FROM vtc_kenhkiemtien.c_story_audio  " +
					  "  WHERE update_date > ? AND STATUS = 1 ORDER BY update_date ASC   LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				audio = new Audio();
				
				audio.id = rs.getInt("id");
				audio.title = rs.getString("title");
				audio.author = rs.getString("author");
				audio.description = rs.getString("description");
				audio.image = rs.getString("image");	
				audio.cat_id = rs.getInt("cat_id");
				audio.reader = rs.getString("reader");
				audio.c_listen = rs.getInt("c_listen");	
				audio.c_chapter = rs.getInt("c_chapter");
				audio.c_like = rs.getInt("c_like");
				audio.c_dislike = rs.getInt("c_dislike");
				audio.hit = rs.getInt("hit");
				audio.create_user = rs.getString("create_user");	
				audio.create_date = rs.getInt("create_date");	
				audio.update_date = rs.getInt("update_date");	
				audio.status = rs.getInt("STATUS");
				
				listNews.add(audio);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getGameAndroid: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getGameAndroid: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getGameAndroid: Error executing >>>" + e.toString());
		}  finally{
			KKTPool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	public List<Audio> getAudioFile() {
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Audio> listNews = new ArrayList<Audio>();
		Connection conn = null;
		try {
			conn = KKTPool.getConnection();
			strSQL = new StringBuffer("SELECT 	*  FROM vtc_kenhkiemtien.c_story_audio_file   ");
			String strSQLUpdate ="Update  vtc_kenhkiemtien.c_story_audio_file SET file = ? Where id = ? ";
			
			preStmt = conn.prepareStatement(strSQL.toString());
			
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				
				String file = rs.getString("file");
				int id = rs.getInt("id");
				if(VietnameseUtil.containVietnameseCharacter(file)||file.indexOf(" ")>0){
					
					Date create_date = new Date(rs.getLong("create_date")*1000);
					String pattern = "/yyyy/MMdd/";
				    SimpleDateFormat format = new SimpleDateFormat(pattern);
				    
				      String date = format.format(create_date);
				      System.out.println(id);
				      System.out.println(date);
				      System.out.println(file+"-->"+UTF8Tool.coDau2KoDau(file.replace(" ", "_")));
				      String filenew = UTF8Tool.coDau2KoDau(file.replace(" ", "_"));
				      
				      String fileName_older = "/home/kktien/domains/kenhkiemtien.com/public_html/kenhkiemtien.com/upload/audio"+date+file;
				      String fileName_new = "/home/kktien/domains/kenhkiemtien.com/public_html/kenhkiemtien.com/upload/audio"+date+filenew;
				      System.out.println(fileName_new);
				    
				      File oFile = new File(fileName_older);
				      oFile.renameTo( new File(fileName_new));
				      
				     
				      
				      strSQLUpdate ="Update  vtc_kenhkiemtien.c_story_audio_file SET file = ? Where id = ?   ";
						
					  preStmt = conn.prepareStatement(strSQLUpdate);
					  preStmt.setString(1,filenew);
					  preStmt.setInt(2,id);
					  preStmt.executeUpdate();
				}
				
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getGameAndroid: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getGameAndroid: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getGameAndroid: Error executing >>>" + e.toString());
		}  finally{
			KKTPool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	public static void main(String args[]){
		AudioDAO audioDAO = new AudioDAO();
		audioDAO.getAudioFile();
	}
}
