package com.ttv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.ttv.dao.Game;
import com.ttv.dao.GameStorePool;
import com.ttv.util.Logger;

public class GameStoreDAO {
	
	private Logger logger = new Logger(this.getClass().getName());
	private int limit = 500;
	
	public List<Game> getGameAndroid(String update_date) {
		Game game = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Game> listNews = new ArrayList<Game>();
		Connection conn = null;
		try {
			conn = GameStorePool.getConnection();
			strSQL = new StringBuffer("SELECT id,bundle_id,name,description,icon,category_id,publisher_id,publisher_name,version_android"
					+ ",version_os_android,size_android,count_android_download,count_android_view,count_review,mark,tags,is_hot,create_date,update_date,status FROM g_game " +
					" WHERE update_date > ?  And is_android = 1 And status = 1 ORDER BY update_date ASC LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				game = new Game();
				
				game.id = rs.getInt("id");
				game.name = rs.getString("name");
				game.description = rs.getString("description");
				game.icon = rs.getString("icon");	
				game.category_id = rs.getInt("category_id");
				game.publisher_id = rs.getInt("publisher_id");
				game.publisher_name = rs.getString("publisher_name");	
				game.version_android = rs.getString("version_android");	
				game.version_os_android = rs.getString("version_os_android");	
				game.size_android = rs.getDouble("size_android");	
				game.count_android_download = rs.getInt("count_android_download");
				game.count_android_view = rs.getInt("count_android_view");
				game.count_review = rs.getInt("count_review");
				game.mark = rs.getDouble("mark");
				
					
				game.tags = rs.getString("tags");	
				game.is_hot = rs.getInt("is_hot");
				game.update_date = rs.getString("update_date");	
				game.create_date = rs.getString("create_date");	
				
				game.status = rs.getInt("status");
				
				listNews.add(game);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getGameAndroid: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getGameAndroid: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getGameAndroid: Error executing >>>" + e.toString());
		}  finally{
			GameStorePool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	
	public List<Game> getGameIOS(String update_date) {
		Game game = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Game> listNews = new ArrayList<Game>();
		Connection conn = null;
		try {
			conn = GameStorePool.getConnection();
			strSQL = new StringBuffer("SELECT id,bundle_id,name,description,icon,category_id,publisher_id,publisher_name,version_ios"
					+ ",version_os_ios,size_ios,count_ios_download,count_ios_view,count_review,mark,tags,is_hot,"
					+ "create_date,update_date,status FROM g_game " +
					" WHERE update_date > ?  And is_ios = 1 And status = 1 ORDER BY update_date ASC    LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				game = new Game();
				
				game.id = rs.getInt("id");
				game.name = rs.getString("name");
				game.description = rs.getString("description");
				game.icon = rs.getString("icon");	
				game.category_id = rs.getInt("category_id");
				game.publisher_id = rs.getInt("publisher_id");
				game.publisher_name = rs.getString("publisher_name");	
				game.version_ios = rs.getString("version_ios");	
				game.version_os_ios = rs.getString("version_os_ios");	
				game.size_ios = rs.getDouble("size_ios");	
				game.count_ios_download = rs.getInt("count_ios_download");
				game.count_ios_view = rs.getInt("count_ios_view");
				game.count_review = rs.getInt("count_review");
				game.mark = rs.getDouble("mark");
				
				game.tags = rs.getString("tags");	
				game.is_hot = rs.getInt("is_hot");
				
				game.update_date = rs.getString("update_date");	
				game.create_date = rs.getString("create_date");	
				game.status = rs.getInt("status");
				
				listNews.add(game);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getGameAndroid: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getGameAndroid: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getGameAndroid: Error executing >>>" + e.toString());
		}  finally{
			GameStorePool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	public static void main(String args[]){
		GameStoreDAO gameDAO = new  GameStoreDAO();
		gameDAO.getGameAndroid("2014-07-10 00:00:00");
	}
	
}
