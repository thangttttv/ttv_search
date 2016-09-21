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
import com.ttv.dao.Game;
import com.ttv.dao.GameStoreDAO;
import com.ttv.util.IndexLogger;
import com.ttv.util.StringTool;
import com.ttv.util.UTF8Tool;

public class GameAndroidIndex {
	public String index_path;
	public String pid_path;
	public String log_path;
	
	public GameAndroidIndex(String index_path,String pid_path,String log_path){
		this.index_path = index_path;
		this.pid_path = pid_path;
		this.log_path = log_path;
	}
	
	public void addDocument(IndexWriter writer,Game game)
	{
		try {
			  Document doc = new Document();
			  
			  System.out.println(game.id);

			  IntField idField = new IntField("id", game.id, IntField.TYPE_STORED);
	          doc.add(idField);
	         
	          game.name = StringTool.stripLucene(game.name.toLowerCase());
	          game.description = StringTool.stripLucene(game.description.toLowerCase());
	          String _name = UTF8Tool.coDau2KoDau(game.name);
	          String _description = UTF8Tool.coDau2KoDau(game.description);
	          
	          doc.add(new Field("name", game.name, TextField.TYPE_STORED));
	          doc.add(new Field("_name", _name, TextField.TYPE_STORED));
	          doc.add(new Field("description", game.description, TextField.TYPE_STORED));
	          doc.add(new Field("_description", _description, TextField.TYPE_STORED));
	         // doc.add(new StringField("bundle_id", game.bundle_id, Field.Store.YES));
	          
	          doc.add(new StringField("icon", game.icon, Field.Store.YES));
	          doc.add(new StringField("category_id", String.valueOf(game.category_id), Field.Store.YES));
	          doc.add(new StringField("publisher_id", String.valueOf(game.publisher_id), Field.Store.YES));
	          doc.add(new StringField("publisher_name", game.publisher_name, Field.Store.YES));
	          doc.add(new StringField("version_android", String.valueOf(game.version_android), Field.Store.YES));
	          doc.add(new StringField("version_os_android", String.valueOf(game.version_os_android), Field.Store.YES));
	          doc.add(new StringField("size_android", String.valueOf(game.size_android), Field.Store.YES));
	          
	          doc.add(new StringField("count_android_download", String.valueOf(game.count_android_download), Field.Store.YES));
	          doc.add(new StringField("count_android_view", String.valueOf(game.count_android_view), Field.Store.YES));
	          doc.add(new StringField("count_review", String.valueOf(game.count_review), Field.Store.YES));
	          
	          doc.add(new DoubleField("mark", game.mark, DoubleField.TYPE_STORED));
	          doc.add(new StringField("file_apk", String.valueOf(game.file_apk), Field.Store.YES));
	          doc.add(new StringField("is_hot", String.valueOf(game.is_hot), Field.Store.YES));
	          doc.add(new StringField("update_date", String.valueOf(game.update_date), Field.Store.YES));
	          doc.add(new StringField("create_date", String.valueOf(game.create_date), Field.Store.YES));
	          doc.add(new StringField("status", String.valueOf(game.status), Field.Store.YES));
	          
	          
	          Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(game.create_date);
	          Date currentDate = Calendar.getInstance().getTime();
	          long diff = Math.abs(currentDate.getTime() - d1.getTime());
	          long diffDays = diff / (24 * 60 * 60 * 1000);
	          int is_new = diffDays>30 ? 0:1;
	          
	          double rank_index = game.is_hot * 3+game.mark*5+is_new*10;
	          DoubleField rankField = new DoubleField("rank_index", rank_index, DoubleField.TYPE_STORED);
	          doc.add(rankField);
	          
	          String arrTags[] = game.tags.split(",");
	          
	          System.out.println(game.name);
	          
	          for (String tag : arrTags) {
	        	  doc.add(new Field("tag", tag.trim().toLowerCase(), TextField.TYPE_STORED));
	        	  doc.add(new Field("_tag", UTF8Tool.coDau2KoDau(tag.trim().toLowerCase()), TextField.TYPE_STORED));
	        	  System.out.println(tag);
	          }
	          
	          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	            writer.addDocument(doc);
	          } else {
	        	 /* BytesRef bytes = new BytesRef(NumericUtils.BUF_SIZE_INT);
	        	  NumericUtils.intToPrefixCoded(game.id, 0, bytes);
	        	  Term term = new Term("id", bytes);*/
	        	 
	        	  Term term1 = new Term("id", String.valueOf(game.id));
	              writer.updateDocument(term1, doc);
	            
	          }
	          
	          
		} catch (Exception e) {
			e.printStackTrace();
		}
		  
	}
	
	public void indexGame(){
		boolean created = false;
		File file = new File(log_path);
		if(!file.exists()) created =true;
		
		IndexLogger.pidPath = pid_path;
		IndexLogger.logPath = log_path;
		
		if(IndexLogger.existPID()) return; else IndexLogger.createPID();
		
		String last_time = "2014-07-10 00:00:00";
		GameStoreDAO gameStoreDAO = new GameStoreDAO();
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
		    	 	java.util.List<Game> games = gameStoreDAO.getGameAndroid(update_date);
					if(games.size()==0) break;
					
					int i = 0;
					
					while(i<games.size()){
						Game game = games.get(i);
						addDocument(writer, game);
						last_time = game.update_date;
						i++;
					}
					
					System.out.println(last_time);
					IndexLogger.writeLog(last_time);
					
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
		/*String log_path = "C:/Projects/index/game/log/logIndexAndroid.txt";
		String pid_Path = "C:/Projects/index/game/log/pidIndexAndroid.txt";
		GameAndroidIndex  gameIndex = new GameAndroidIndex("C:/Projects/index/game/", pid_Path,log_path);
		gameIndex.indexGame();*/
		
		String log_path = "/home/search/gameAndroid/log/logIndexAndroid.txt";
		String pid_Path = "/home/search/gameAndroid/log/pidIndexAndroid.txt";
		
		GameAndroidIndex  gameIndex = new GameAndroidIndex("/home/search/gameAndroid/", pid_Path,log_path);
		gameIndex.indexGame();
	}

}
