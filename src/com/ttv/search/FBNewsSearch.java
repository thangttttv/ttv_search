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
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.FBNews;
import com.ttv.util.StringTool;

public class FBNewsSearch {
	public String index_path;
	public int totalHit;

	public FBNewsSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<FBNews> query(String keyword, int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<FBNews> kList = new ArrayList<FBNews>();
		FBNews game = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword);
				 
				 
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("title", 2.0f);
				boots.put("_title", 2.0f);
				boots.put("description", 1.0f);
				boots.put("_description", 1.0f);
				boots.put("club_name", 1.0f);
				boots.put("cup_name", 1.0f);
				boots.put("country", 1.0f);
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"title","_title","description","_description","club_name","cup_name","country"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				
				Query queryName = queryp.parse(keyword);
				queryKeyword.add(queryName,BooleanClause.Occur.SHOULD);
				
				HashMap<String,Float> bootTags = new HashMap<String, Float>();
				boots.put("tag", 1.0f);
				boots.put("_tag", 1.0f);
			
				QueryParser queryTag = new MultiFieldQueryParser(Version.LUCENE_4_9, new
						String[] {"tag","_tag"},analyzer,bootTags);
				
				queryKeyword.add(queryTag.parse("\""+keyword+"\""),BooleanClause.Occur.SHOULD);
				
				//create the term query object
				Query queryNameEn = new PrefixQuery(new Term("title",keyword));
				Query queryName_ = new PrefixQuery(new Term("_title",keyword));
				
				
				query.add(queryNameEn, BooleanClause.Occur.SHOULD);
				query.add(queryName_, BooleanClause.Occur.SHOULD);
				
				query.add(queryKeyword, BooleanClause.Occur.SHOULD);
			}
			
			System.out.println("query:"+query.toString());
			Sort sort = new Sort(new SortField("rank_index", Type.DOUBLE, true));
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
				game = new FBNews();
				game.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				game.title = searcher.doc(scoreDoc.doc).get("title");
				game.description = searcher.doc(scoreDoc.doc).get("description");
				game.club_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("club_id"));
				game.cup_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cup_id"));
				game.club_name = searcher.doc(scoreDoc.doc).get("club_name");
				game.cup_name = searcher.doc(scoreDoc.doc).get("cup_name");
				game.image = searcher.doc(scoreDoc.doc).get("image");
				game.country = searcher.doc(scoreDoc.doc).get("country");
				
				game.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				
				System.out.println(game.title+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
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
		FBNewsSearch  gameSearch = new FBNewsSearch("C:/Projects/index/football/news/");
		gameSearch.query("r",1, 10);
		
	}
}
