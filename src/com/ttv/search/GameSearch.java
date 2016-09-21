package com.ttv.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.Game;


public class GameSearch {
	public String index_path;
	public int totalHit;

	public GameSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<Game> query(String keyword,int cat_id,int type_sort, int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Game> kList = new ArrayList<Game>();
		Game game = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			 
			QueryParser parser = new QueryParser(Version.LUCENE_4_9, "name", analyzer);
			Query queryName = parser.parse(keyword);
			query.add(queryName,BooleanClause.Occur.MUST);
			
			BooleanQuery query1 = new BooleanQuery();
			query1.add(new TermQuery(new Term("id", String.valueOf(cat_id))), BooleanClause.Occur.MUST);
	 
	         
			if (cat_id > 0) {
				//query.add(new TermQuery(new Term("_id", String.valueOf(cat_id))), BooleanClause.Occur.MUST);
			}
			
			//query.add(new TermQuery(new intTerm("_id", String.valueOf(cat_id))), BooleanClause.Occur.MUST);
			
			//NumericRangeQuery<Integer> price = NumericRangeQuery.newIntRange("id", cat_id, 10000, true, true);
			//query.add(price, BooleanClause.Occur.MUST);
			
			
			Term t = new Term("name", keyword);     
		    Query query2 = new TermQuery(t);     
		    
		    QueryParser query3 = new QueryParser(Version.LUCENE_4_9,"name",analyzer);
		    query3.setDefaultOperator(QueryParser.AND_OPERATOR);
		   
		    QueryParser queryp = new MultiFieldQueryParser(Version.LUCENE_4_9, new String[] {"name", "description"}, analyzer);
			queryp.setDefaultOperator(QueryParser.OR_OPERATOR);
			Query queryMulti = queryp.parse(keyword);
			
			System.out.println("queryMulti:"+queryMulti.toString());
			System.out.println("query3:"+query3.parse(keyword).toString());
			System.out.println("query2:"+query2.toString());
			System.out.println("query:"+query.toString());
			TopDocs topDocs = searcher.search(query3.parse(keyword), 10);
			
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
				System.out.println(game.name);
				kList.add(game);
				i++;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return kList;
	}
	
	public static void main(String[] args) {
		GameSearch  gameSearch = new GameSearch("C:\\Projects\\index\\game\\");
		gameSearch.query("Bom   bản phiên ", 7, 0, 1, 10);
	}
	
}
