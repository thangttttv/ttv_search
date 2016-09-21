package com.ttv.search;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ibm.icu.util.Calendar;
import com.ttv.dao.Product;
import com.ttv.dao.ProductDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class ProductIndex {
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public ProductIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,Product product)
	{
		try {
			  Document doc = new Document();
			  
			  System.out.println(product.id+"-"+product.create_date);

			  Field idField = new Field("id", product.id+"", TextField.TYPE_STORED);
	          doc.add(idField);
	         
	          product.title = StringTool.stripLucene(product.title);
	          product.description = StringTool.stripLucene(product.description);
	          String _title = UTF8Tool.coDau2KoDau(product.title);
	          String _description = UTF8Tool.coDau2KoDau(product.description);
	          
	          doc.add(new Field("title", product.title, TextField.TYPE_STORED));
	          doc.add(new Field("_title", _title, TextField.TYPE_STORED));
	          doc.add(new Field("description", product.description, TextField.TYPE_STORED));
	          doc.add(new Field("_description", _description, TextField.TYPE_STORED));
	        
	          doc.add(new StringField("image", product.image, Field.Store.YES));
	          doc.add(new StringField("image_size", product.image_size, Field.Store.YES));
	          doc.add(new StringField("image", product.image, Field.Store.YES));
	          doc.add(new StringField("cate_id", String.valueOf(product.cate_id), Field.Store.YES));
	          System.out.println("product.cate_parent_id"+product.cate_parent_id);
	          doc.add(new StringField("cate_parent_id", String.valueOf(product.cate_parent_id), Field.Store.YES));
	          
	          doc.add(new StringField("sta_comment", String.valueOf(product.sta_comment), Field.Store.YES));
	          doc.add(new StringField("sta_like", String.valueOf(product.sta_like), Field.Store.YES));
	          doc.add(new StringField("sta_transaction", String.valueOf(product.sta_transaction), Field.Store.YES));
	          doc.add(new StringField("sta_view", String.valueOf(product.sta_view), Field.Store.YES));
	          doc.add(new StringField("status", String.valueOf(product.status), Field.Store.YES));
	          doc.add(new StringField("quantity", String.valueOf(product.quantity), Field.Store.YES));
	          
	          doc.add(new DoubleField("lat", product.lat, Field.Store.YES));
	          doc.add(new DoubleField("lng", product.lng, Field.Store.YES));
	          doc.add(new DoubleField("price", product.price, Field.Store.YES));
	          doc.add(new IntField("user_id", product.user_id, Field.Store.YES));
	          
	          doc.add(new StringField("create_date", product.create_date, Field.Store.YES));
	          doc.add(new StringField("update_date", product.update_date, Field.Store.YES));
	          doc.add(new StringField("create_user", product.create_user, Field.Store.YES));
	          
	          Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(product.create_date);
	          Date currentDate = Calendar.getInstance().getTime();
	          long diff = Math.abs(currentDate.getTime() - d1.getTime());
	          long diffDays = diff / (24 * 60 * 60 * 1000);
	          int is_new = diffDays>30 ? 0:1;
	          
	          double rank_index = product.sta_view+(product.sta_like*3)+(product.sta_comment*2)+is_new*10;
	          DoubleField rankField = new DoubleField("rank_index", rank_index, DoubleField.TYPE_STORED);
	          doc.add(rankField);
	          
	          if(product.deleted==1||product.status==0) {
	        	  Term term1 = new Term("id", String.valueOf(product.id));
	              writer.deleteDocuments(term1);
	              return;
	          }
	          /*if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(product.id));
	              writer.updateDocument(term1, doc);
	          }*/
	          
	          Term term1 = new Term("id", String.valueOf(product.id));
              writer.updateDocument(term1, doc);
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexProduct(){
		boolean created = false;
		File file = new File(log_path);
		if(!file.exists()) created =true;
		
		IndexLogger.pidPath = pid_path;
		IndexLogger.logPath = log_path;
		
		if(IndexLogger.existPID()) return; else IndexLogger.createPID();
		
		String last_time = "";
		ProductDAO productDAO = new ProductDAO();
		Directory dir;
		try {
			
			dir = FSDirectory.open(new File(index_path));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);

		     if (created) {
		        iwc.setOpenMode(OpenMode.CREATE);
		      } else {
		        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		     }
		      
		     IndexWriter writer = new IndexWriter(dir, iwc); 
		    
		     while(true){
		    	 	String update_date = IndexLogger.readLog();
		    	 	java.util.List<Product> products = productDAO.getProduct(update_date);
					if(products.size()==0) break;
					
					int i = 0;
					
					while(i<products.size()){
						Product product = products.get(i);
						addDocument(writer, product);
						last_time = product.update_date;
						i++;
					}
					
					System.out.println(last_time);
					IndexLogger.writeLog(String.valueOf(last_time));
					
				}
		     
		      writer.commit();
		      writer.close();
		      
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IndexLogger.deletePID();
		}
	}
	
	
	public static void main(String[] args) {
		/*String log_path = "C:/Projects/index/product/log/logIndexProduct.txt";
		String pid_Path = "C:/Projects/index/product/log/pidIndexProduct.txt";
		ProductIndex  productIndex = new ProductIndex("C:/Projects/index/product/", pid_Path,log_path);
		productIndex.indexProduct();*/
		
		String log_path = "/home/search/product/log/logIndexProduct.txt";
		String pid_Path = "/home/search/product/log/pidIndexProduct.txt";
		
		ProductIndex  productIndex = new ProductIndex("/home/search/product/", pid_Path,log_path);
		productIndex.indexProduct();
	}
}
