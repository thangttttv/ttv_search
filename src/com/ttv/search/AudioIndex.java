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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.Audio;
import com.ttv.dao.AudioDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class AudioIndex {
	
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public AudioIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,Audio audio)
	{
		try {
			  Document doc = new Document();
			  
			  System.out.println(audio.id);

			  IntField idField = new IntField("id", audio.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          audio.title = StringTool.stripLucene(audio.title);
	          audio.description = StringTool.stripLucene(audio.description);
	          String _title = UTF8Tool.coDau2KoDau(audio.title);
	          String _description = UTF8Tool.coDau2KoDau(audio.description);
	          
	          doc.add(new Field("title", audio.title, TextField.TYPE_STORED));
	          doc.add(new Field("_title", _title, TextField.TYPE_STORED));
	          doc.add(new Field("description", audio.description, TextField.TYPE_STORED));
	          doc.add(new Field("_description", _description, TextField.TYPE_STORED));
	        
	          doc.add(new StringField("image", audio.image, Field.Store.YES));
	          doc.add(new StringField("cat_id", String.valueOf(audio.cat_id), Field.Store.YES));
	          doc.add(new StringField("c_chapter", String.valueOf(audio.c_chapter), Field.Store.YES));
	          doc.add(new StringField("c_download", String.valueOf(audio.c_download), Field.Store.YES));
	          doc.add(new StringField("c_listen", String.valueOf(audio.c_listen), Field.Store.YES));
	          doc.add(new StringField("hit", String.valueOf(audio.hit), Field.Store.YES));
	          doc.add(new IntField("create_date", audio.create_date, Field.Store.YES));
	          doc.add(new IntField("update_date", audio.update_date, Field.Store.YES));
	          doc.add(new StringField("create_user", String.valueOf(audio.create_user), Field.Store.YES));
	          doc.add(new StringField("status", audio.status+"", Field.Store.YES));
	          doc.add(new StringField("author", audio.author, Field.Store.YES));
	          doc.add(new StringField("reader", audio.reader, Field.Store.YES));
	          
	          double rank_index = audio.c_download+audio.c_listen;
	          DoubleField rankField = new DoubleField("rank_index", rank_index, DoubleField.TYPE_STORED);
	          doc.add(rankField);
	          
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	  Term term1 = new Term("id", String.valueOf(audio.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexAudio(){
		boolean created = false;
		File file = new File(log_path);
		if(!file.exists()) created =true;
		
		IndexLogger.pidPath = pid_path;
		IndexLogger.logPath = log_path;
		
		if(IndexLogger.existPID()) return; else IndexLogger.createPID();
		
		int last_time = 0;
		AudioDAO audioDAO = new AudioDAO();
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
		    	 	java.util.List<Audio> games = audioDAO.getAudio(Integer.parseInt(update_date));
					if(games.size()==0) break;
					
					int i = 0;
					
					while(i<games.size()){
						Audio audio = games.get(i);
						addDocument(writer, audio);
						last_time = audio.update_date;
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
		/*String log_path = "C:/Projects/index/audio/log/logIndexAudio.txt";
		String pid_Path = "C:/Projects/index/audio/log/pidIndexAudio.txt";
		AudioIndex  gameIndex = new AudioIndex("C:/Projects/index/audio/", pid_Path,log_path);
		gameIndex.indexAudio();*/
		
		String log_path = "/home/search/audio/log/logIndexAudio.txt";
		String pid_Path = "/home/search/audio/log/pidIndexAudio.txt";
		
		AudioIndex  audioIndex = new AudioIndex("/home/search/audio/", pid_Path,log_path);
		audioIndex.indexAudio();
	}
	
}
