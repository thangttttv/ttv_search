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
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.ttv.dao.FBClub;
import com.ttv.util.StringTool;

public class FBClubSearch {

	public String index_path;
	public int totalHit;

	public FBClubSearch(String index_path){
		this.index_path = index_path;
	}
	
	
	public List<FBClub> query(String keyword, int page, int rowpage) {
		BooleanQuery query = new BooleanQuery();	
		List<FBClub> kList = new ArrayList<FBClub>();
		FBClub fbClub = null;
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
		
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index_path)));
			IndexSearcher searcher = new IndexSearcher(reader);
			
			if(!StringTool.isEmptyOrNul(keyword))
			{
				keyword = StringTool.stripLucene(keyword.toLowerCase());
				 
				HashMap<String,Float> boots = new HashMap<String, Float>();
				boots.put("name", 2.0f);
				boots.put("_name", 2.0f);
				boots.put("name_en", 2.0f);
				boots.put("country", 1.0f);
				boots.put("_country", 1.0f);
				boots.put("city", 1.0f);
				boots.put("country_en", 1.0f);
				
				BooleanQuery queryKeyword = new BooleanQuery();	
				QueryParser queryp  = new MultiFieldQueryParser(Version.LUCENE_4_9, new
				String[] {"name","_name","name_en","country_en","country","_country","city"}, analyzer,boots);
				queryp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryp.setAllowLeadingWildcard(true);
				
				Query queryOther = queryp.parse(keyword);
				queryKeyword.add(queryOther,BooleanClause.Occur.SHOULD);
				
				   //create the term query object
				 Query queryNameEn = new PrefixQuery(new Term("name_en",keyword));
				 Query queryName_ = new PrefixQuery(new Term("_name",keyword));
				 Query queryName = new PrefixQuery(new Term("name",keyword));
				 
				
				query.add(queryNameEn, BooleanClause.Occur.SHOULD);
				query.add(queryName_, BooleanClause.Occur.SHOULD);
				query.add(queryName, BooleanClause.Occur.SHOULD);
				query.add(queryKeyword, BooleanClause.Occur.SHOULD);
			}

			
			System.out.println("query:"+query.toString());
			TopDocs topDocs = searcher.search(query, 1000);
			
			int begin = (page - 1) * rowpage;
			begin = begin<0?0:begin;
			int end = begin + rowpage;
			this.totalHit = topDocs.scoreDocs.length;
			end = end > topDocs.scoreDocs.length ? topDocs.scoreDocs.length: end;
			int i = begin;
			ScoreDoc scoreDoc  = null;	
			System.out.println(i);
			System.out.println(end);
			
			while (i < end) {
				scoreDoc = topDocs.scoreDocs[i];	
				fbClub = new FBClub();
				fbClub.id = Integer.parseInt(searcher.doc(scoreDoc.doc).get("id"));
				fbClub.name = searcher.doc(scoreDoc.doc).get("name");
				fbClub.name_en = searcher.doc(scoreDoc.doc).get("name_en");
				fbClub.city = searcher.doc(scoreDoc.doc).get("city");
				fbClub.country = searcher.doc(scoreDoc.doc).get("country");
				fbClub.country_en = searcher.doc(scoreDoc.doc).get("country_en");
				fbClub.code = searcher.doc(scoreDoc.doc).get("code");
				fbClub.logo = searcher.doc(scoreDoc.doc).get("logo");
				fbClub.create_date = searcher.doc(scoreDoc.doc).get("create_date");
				
				System.out.println(fbClub.name );
				System.out.println(fbClub.country );
				kList.add(fbClub);
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
		FBClubSearch  gameSearch = new FBClubSearch("C:/Projects/index/football/club/");
		gameSearch.query("a", 1, 10);
	}
}
