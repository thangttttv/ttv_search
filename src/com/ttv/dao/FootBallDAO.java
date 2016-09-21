package com.ttv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.ttv.util.Logger;

public class FootBallDAO {

	private Logger logger = new Logger(this.getClass().getName());
	private int limit = 100;
	
	public List<FBClub> getListClub(String update_date) {
		FBClub fbClub = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBClub> listClub = new ArrayList<FBClub>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,CODE,NAME,name_en,city,country,country_en,create_date,update_date,logo "
					+ "FROM vtc_bongda.fb_club  " +
					  "  WHERE update_date > ?  ORDER BY update_date ASC  LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbClub = new FBClub();
				
				fbClub.id = rs.getInt("id");
				fbClub.code = rs.getString("CODE");
				fbClub.name = rs.getString("NAME");
				fbClub.name_en = rs.getString("name_en");
				fbClub.city = rs.getString("city");	
				fbClub.country = rs.getString("country");
				fbClub.country_en = rs.getString("country_en");
				fbClub.update_date = rs.getString("update_date");
				fbClub.create_date = rs.getString("create_date");
				fbClub.logo = rs.getString("logo");
			
				listClub.add(fbClub);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListClub: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListClub: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListClub: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	
	public List<FBCup> getListCup(String update_date) {
		FBCup fbCup = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBCup> listClub = new ArrayList<FBCup>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,CODE,NAME,name_en,country,country_en,create_date,update_date,logo "
					+ "FROM vtc_bongda.fb_cup  " +
					  "  WHERE update_date > ?  ORDER BY update_date ASC  LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbCup = new FBCup();
				fbCup.id = rs.getInt("id");
				fbCup.code = rs.getString("CODE");
				fbCup.name = rs.getString("NAME");
				fbCup.name_en = rs.getString("name_en");
				fbCup.country = rs.getString("country");
				fbCup.country_en = rs.getString("country_en");
				fbCup.update_date = rs.getString("update_date");
				fbCup.create_date = rs.getString("create_date");
				fbCup.logo = rs.getString("logo");
				
				listClub.add(fbCup);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListCup: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListCup: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListCup: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	
	public FBCup getCup(int cup_id) {
		FBCup fbCup = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
	
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,CODE,NAME,name_en,country,country_en,create_date,update_date,rate "
					+ "FROM vtc_bongda.fb_cup  " +
					  "  WHERE id = ?  ");
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,cup_id);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbCup = new FBCup();
				fbCup.id = rs.getInt("id");
				fbCup.code = rs.getString("CODE");
				fbCup.name = rs.getString("NAME");
				fbCup.name_en = rs.getString("name_en");
				fbCup.country = rs.getString("country");
				fbCup.country_en = rs.getString("country_en");
				fbCup.update_date = rs.getString("update_date");
				fbCup.create_date = rs.getString("create_date");
				fbCup.rate = rs.getInt("rate");
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getCup: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getCup: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getCup: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return fbCup;
	}
	
	
	public List<FBFootballer> getListFootballer(String update_date) {
		FBFootballer fbFootballer = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBFootballer> listClub = new ArrayList<FBFootballer>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT f.id,f.name,f.avatar,f.country,f.create_date,f.update_date,c.name as club_name,c.name_en as club_name_en "
					+ "FROM vtc_bongda.fb_footballer f Left Join vtc_bongda.fb_club c On f.club_id = c.id  " +
					  "  WHERE f.update_date > ?  ORDER BY f.update_date ASC  LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbFootballer = new FBFootballer();
				fbFootballer.id = rs.getInt("id");
				fbFootballer.name = rs.getString("NAME");
				fbFootballer.country = rs.getString("country");
				fbFootballer.update_date = rs.getString("update_date");
				fbFootballer.create_date = rs.getString("create_date");
				fbFootballer.club_name = rs.getString("club_name");
				fbFootballer.club_name_en = rs.getString("club_name_en");
				fbFootballer.avatar = rs.getString("avatar");
			
				listClub.add(fbFootballer);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListFootballer: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListFootballer: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListFootballer: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	public List<FBCoach> getListCoach(String update_date) {
		FBCoach fbFootballer = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBCoach> listClub = new ArrayList<FBCoach>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,NAME,name_en,country,avatar,create_date,update_date FROM fb_coach"
					+ "  WHERE update_date > ?  ORDER BY update_date ASC  LIMIT  "+limit);
			System.out.println("SELECT id,NAME,name_en,country,avatar,create_date,update_date FROM fb_coach"
					+ "  WHERE update_date > '"+update_date+"'  ORDER BY update_date ASC  LIMIT  "+limit); 
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbFootballer = new FBCoach();
				fbFootballer.id = rs.getInt("id");
				fbFootballer.name = rs.getString("NAME");
				fbFootballer.name_en = rs.getString("name_en");
				fbFootballer.avatar = rs.getString("avatar");
				fbFootballer.country = rs.getString("country");
				fbFootballer.update_date = rs.getString("update_date");
				fbFootballer.create_date = rs.getString("create_date");
			
				listClub.add(fbFootballer);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListCoach: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListCoach: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListCoach: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	
	public List<FBNews> getListNews(String update_date) {
		FBNews fbFootballer = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBNews> listClub = new ArrayList<FBNews>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,title,image,description,tags,club_id,cup_id,cup_name,club_name,"
					+ "country,create_date,update_date FROM fb_news "
					+ "  WHERE update_date > ? AND  STATUS = 1  ORDER BY update_date ASC  LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbFootballer = new FBNews();
				fbFootballer.id = rs.getInt("id");
				fbFootballer.club_id = rs.getInt("club_id");
				fbFootballer.cup_id = rs.getInt("cup_id");
				fbFootballer.title = rs.getString("title");
				fbFootballer.description = rs.getString("description");
				fbFootballer.image = rs.getString("image");
				fbFootballer.country = rs.getString("country");
				fbFootballer.tags = rs.getString("tags");
				fbFootballer.cup_name = rs.getString("cup_name");
				fbFootballer.club_name = rs.getString("club_name");
				fbFootballer.update_date = rs.getString("update_date");
				fbFootballer.create_date = rs.getString("create_date");
			
				listClub.add(fbFootballer);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListNews: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListNews: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListNews: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	
	
	public List<FBVideo> getListVideo(String update_date) {
		FBVideo fbFootballer = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<FBVideo> listClub = new ArrayList<FBVideo>();
		Connection conn = null;
		try {
			conn = FootBallPool.getConnection();
			strSQL = new StringBuffer("SELECT id,title,image,video,cup_id,club_id,club_name,cup_name,description,"
					+ "create_date,update_date FROM fb_video"
					+ "  WHERE update_date > ? AND  STATUS = 1  ORDER BY update_date ASC  LIMIT  "+limit);
			
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				fbFootballer = new FBVideo();
				fbFootballer.id = rs.getInt("id");
				fbFootballer.club_id = rs.getInt("club_id");
				fbFootballer.cup_id = rs.getInt("cup_id");
				fbFootballer.title = rs.getString("title");
				fbFootballer.description = rs.getString("description");
				fbFootballer.image = rs.getString("image");
				fbFootballer.cup_name = rs.getString("cup_name");
				fbFootballer.club_name = rs.getString("club_name");
				fbFootballer.update_date = rs.getString("update_date");
				fbFootballer.create_date = rs.getString("create_date");
				fbFootballer.video = rs.getString("video");
				
			
				listClub.add(fbFootballer);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getListVideo: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getListVideo: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getListVideo: Error executing >>>" + e.toString());
		}  finally{
			FootBallPool.attemptClose(conn,preStmt,rs);
		}
		return listClub;
	}
	
	
	
}
