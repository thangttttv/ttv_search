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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.FBFootballer;
import com.ttv.dao.FootBallDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class FBFootBallerIndex {
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public FBFootBallerIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,FBFootballer fbFootballer)
	{
		try {
			  Document doc = new Document();
		
			  System.out.println(fbFootballer.id);

			  IntField idField = new IntField("id", fbFootballer.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          fbFootballer.name = StringTool.stripLucene(fbFootballer.name);
	          fbFootballer.country = StringTool.stripLucene(fbFootballer.country);
	          
	          fbFootballer.club_name = StringTool.stripLucene(fbFootballer.club_name);
	          fbFootballer.club_name_en = StringTool.stripLucene(fbFootballer.club_name_en);
	          
	          String _name = UTF8Tool.coDau2KoDau(fbFootballer.name);
	          String _country = UTF8Tool.coDau2KoDau(fbFootballer.country);
	          String _club_name = UTF8Tool.coDau2KoDau(fbFootballer.club_name);
	          
	          doc.add(new Field("name", fbFootballer.name, TextField.TYPE_STORED));
	          doc.add(new Field("_name", _name, TextField.TYPE_STORED));
	          doc.add(new Field("country", fbFootballer.country, TextField.TYPE_STORED));
	          doc.add(new Field("_country", fbFootballer.club_name_en, TextField.TYPE_STORED));
	          doc.add(new Field("club_name", fbFootballer.club_name, TextField.TYPE_STORED));
	          doc.add(new Field("_club_name", _club_name, TextField.TYPE_STORED));
	          doc.add(new Field("club_name_en", _country, TextField.TYPE_STORED));
	          doc.add(new Field("avatar", fbFootballer.avatar!=null?fbFootballer.avatar:"", TextField.TYPE_STORED));
	          doc.add(new StringField("create_date", fbFootballer.create_date, Field.Store.YES));
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(fbFootballer.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexFootballer(){
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
		    	 	java.util.List<FBFootballer> listNews = footBallDAO.getListFootballer(update_date);
					if(listNews.size()==0) break;
					int i = 0;
					
					while(i<listNews.size()){
						FBFootballer news = listNews.get(i);
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
		/*String log_path = "C:/Projects/index/football/footballer/log/logIndexFootballer.txt";
		String pid_Path = "C:/Projects/index/football/footballer/log/pidIndexFootballer.txt";
		FBFootBallerIndex  fbCoachIndex = new FBFootBallerIndex("C:/Projects/index/football/footballer", pid_Path,log_path);
		fbCoachIndex.indexFootballer();*/
		
		String log_path = "/home/search/football/footballer/log/logIndexFootballer.txt";
		String pid_Path = "/home/search/football/footballer/pidIndexFootballer.txt";
		
		FBFootBallerIndex  fbCoachIndex = new FBFootBallerIndex("/home/search/football/footballer/", pid_Path,log_path);
		fbCoachIndex.indexFootballer();
	}
	
}
