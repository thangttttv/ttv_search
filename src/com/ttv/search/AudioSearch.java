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

import com.ttv.dao.Audio;
import com.ttv.util.StringTool;

public class AudioSearch {

	public String index_path;
	public int totalHit;

	public AudioSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<Audio> query(String keyword, int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<Audio> kList = new ArrayList<Audio>();
		Audio audio = null;
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
				//boots.put("description", 1.0f);
				//boots.put("_description", 1.0f);
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"title","_title"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				
				Query queryName = queryp.parse(keyword);
				queryKeyword.add(queryName,BooleanClause.Occur.SHOULD);
				
				query.add(queryKeyword, BooleanClause.Occur.MUST);
			}
			
			Term t = new Term("status", "1");     
		    Query queryStatus = new TermQuery(t);     
		    query.add(queryStatus, BooleanClause.Occur.MUST);
		   
			System.out.println("query:"+query.toString());
			Sort sort = new Sort(new SortField("rank_index", Type.DOUBLE, true));
			TopDocs topDocs = searcher.search(query, 10,sort);
			
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
				audio = new Audio();
				audio.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				audio.title = searcher.doc(scoreDoc.doc).get("title");
				audio.description = searcher.doc(scoreDoc.doc).get("description");
				audio.cat_id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("cat_id"));
				audio.image = searcher.doc(scoreDoc.doc).get("image");
				
				audio.author = searcher.doc(scoreDoc.doc).get("author");
				audio.reader = searcher.doc(scoreDoc.doc).get("reader");
				
				audio.create_date = Integer.parseInt(searcher.doc(scoreDoc.doc).get("create_date"));
				
				audio.c_chapter = Integer.parseInt(searcher.doc(scoreDoc.doc).get("c_chapter"));
				audio.c_download = Integer.parseInt(searcher.doc(scoreDoc.doc).get("c_download"));
				audio.c_listen = Integer.parseInt(searcher.doc(scoreDoc.doc).get("c_listen"));
				
				System.out.println(audio.title+":"+searcher.doc(scoreDoc.doc).get("rank_index"));
				kList.add(audio);
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
		AudioSearch  gameSearch = new AudioSearch("C:\\Projects\\index\\audio\\");
		gameSearch.query("võ lâm",1, 10);
		
	}
	
}
