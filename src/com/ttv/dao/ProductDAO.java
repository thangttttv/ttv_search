package com.ttv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.ttv.util.Logger;

public class ProductDAO {
	private Logger logger = new Logger(this.getClass().getName());
	private int limit = 100;
	
	public List<Product> getProduct(String update_date) {
		Product product = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Product> listNews = new ArrayList<Product>();
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("SELECT 	id,title,cate_id,cate_parent_id,"
					+ "user_id,description,price,use_status,use_time,quantity,transport_fee,image,image_size,"
					+ "wish_swap,wish_cate_id,sta_comment,sta_like,sta_view,sta_transaction,"
					+ "lat,lng,STATUS,DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') as create_date,create_user,update_date,update_user,deleted FROM "
					+ " ms_product Where update_date > ?  "
					+ "ORDER BY update_date ASC   LIMIT  "+limit);
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,update_date);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				product = new Product();
				
				product.id = rs.getInt("id");
				product.title = rs.getString("title");
				product.description = rs.getString("description");
				product.image = rs.getString("image");
				product.image_size = rs.getString("image_size");	
				product.cate_id = rs.getInt("cate_id");
				product.cate_parent_id = rs.getInt("cate_parent_id");
				product.user_id = rs.getInt("user_id");	
				product.price = rs.getDouble("price");
				product.use_status = rs.getInt("use_status");
				product.use_time = rs.getInt("use_time");
				product.quantity = rs.getInt("quantity");
				product.wish_swap = rs.getString("wish_swap");
				product.wish_cate_id = rs.getString("wish_cate_id");
				product.sta_comment = rs.getInt("sta_comment");
				product.sta_like = rs.getInt("sta_like");
				product.sta_view = rs.getInt("sta_view");
				product.sta_transaction = rs.getInt("sta_transaction");
				
				product.lat = rs.getDouble("lat");
				product.lng = rs.getDouble("lng");
				product.status = rs.getInt("STATUS");
				product.deleted = rs.getInt("deleted");
				
				product.create_user = rs.getString("create_user");	
				product.create_date = rs.getString("create_date");	
				product.update_date = rs.getString("update_date");	
				
				
				listNews.add(product);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getProduct: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getProduct: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getProduct: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	public List<Product> getProductBeginID(int id_begin,int limit) {
		Product product = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		List<Product> listNews = new ArrayList<Product>();
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("SELECT 	id,title,cate_id,cate_parent_id,"
					+ "user_id,description,price,use_status,use_time,quantity,transport_fee,image,image_size,"
					+ "wish_swap,wish_cate_id,sta_comment,sta_like,sta_view,sta_transaction,"
					+ "lat,lng,STATUS,DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') as create_date,create_user,update_date,update_user,deleted FROM "
					+ " ms_product Where id > ?  "
					+ "ORDER BY id ASC   LIMIT  "+limit);
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,id_begin);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				product = new Product();
				
				product.id = rs.getInt("id");
				product.title = rs.getString("title");
				product.description = rs.getString("description");
				product.image = rs.getString("image");
				product.image_size = rs.getString("image_size");	
				product.cate_id = rs.getInt("cate_id");
				product.cate_parent_id = rs.getInt("cate_parent_id");
				product.user_id = rs.getInt("user_id");	
				product.price = rs.getDouble("price");
				product.use_status = rs.getInt("use_status");
				product.use_time = rs.getInt("use_time");
				product.quantity = rs.getInt("quantity");
				product.wish_swap = rs.getString("wish_swap");
				product.wish_cate_id = rs.getString("wish_cate_id");
				product.sta_comment = rs.getInt("sta_comment");
				product.sta_like = rs.getInt("sta_like");
				product.sta_view = rs.getInt("sta_view");
				product.sta_transaction = rs.getInt("sta_transaction");
				
				product.lat = rs.getDouble("lat");
				product.lng = rs.getDouble("lng");
				product.status = rs.getInt("STATUS");
				product.deleted = rs.getInt("deleted");
				
				product.create_user = rs.getString("create_user");	
				product.create_date = rs.getString("create_date");	
				product.update_date = rs.getString("update_date");	
				
				
				listNews.add(product);
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getProductBeginID: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getProductBeginID: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getProductBeginID: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
	
	public int getIdProductLastWeek() {
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		Connection conn = null;
		Calendar calendar = Calendar.getInstance();
		Date date = new Date(calendar.getTime().getTime()-(24*60*60*1000*7));
		int id = 0;
		String pattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("SELECT 	id,title,cate_id,cate_parent_id,"
					+ "user_id,description,price,use_status,use_time,quantity,transport_fee,image,image_size,"
					+ "wish_swap,wish_cate_id,sta_comment,sta_like,sta_view,sta_transaction,"
					+ "lat,lng,STATUS,DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%s') as create_date,create_user,update_date,update_user,deleted FROM "
					+ " ms_product Where create_date >= ?  "
					+ "ORDER BY id ASC   LIMIT  1");
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setString(1,simpleDateFormat.format(date));
			
			rs = preStmt.executeQuery();
			if (rs.next()) {				
				id = rs.getInt("id");
				
			}
			
			System.out.println(id+"-"+simpleDateFormat.format(date));
		
		} catch (NoSuchElementException nse) {
			logger.error("getIdProductLastWeek: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getIdProductLastWeek: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getIdProductLastWeek: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt,rs);
		}
		return id;
	}
	
	
	
	
	
	public User getUserById(int user_id) {
		User user = null;
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		ResultSet rs = null;
		
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("SELECT * FROM ms_user WHERE id = ?");
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,user_id);
			
			rs = preStmt.executeQuery();
			while (rs.next()) {				
				user = new User();
				user.id = rs.getInt("id");
				user.fullname = rs.getString("fullname");
				user.email = rs.getString("email");
				user.mobile = rs.getString("mobile");
				user.sex = rs.getInt("sex");	
				user.avatar_url = rs.getString("avatar_url");
			}
		
		} catch (NoSuchElementException nse) {
			logger.error("getUserById: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getUserById: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getUserById: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt,rs);
		}
		return user;
	}
	
	public boolean  saveProductRecommendSwap(int product_id, int user_id, String recommend_swap) {
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		boolean kq = false;
		
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("INSERT INTO vtc_swaphub.ms_product_recommend_swap "
					+ "(product_id, user_id, recommend_swap, create_date, update_date) VALUES (?,?,?,NOW(),NOW())");
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,product_id);
			preStmt.setInt(2,user_id);
			preStmt.setString(3,recommend_swap);

			kq = preStmt.execute();
			
		} catch (NoSuchElementException nse) {
			logger.error("saveProductRecommendSwap: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("saveProductRecommendSwap: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("saveProductRecommendSwap: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt);
		}
		return kq;
	}
	
	public boolean  deleteProductRecommendSwap(int product_id) {
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		boolean kq = false;
		
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("DELETE  FROM ms_product_recommend_swap "
					+ "  WHERE  product_id = ? ");
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(1,product_id);
			

			kq = preStmt.execute();
			
		} catch (NoSuchElementException nse) {
			logger.error("deleteProductRecommendSwap: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("deleteProductRecommendSwap: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("deleteProductRecommendSwap: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt);
		}
		return kq;
	}
	
	public boolean  updateProductRecommendSwap(int product_id, int user_id, String recommend_swap) {
		PreparedStatement preStmt = null;
		StringBuffer strSQL = null;
		boolean kq = false;
		
		Connection conn = null;
		try {
			conn = SwapPool.getConnection();
			strSQL = new StringBuffer("UPDATE  vtc_swaphub.ms_product_recommend_swap "
					+ " SET  recommend_swap = ?,update_date=NOW() WHERE  product_id = ? ");
			System.out.println(strSQL);
			preStmt = conn.prepareStatement(strSQL.toString());
			preStmt.setInt(2,product_id);
			preStmt.setString(1,recommend_swap);

			kq = preStmt.execute();
			
		} catch (NoSuchElementException nse) {
			logger.error("updateProductRecommendSwap: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("updateProductRecommendSwap: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("updateProductRecommendSwap: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt);
		}
		return kq;
	}
	
	public static void main(String[] args) {
		ProductDAO productDAO = new ProductDAO();
		productDAO.getIdProductLastWeek();
	}
}
