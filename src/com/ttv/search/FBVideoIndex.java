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
import com.ttv.dao.FBVideo;
import com.ttv.dao.FootBallDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class FBVideoIndex {
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public FBVideoIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,FBVideo video)
	{
		try {
			  Document doc = new Document();
			  FootBallDAO footBallDAO   = new FootBallDAO();
			  
			  System.out.println(video.id);

			  IntField idField = new IntField("id", video.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          video.title = StringTool.stripLucene(video.title);
	          video.description = StringTool.stripLucene(video.description);
	          String _title = UTF8Tool.coDau2KoDau(video.title);
	          String _description = UTF8Tool.coDau2KoDau(video.description);
	          
	          doc.add(new Field("title", video.title, TextField.TYPE_STORED));
	          doc.add(new Field("_title", _title, TextField.TYPE_STORED));
	          doc.add(new Field("description", video.description, TextField.TYPE_STORED));
	          doc.add(new Field("_description", _description, TextField.TYPE_STORED));
	          doc.add(new Field("club_name", video.club_name!=null?video.club_name:"", TextField.TYPE_STORED));
	          doc.add(new Field("cup_name", video.cup_name!=null?video.cup_name:"", TextField.TYPE_STORED));
	          
	          doc.add(new StringField("image", video.image, Field.Store.YES));
	          doc.add(new StringField("video", video.video, Field.Store.YES));
	          doc.add(new StringField("club_id", String.valueOf(video.club_id), Field.Store.YES));
	          doc.add(new StringField("cup_id", String.valueOf(video.cup_id), Field.Store.YES));
	          doc.add(new StringField("create_date", video.create_date, Field.Store.YES));
	          
	          FBCup cup = footBallDAO.getCup(video.cup_id);
	          int rate = 0;
	          if(cup!=null) rate = 3*(10-rate);
	          double rank_index = video.id+rate;
	          DoubleField rankField = new DoubleField("rank_index", rank_index, DoubleField.TYPE_STORED);
	          doc.add(rankField);
	          
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(video.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexVideo(){
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
		    	 	java.util.List<FBVideo> listNews = footBallDAO.getListVideo(update_date);
		    	 	if(listNews.size()==0) break;
					
					int i = 0;
					
					while(i<listNews.size()){
						FBVideo video = listNews.get(i);
						addDocument(writer, video);
						last_time = video.update_date;
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
	/*	String log_path = "C:/Projects/index/football/video/log/logIndexVideo.txt";
		String pid_Path = "C:/Projects/index/football/video/log/pidIndexVideo.txt";
		
		FBVideoIndex  audioIndex = new FBVideoIndex("C:/Projects/index/football/video/", pid_Path,log_path);
		audioIndex.indexVideo();*/
		
		String log_path = "/home/search/football/video/log/logIndexVideo.txt";
		String pid_Path = "/home/search/football/video/log/pidIndexVideo.txt";
		
		FBVideoIndex  audioIndex = new FBVideoIndex("/home/search/football/video/", pid_Path,log_path);
		audioIndex.indexVideo();
	}
}
