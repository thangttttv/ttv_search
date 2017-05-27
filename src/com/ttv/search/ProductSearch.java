package com.ttv.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.Product;
import com.ttv.dao.ProductDAO;
import com.ttv.dao.User;
import com.ttv.util.LatLngUtil;
import com.ttv.util.ProductComparator;
import com.ttv.util.ProductRecommentComparator;
import com.ttv.util.StringTool;

public class ProductSearch {
	public String index_path;
	public int totalHit;
	public static final int SORT_PRICE_ASC = 1;
	public static final int SORT_PRICE_DESC = 2;
	public static final int SORT_PRICE_NEWEST = 3;
	public static final int SORT_PRICE_NEAREST = 4;


	public ProductSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<Product> query(String keyword,int cat_id,double priceF,double priceT,double lat, double lng
			, double distance ,int order_by ,int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Product> kList = new ArrayList<Product>();
		Product product = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword.toLowerCase());
				BooleanQuery blqueryK = new BooleanQuery();	 
				 
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("title", 2.0f);
				boots.put("_title", 2.0f);
				
				/*BooleanQuery queryKeyword = new BooleanQuery();	
				
				Query queryTitleF = new PrefixQuery(new Term("title",keyword));
			    Query queryTitle_F = new PrefixQuery(new Term("_title",keyword));
			    queryKeyword.add(queryTitleF,BooleanClause.Occur.SHOULD);
			    queryKeyword.add(queryTitle_F,BooleanClause.Occur.SHOULD);
				//query.add(queryKeyword, BooleanClause.Occur.MUST);
			    blqueryK.add(queryKeyword, BooleanClause.Occur.SHOULD);*/
				keyword = keyword.trim()+"*";
				
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"title","_title"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryp.setAllowLeadingWildcard(true);
				Query queryName = queryp.parse(keyword);
				blqueryK.add(queryName, BooleanClause.Occur.SHOULD);
				query.add(blqueryK, BooleanClause.Occur.MUST);
				
			}
			
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
		    
		    if(priceF>0){
			    Query qPrice = NumericRangeQuery.newDoubleRange("price",priceF,priceT, true, true);
			    query.add(qPrice, BooleanClause.Occur.MUST);
		    }
		    
		    
		    if(cat_id>0){
		    	QueryParser queryPCate  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"cate_id","cate_parent_id"}, analyzer);
		    	queryPCate.setDefaultOperator(QueryParser.AND_OPERATOR);
		    	Query queryCat = queryPCate.parse(cat_id+"");
				query.add(queryCat, BooleanClause.Occur.MUST);
			    
		    }
		    
		    if(distance>0){
		    	 double max_lat =LatLngUtil.getMaxLatitude(lat,lng,distance);
		    	 double min_lat =LatLngUtil.getMinLatitude(lat,lng,distance);
		    	 double tem = 0;
		         if(max_lat<min_lat){
		             tem = max_lat;
		             max_lat = min_lat;
		             min_lat = tem;
		         }
		         double max_lon = LatLngUtil.getMaxLongitude(lat,lng,distance);
		         double min_lon = LatLngUtil.getMinLongitude(lat,lng,distance);
		         if(max_lon<min_lon){
		             tem = max_lon;
		             max_lon = min_lon;
		             min_lon = tem;
		         }
		         Query qLat = NumericRangeQuery.newDoubleRange("lat",min_lat,max_lat, true, true);
				 query.add(qLat, BooleanClause.Occur.MUST);
				 Query qLng = NumericRangeQuery.newDoubleRange("lng",min_lon,max_lon, true, true);
				 query.add(qLng, BooleanClause.Occur.MUST);
		    }
		    
			System.out.println("query:"+query.toString());
			
			
			
			if(order_by!=SORT_PRICE_NEAREST){
				Sort sort = new Sort(new SortField("rank_index", Type.DOUBLE, true));
				switch(order_by){
				case SORT_PRICE_ASC:
					sort = new Sort(new SortField("price", Type.DOUBLE, false));
					break;
				case SORT_PRICE_DESC:
					sort = new Sort(new SortField("price", Type.DOUBLE, true));
					break;
				case SORT_PRICE_NEWEST:
					sort = new Sort(new SortField("id", Type.INT, true));
					break;
				}
			
				TopDocs topDocs = searcher.search(query, 1000,sort);
				int begin = (page - 1) * rowpage;
				begin = begin<0?0:begin;
				int end = begin + rowpage;
				this.totalHit = topDocs.scoreDocs.length ;
				end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
				int i = begin;
				ScoreDoc scoreDoc  = null;	
				System.out.println(i);
				System.out.println(end);
				
				while (i < end) {
					scoreDoc = topDocs.scoreDocs[i];	
					product = new Product();
					product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
					product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
					product.title = searcher.doc(scoreDoc.doc).get("title");
					product.description = searcher.doc(scoreDoc.doc).get("description");
					product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
					product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
					product.image = searcher.doc(scoreDoc.doc).get("image");
					product.image_size = searcher.doc(scoreDoc.doc).get("image_size");
					
					product.price = Double.parseDouble(searcher.doc(scoreDoc.doc).get("price"));
					product.quantity = Integer.parseInt(searcher.doc(scoreDoc.doc).get("quantity"));
					product.sta_comment = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_comment"));
					product.sta_like = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_like"));
					product.sta_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_view"));
					product.create_date = searcher.doc(scoreDoc.doc).get("create_date");
					
					product.lat = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lat"));
					product.lng = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lng"));
					System.out.println(product.title+":"+product.description+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
					kList.add(product);
					i++;
				}
				return kList;
			}else{
				List<Product> productList = new ArrayList<Product>();
				TopDocs topDocs = searcher.search(query, 500);
				ScoreDoc scoreDoc  = null;
				int i = 0;
				int total = topDocs.totalHits>500?500:topDocs.totalHits;
				while (i < total) {
					scoreDoc = topDocs.scoreDocs[i];	
					product = new Product();
					product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
					product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
					product.title = searcher.doc(scoreDoc.doc).get("title");
					product.description = searcher.doc(scoreDoc.doc).get("description");
					product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
					product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
					product.image = searcher.doc(scoreDoc.doc).get("image");
					product.image_size = searcher.doc(scoreDoc.doc).get("image_size");
					
					product.price = Double.parseDouble(searcher.doc(scoreDoc.doc).get("price"));
					product.quantity = Integer.parseInt(searcher.doc(scoreDoc.doc).get("quantity"));
					product.sta_comment = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_comment"));
					product.sta_like = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_like"));
					product.sta_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_view"));
					product.create_date = searcher.doc(scoreDoc.doc).get("create_date");
					
					product.lat =Double.parseDouble(searcher.doc(scoreDoc.doc).get("lat"));
					product.lng =Double.parseDouble(searcher.doc(scoreDoc.doc).get("lng"));
					double khoangcach = LatLngUtil.getDistance(product.lat, product.lng, lat, lng);
					product.distance = khoangcach/1000;
					//System.out.println(product.title+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
					kList.add(product);
					i++;
				}
				
				Collections.sort(kList, new ProductComparator());
				
				int begin = (page - 1) * rowpage;
				begin = begin<0?0:begin;
				int end = begin + rowpage;
				this.totalHit = topDocs.scoreDocs.length ;
				end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
				i = begin;
				
				System.out.println(i);
				System.out.println(end);
				
				
				while (i < end) {
					product = kList.get(i);
					productList.add(product);
					System.out.println("sort-->"+product.title+", KC:"+product.distance+", cate_id:"+product.cate_id);
					i++;
				}
				return productList;
				
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return kList;
	}
	
	
	public List<Product> queryProductRecommend(String keywords,int user_id,double price,double lat, double lng ,int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Product> kList = new ArrayList<Product>();
		List<Product> productList = new ArrayList<Product>();
		Product product = null;
		int[] arrIndexRank = new int[20];
		int i = 0;int j = 20;
		while(i<20){
			arrIndexRank[i] = j-i;
			i++;
		}
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			String[] arrKeyword = keywords.toLowerCase().split(",");
			
			if(!StringTool.isEmptyOrNul(keywords))
			{
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				for (String keyword : arrKeyword) {
					String arrK[] = keyword.split(" ");	
					for (String itemK : arrK) {
					Query queryTitleF = new PrefixQuery(new Term("title",itemK));
				    Query queryTitle_F = new PrefixQuery(new Term("_title",itemK));
				    queryKeyword.add(queryTitleF,BooleanClause.Occur.SHOULD);
				    queryKeyword.add(queryTitle_F,BooleanClause.Occur.SHOULD);
					}
				}
				query.add(queryKeyword, BooleanClause.Occur.MUST);
			}
			
			Query qUser = NumericRangeQuery.newIntRange("user_id",user_id,user_id, true, true);
			query.add(qUser, BooleanClause.Occur.MUST_NOT);
			 
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
			System.out.println("query:"+query.toString());
			
			Sort sort = new Sort(new SortField("id", Type.INT, true));
			TopDocs topDocs = searcher.search(query, 100,sort);
			ScoreDoc scoreDoc  = null;	
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 i = 0;
			
			while (i < topDocs.scoreDocs.length) {
				scoreDoc = topDocs.scoreDocs[i];	
				product = new Product();
				product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
				product.title = searcher.doc(scoreDoc.doc).get("title");
				product.description = searcher.doc(scoreDoc.doc).get("description");
				product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
				product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
				product.image = searcher.doc(scoreDoc.doc).get("image");
				product.image_size = searcher.doc(scoreDoc.doc).get("image_size");
				
				product.price = Double.parseDouble(searcher.doc(scoreDoc.doc).get("price"));
				product.quantity = Integer.parseInt(searcher.doc(scoreDoc.doc).get("quantity"));
				product.sta_comment = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_comment"));
				product.sta_like = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_like"));
				product.sta_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_view"));
				product.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				product.lat = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lat"));
				product.lng = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lng"));
				double khoangcach = LatLngUtil.getDistance(product.lat, product.lng, lat, lng);
				product.distance = khoangcach/1000;
				
				int rank_distance = (int) (product.distance / 5) ;
				rank_distance = rank_distance >19 ? 19: rank_distance;
				
				long soNgay = 30;
				try {
					 soNgay = (Calendar.getInstance().getTimeInMillis() - formatter.parse(product.create_date).getTime())/(24*60*60*1000);	
				} catch (Exception e) {
					e.printStackTrace();
				}
				int rank_time =  (int)(soNgay>19?19:soNgay);
				
				
				int rank_price =  (int)(price - product.price)/1000000  ;
				rank_price =  rank_price>=0?rank_price:(-1*rank_price);
				rank_price = rank_price > 19? 19:rank_price ;
				
				//System.out.println(product.title+">"+rank_distance+":"+rank_price+":"+":time"+rank_time);
				
				double rank_index =arrIndexRank[rank_distance]*2+arrIndexRank[rank_time]+arrIndexRank[rank_price]*2;
				product.index = rank_index;
				kList.add(product);
				i++;
			}
			
			Collections.sort(kList, new ProductRecommentComparator());
			
			int begin = (page - 1) * rowpage;
			begin = begin<0?0:begin;
			int end = begin + rowpage;
			this.totalHit = topDocs.scoreDocs.length ;
			end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
			i = begin;
			
			System.out.println(i);
			System.out.println(end);
			
			
			while (i < end) {
				product = kList.get(i);
				productList.add(product);
				System.out.println(product.title+"-"+product.distance+"-"+product.price+"-Rank->"+product.index);
				i++;
			}
			
		} catch (IOException  e) {
			e.printStackTrace();
		}
	
		return productList;
	}
	
	public List<Product> queryProductRecommendNext(String keywords,int user_id,double price,double lat, double lng ,int page, int rowpage,int from) {
		BooleanQuery query = new BooleanQuery();	
		List<Product> kList = new ArrayList<Product>();
		List<Product> productList = new ArrayList<Product>();
		Product product = null;
		int[] arrIndexRank = new int[20];
		int i = 0;int j = 20;
		while(i<20){
			arrIndexRank[i] = j-i;
			i++;
		}
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			String[] arrKeyword = keywords.toLowerCase().split(",");
			
			if(!StringTool.isEmptyOrNul(keywords))
			{
				BooleanQuery queryKeyword = new BooleanQuery();	
				for (String keyword : arrKeyword) {
					String arrK[] = keyword.split(" ");	
					for (String itemK : arrK) {
					Query queryTitleF = new PrefixQuery(new Term("title",itemK));
				    Query queryTitle_F = new PrefixQuery(new Term("_title",itemK));
				    queryKeyword.add(queryTitleF,BooleanClause.Occur.SHOULD);
				    queryKeyword.add(queryTitle_F,BooleanClause.Occur.SHOULD);
					}
				}
				query.add(queryKeyword, BooleanClause.Occur.MUST);
				
			}
			
			Query qUser = NumericRangeQuery.newIntRange("user_id",user_id,user_id, true, true);
			query.add(qUser, BooleanClause.Occur.MUST_NOT);
			 
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
			System.out.println("query:"+query.toString());
			
			Sort sort = new Sort(new SortField("id", Type.INT, true));
			TopDocs topDocs = searcher.search(query, 100,sort);
			ScoreDoc scoreDoc  = null;	
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 i = 0;
			
			while (i < topDocs.scoreDocs.length) {
				scoreDoc = topDocs.scoreDocs[i];	
				product = new Product();
				product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
				product.title = searcher.doc(scoreDoc.doc).get("title");
				product.description = searcher.doc(scoreDoc.doc).get("description");
				product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
				product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
				product.image = searcher.doc(scoreDoc.doc).get("image");
				product.image_size = searcher.doc(scoreDoc.doc).get("image_size");
				
				product.price = Double.parseDouble(searcher.doc(scoreDoc.doc).get("price"));
				product.quantity = Integer.parseInt(searcher.doc(scoreDoc.doc).get("quantity"));
				product.sta_comment = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_comment"));
				product.sta_like = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_like"));
				product.sta_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_view"));
				product.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				product.lat = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lat"));
				product.lng = Double.parseDouble(searcher.doc(scoreDoc.doc).get("lng"));
				double khoangcach = LatLngUtil.getDistance(product.lat, product.lng, lat, lng);
				product.distance = khoangcach/1000;
				
				// 5km is unit distance
				int rank_distance = (int) (product.distance / 5) ;
				rank_distance = rank_distance >19 ? 19: rank_distance;
				
				long soNgay = 30;
				try {
					 soNgay = (Calendar.getInstance().getTimeInMillis() - formatter.parse(product.create_date).getTime())/(24*60*60*1000);	
				} catch (Exception e) {
					e.printStackTrace();
				}
				int rank_time =  (int)(soNgay>19?19:soNgay);
				
				// 1 tr is unit price
				int rank_price =  (int)(price - product.price)/1000000  ;
				rank_price =  rank_price>=0?rank_price:(-1*rank_price);
				rank_price = rank_price > 19? 19:rank_price ;
				
				double rank_index =arrIndexRank[rank_distance]*2+arrIndexRank[rank_time]+arrIndexRank[rank_price]*2;
				
				System.out.println(rank_distance+":"+rank_price+":"+product.cate_id+":time"+rank_time);
				
				product.index = rank_index;
				kList.add(product);
				i++;
			}
			
			Collections.sort(kList, new ProductRecommentComparator());
			
			int begin = (page - 1) * rowpage;
			begin = begin<0?0:begin;
			if(page==1) begin += from; 
				
			int end = begin + rowpage;
			this.totalHit = topDocs.scoreDocs.length ;
			end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
			i = begin;
			
			System.out.println(i);
			System.out.println(end);
			
			
			while (i < end) {
				product = kList.get(i);
				productList.add(product);
				System.out.println(product.title+":"+product.distance+":"+product.cate_id+":Rank"+product.index);
				i++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return productList;
	}
	
	public List<Product> querySortNearest(String keyword,int cat_id,double priceF,double priceT,double lat, double lng
			, double distance ,int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Product> kList = new ArrayList<Product>();
		List<Product> productList = new ArrayList<Product>();
		Product product = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword.toLowerCase());
				 
				 
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("title", 2.0f);
				boots.put("_title", 2.0f);
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				
				Query queryTitleF = new PrefixQuery(new Term("title",keyword));
			    Query queryTitle_F = new PrefixQuery(new Term("_title",keyword));
			    queryKeyword.add(queryTitleF,BooleanClause.Occur.SHOULD);
			    queryKeyword.add(queryTitle_F,BooleanClause.Occur.SHOULD);
				query.add(queryKeyword, BooleanClause.Occur.MUST);
			}
			
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
		    
		    if(priceF>0){
			    Query qPrice = NumericRangeQuery.newDoubleRange("price",priceF,priceT, true, true);
			    query.add(qPrice, BooleanClause.Occur.MUST);
		    }
		    
		    if(cat_id>0){
		    	QueryParser queryPCate  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"cate_id","cate_parent_id"}, analyzer);
		    	queryPCate.setDefaultOperator(QueryParser.AND_OPERATOR);
		    	Query queryCat = queryPCate.parse(cat_id+"");
				query.add(queryCat, BooleanClause.Occur.MUST);
			    
		    }
		    
		    if(distance>0){
		    	 double max_lat =LatLngUtil.getMaxLatitude(lat,lng,distance);
		    	 double min_lat =LatLngUtil.getMinLatitude(lat,lng,distance);
		    	 double tem = 0;
		         if(max_lat<min_lat){
		             tem = max_lat;
		             max_lat = min_lat;
		             min_lat = tem;
		         }
		         double max_lon = LatLngUtil.getMaxLongitude(lat,lng,distance);
		         double min_lon = LatLngUtil.getMinLongitude(lat,lng,distance);
		         if(max_lon<min_lon){
		             tem = max_lon;
		             max_lon = min_lon;
		             min_lon = tem;
		         }
		         Query qLat = NumericRangeQuery.newDoubleRange("lat",min_lat,max_lat, true, true);
				 query.add(qLat, BooleanClause.Occur.MUST);
				 Query qLng = NumericRangeQuery.newDoubleRange("lng",min_lon,max_lon, true, true);
				 query.add(qLng, BooleanClause.Occur.MUST);
		    }
		    
			System.out.println("query:"+query.toString());
			
			
			TopDocs topDocs = searcher.search(query, 500);
			ScoreDoc scoreDoc  = null;	
			int i = 0;
			while (i < topDocs.scoreDocs.length) {
				scoreDoc = topDocs.scoreDocs[i];	
				product = new Product();
				product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
				product.title = searcher.doc(scoreDoc.doc).get("title");
				product.description = searcher.doc(scoreDoc.doc).get("description");
				product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
				product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
				product.image = searcher.doc(scoreDoc.doc).get("image");
				product.image_size = searcher.doc(scoreDoc.doc).get("image_size");
				
				product.price = Double.parseDouble(searcher.doc(scoreDoc.doc).get("price"));
				product.quantity = Integer.parseInt(searcher.doc(scoreDoc.doc).get("quantity"));
				product.sta_comment = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_comment"));
				product.sta_like = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_like"));
				product.sta_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("sta_view"));
				product.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				product.lat =Double.parseDouble(searcher.doc(scoreDoc.doc).get("lat"));
				product.lng =Double.parseDouble(searcher.doc(scoreDoc.doc).get("lng"));
				double khoangcach = LatLngUtil.getDistance(product.lat, product.lng, lat, lng);
				product.distance = khoangcach;
				
				//System.out.println(product.title+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
				kList.add(product);
				i++;
			}
			
			Collections.sort(kList, new ProductComparator());
			
			int begin = (page - 1) * rowpage;
			begin = begin<0?0:begin;
			int end = begin + rowpage;
			this.totalHit = topDocs.scoreDocs.length ;
			end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
			i = begin;
			
			System.out.println(i);
			System.out.println(end);
			
			
			while (i < end) {
				product = kList.get(i);
				productList.add(product);
				System.out.println(product.title+":"+product.distance+":"+product.cate_id);
				i++;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	
		return productList;
	}
	
	public List<User> queryListUserWishProduct(String keyword, int user_id) {
		BooleanQuery query = new BooleanQuery();	
		List<User> users = new ArrayList<User>();
		Product product = null;
		ProductDAO productDAO = new ProductDAO();
		HashMap<Integer,User> mapUser = new HashMap<Integer,User>();
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword.toLowerCase());
				String arrK[] = keyword.split(" ");
				BooleanQuery queryKeyword = new BooleanQuery();	
				for (String itemK : arrK) {
					Query queryTitleF = new PrefixQuery(new Term("wish_swap",itemK));
				    queryKeyword.add(queryTitleF,BooleanClause.Occur.SHOULD);
				}
				
				query.add(queryKeyword, BooleanClause.Occur.MUST);
			}
			
			
			Query qUser = NumericRangeQuery.newIntRange("user_id",user_id,user_id, true, true);
			query.add(qUser, BooleanClause.Occur.MUST_NOT);
			
			System.out.println("query:"+query.toString());
			
			Sort sort = new Sort(new SortField("id", Type.INT, true));
			TopDocs topDocs = searcher.search(query, 30,sort);
			ScoreDoc scoreDoc  = null;	
			int i = 0;
			while (i < topDocs.scoreDocs.length) {
				scoreDoc = topDocs.scoreDocs[i];	
				product = new Product();
				product.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				product.user_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("user_id"));
				product.title = searcher.doc(scoreDoc.doc).get("title");
				product.cate_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_id"));
				product.cate_parent_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cate_parent_id"));
				
				User user = productDAO.getUserById(product.user_id);
				if(user!=null){
					mapUser.put(new Integer(user.id), user);
					System.out.println(product.title+":"+product.user_id+"-"+user.fullname+":"+product.cate_id);
				}
				i++;
			}
			
			if(mapUser.size()>0){
				Iterator<User> iterator = mapUser.values().iterator();
				while(iterator.hasNext()){
					users.add(iterator.next());
				}
			}
			
			
		} catch (IOException  e) {
			e.printStackTrace();
		}
	
		return users;
	}
	
	
	public int getTotalHit() {
		return totalHit;
	}
	
	public static void main(String[] args) {
		ProductSearch  gameSearch = new ProductSearch("C:/Projects/index/product");
		//gameSearch.query("iphone",0,0,0,21.0187516,105.7752809 ,10,4, 1,1000);
		//gameSearch.queryListUserWishProduct("dien thoai samsung",1);
		gameSearch.queryProductRecommend("xe may",1,1200000,21.0305962, 105.7862149,1, 100) ;
		//gameSearch.querySortNearest("iphone",12,0,1000000,0,0,0,1, 10);
		
	}
}
