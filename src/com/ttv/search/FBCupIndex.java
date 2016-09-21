package com.ttv.search;

import java.io.File;
import java.io.IOException;

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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.FBCup;
import com.ttv.dao.FootBallDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class FBCupIndex {
	
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public FBCupIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,FBCup cup)
	{
		try {
			  Document doc = new Document();
		
			  System.out.println(cup.id);

			  IntField idField = new IntField("id", cup.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          cup.name = StringTool.stripLucene(cup.name);
	          cup.name_en = StringTool.stripLucene(cup.name_en);
	          cup.country = StringTool.stripLucene(cup.country);
	          cup.country_en = StringTool.stripLucene(cup.country_en);
	          
	          String _name = UTF8Tool.coDau2KoDau(cup.name);
	          String _country = UTF8Tool.coDau2KoDau(cup.country);
	          
	          doc.add(new Field("name", cup.name, TextField.TYPE_STORED));
	          doc.add(new Field("_name", _name, TextField.TYPE_STORED));
	          doc.add(new Field("country", cup.country, TextField.TYPE_STORED));
	          doc.add(new Field("_country", _country, TextField.TYPE_STORED));
	          doc.add(new Field("name_en", cup.name_en, TextField.TYPE_STORED));
	          doc.add(new Field("country_en", cup.country_en, TextField.TYPE_STORED));
	         
	          doc.add(new StringField("code", cup.code, Field.Store.YES));
	          
	          doc.add(new StringField("logo", cup.logo!=null?cup.logo:"", Field.Store.YES));
	          doc.add(new StringField("create_date", cup.create_date, Field.Store.YES));
	          
	          int rate = 0;
	          if(cup!=null) rate = 3*(10-rate);
	          double rank_index = rate;
	          DoubleField rankField = new DoubleField("rank_index", rank_index, DoubleField.TYPE_STORED);
	          doc.add(rankField);
	          
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(cup.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexCup(){
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
		    	 	java.util.List<FBCup> listNews = footBallDAO.getListCup(update_date);
					if(listNews.size()==0) break;
					int i = 0;
					
					while(i<listNews.size()){
						FBCup news = listNews.get(i);
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
	/*	String log_path = "C:/Projects/index/football/cup/log/logIndexCup.txt";
		String pid_Path = "C:/Projects/index/football/cup/log/pidIndexCup.txt";
		
		FBCupIndex  audioIndex = new FBCupIndex("C:/Projects/index/football/cup/", pid_Path,log_path);
		audioIndex.indexCup();*/
		
		String log_path = "/home/search/football/cup/log/logIndexCup.txt";
		String pid_Path = "/home/search/football/cup/log/pidIndexCup.txt";
		
		FBCupIndex  audioIndex = new FBCupIndex("/home/search/football/cup/", pid_Path,log_path);
		audioIndex.indexCup();
	}
	
}
