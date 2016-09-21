package com.ttv.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
			logger.error("getGameAndroid: Error executing >>>" + nse.toString());
		} catch (SQLException se) {
			logger.error("getGameAndroid: Error executing SQL >>>"
					+ strSQL.toString() + se.toString());
		} catch (Exception e) {
			logger.error("getGameAndroid: Error executing >>>" + e.toString());
		}  finally{
			SwapPool.attemptClose(conn,preStmt,rs);
		}
		return listNews;
	}
}
