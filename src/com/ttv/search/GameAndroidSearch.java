package com.ttv.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.Game;
import com.ttv.util.StringTool;

public class GameAndroidSearch {
	public String index_path;
	public int totalHit;

	public GameAndroidSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<Game> query(String keyword, int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Game> kList = new ArrayList<Game>();
		Game game = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword);
				 
				 
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("name", 2.0f);
				boots.put("_name", 2.0f);
				boots.put("description", 1.0f);
				boots.put("_description", 1.0f);
				//boots.put("tag", 1.0f);
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"name","_name","description","_description"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				
				Query queryName = queryp.parse(keyword);
				queryKeyword.add(queryName,BooleanClause.Occur.SHOULD);
				
				HashMap<String,Float> bootTags = new HashMap<String, Float>();
				boots.put("tag", 1.0f);
				boots.put("_tag", 1.0f);
			
				QueryParser queryTag = new MultiFieldQueryParser(Version.LUCENE_4_9, new
						String[] {"tag","_tag"},analyzer,bootTags);
				
				queryKeyword.add(queryTag.parse("\""+keyword+"\""),BooleanClause.Occur.SHOULD);
				
				query.add(queryKeyword, BooleanClause.Occur.MUST);
			}
			
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
		   
			System.out.println("query:"+query.toString());
			Sort sort = new Sort(new SortField("rank_index", Type.DOUBLE, true));
			TopDocs topDocs = searcher.search(query, 1000,sort);
			
			int begin = (page - 1) * rowpage;
			begin = begin<0?0:begin;
			int end = begin + rowpage;
			this.totalHit = topDocs.totalHits ;
			end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
			int i = begin;
			ScoreDoc scoreDoc  = null;	
			System.out.println(i);
			System.out.println(end);
			
			while (i < end) {
				scoreDoc = topDocs.scoreDocs[i];	
				game = new Game();
				game.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				game.name = searcher.doc(scoreDoc.doc).get("name");
				game.description = searcher.doc(scoreDoc.doc).get("description");
				game.category_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("category_id"));
				game.publisher_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("publisher_id"));
				game.publisher_name = searcher.doc(scoreDoc.doc).get("publisher_name");
				game.icon = searcher.doc(scoreDoc.doc).get("icon");
				game.version_android = searcher.doc(scoreDoc.doc).get("version_android");
				game.version_os_android = searcher.doc(scoreDoc.doc).get("version_os_android");
				
				game.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				game.size_android = Double.parseDouble(searcher.doc(scoreDoc.doc).get("size_android"));
				game.mark = Double.parseDouble(searcher.doc(scoreDoc.doc).get("mark"));
				game.count_android_download = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_android_download"));
				game.count_android_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_android_view"));
				game.count_review = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_review"));
				game.is_hot = Integer.parseInt(searcher.doc(scoreDoc.doc).get("is_hot"));
				
				System.out.println(game.name+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
				kList.add(game);
				i++;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	
		return kList;
	}
	
	public List<Game> queryRecomment(String keyword) {
		BooleanQuery query = new BooleanQuery();	
		List<Game> kList = new ArrayList<Game>();
		Game game = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword);
				keyword = keyword.trim()+"*";
				
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("name", 2.0f);
				boots.put("_name", 2.0f);
				
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"name","_name"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryp.setAllowLeadingWildcard(true);
				Query queryName = queryp.parse(keyword);
				
				query.add(queryName, BooleanClause.Occur.MUST);
			}
			
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
		   
			System.out.println("query:"+query.toString());
			Sort sort = new Sort(new SortField("rank_index", Type.DOUBLE, true));
			TopDocs topDocs = searcher.search(query, 10,sort);
			
			this.totalHit = topDocs.scoreDocs.length ;
			int i = 0;
			ScoreDoc scoreDoc  = null;	
			System.out.println(i);
			System.out.println(this.totalHit);
			
			while (i < this.totalHit) {
				scoreDoc = topDocs.scoreDocs[i];	
				game = new Game();
				game.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				game.name = searcher.doc(scoreDoc.doc).get("name");
				game.description = searcher.doc(scoreDoc.doc).get("description");
				game.category_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("category_id"));
				game.publisher_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("publisher_id"));
				game.publisher_name = searcher.doc(scoreDoc.doc).get("publisher_name");
				game.icon = searcher.doc(scoreDoc.doc).get("icon");
				game.version_android = searcher.doc(scoreDoc.doc).get("version_android");
				game.version_os_android = searcher.doc(scoreDoc.doc).get("version_os_android");
				
				game.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				game.size_android = Double.parseDouble(searcher.doc(scoreDoc.doc).get("size_android"));
				game.mark = Double.parseDouble(searcher.doc(scoreDoc.doc).get("mark"));
				game.count_android_download = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_android_download"));
				game.count_android_view = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_android_view"));
				game.count_review = Integer.parseInt(searcher.doc(scoreDoc.doc).get("count_review"));
				game.is_hot = Integer.parseInt(searcher.doc(scoreDoc.doc).get("is_hot"));
				
				
				System.out.println(game.name+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
				kList.add(game);
				i++;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return kList;
	}
	
	public int getTotalHit() {
		return totalHit;
	}
	
	public static void main(String[] args) {
		GameAndroidSearch  gameSearch = new GameAndroidSearch("C:\\Projects\\index\\game\\");
		gameSearch.query("chien thuat",1, 10);
		gameSearch.queryRecomment("khat von*");
	}
}
