package com.ttv.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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

import com.ttv.dao.FBClub;
import com.ttv.dao.FootBallDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class FBClubIndex {
	
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public FBClubIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,FBClub club)
	{
		try {
			  Document doc = new Document();
		
			  System.out.println(club.id);

			  IntField idField = new IntField("id", club.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          club.name = StringTool.stripLucene(club.name);
	          club.name_en = StringTool.stripLucene(club.name_en);
	          club.country = StringTool.stripLucene(club.country);
	          club.country_en = StringTool.stripLucene(club.country_en);
	          club.city = StringTool.stripLucene(club.city);
	          
	          String _name = UTF8Tool.coDau2KoDau(club.name);
	          String _country = UTF8Tool.coDau2KoDau(club.country);
	          
	          doc.add(new Field("name", club.name, TextField.TYPE_STORED));
	          doc.add(new Field("_name", _name, TextField.TYPE_STORED));
	          doc.add(new Field("country", club.country, TextField.TYPE_STORED));
	          doc.add(new Field("_country", _country, TextField.TYPE_STORED));
	          doc.add(new Field("country_en", club.country_en, TextField.TYPE_STORED));
	          doc.add(new Field("city", club.city, TextField.TYPE_STORED));
	          doc.add(new Field("name_en", club.name_en, TextField.TYPE_STORED));
	         
	          doc.add(new StringField("logo", club.logo!=null?club.logo:"", Field.Store.YES));
	          doc.add(new StringField("create_date", club.create_date, Field.Store.YES));
	          
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(club.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          System.out.println(_name);
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexClub(){
		boolean created = false;
		File file = new File(log_path);
		if(!file.exists()) created =true;
		
		IndexLogger.pidPath = pid_path;
		IndexLogger.logPath = log_path;
		
		if(IndexLogger.existPID()) return; else IndexLogger.createPID();
		
		String last_time = "2014-07-10 00:00:00";
		FootBallDAO footBallDAO = new FootBallDAO();
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
		    	 	java.util.List<FBClub> listNews = footBallDAO.getListClub(update_date);
					if(listNews.size()==0) break;
					int i = 0;
					
					while(i<listNews.size()){
						FBClub news = listNews.get(i);
						addDocument(writer, news);
						last_time = news.update_date;
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
		/*String log_path = "C:/Projects/index/football/club/log/logIndexClub.txt";
		String pid_Path = "C:/Projects/index/football/club/log/pidIndexClub.txt";
	
		FBClubIndex  audioIndex = new FBClubIndex("C:/Projects/index/football/club/", pid_Path,log_path);
		audioIndex.indexClub();*/
		
		String log_path = "/home/search/football/club/log/logIndexClub.txt";
		String pid_Path = "/home/search/football/club/pidIndexClub.txt";
		
		FBClubIndex  audioIndex = new FBClubIndex("/home/search/football/club/", pid_Path,log_path);
		audioIndex.indexClub();
	}
	
}
