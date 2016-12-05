package TempIr.AssignOne.SearchEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.ScoringRewrite;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity.CollectionModel;
import org.apache.lucene.search.similarities.LMSimilarity.LMStats;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jsoup.parser.Parser;

public class queryProcess{
	static String indexDir = "/Users/Rishita/ITIS/semester_02/TempIr";
	public static void main(String args[]){
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter query in the format query@yyyy-yyyy: ");
		String query = reader.nextLine();
		String queryPartA = query.substring(0, query.indexOf("@")).trim();
		String queryPartB = query.substring(query.indexOf("@")+1).trim();
		System.out.println("Enter 1 for Dirichlet and 2 for JelinikMercer: ");
		int choice = reader.nextInt();
		float m=0;
		float lam = 0;
		Scanner reader1 = new Scanner(System.in);
		if(choice==1){
		  System.out.println("Enter mu value: ");
		   m = reader1.nextFloat();
		}
		else{
			System.out.println("Enter lamda value: ");
			lam = reader1.nextFloat();
		}
		  System.out.println("Enter the no. of results to return: ");
		  int k = reader1.nextInt();
		  System.out.println(k);
		
		String[] queryWords = queryPartA.split(" "); 
		System.out.println("Searching....");
		IndexReader read = null;
		try {
			 read= DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(read);
		if(choice==1){
			searcher.setSimilarity(new LMDirichlet(m));
		}
		else{
			searcher.setSimilarity(new LMMercer(lam));
		}
		
		
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser qp = new QueryParser("content", analyzer);
		Query qA = null;
		try {
			qA = qp.parse(queryPartA);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String lowerDate = queryPartB.substring(0, queryPartB.indexOf("-"))+"-01-01";
		String upperDate = queryPartB.substring(queryPartB.indexOf("-")+1)+"-12-31";
		TermRangeQuery yearQuery = new TermRangeQuery("date", new BytesRef(lowerDate), new BytesRef(upperDate), Boolean.TRUE, Boolean.TRUE); 
		BooleanQuery finalQuery = new BooleanQuery.Builder().add(qA, Occur.MUST).add(yearQuery, Occur.MUST).build();
		TopDocs topdocs = null;
		TopDocs finalDocs = null;
		try {
			topdocs = searcher.search(finalQuery, k);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int noDocs = topdocs.totalHits;
		System.out.println(noDocs);
		if (topdocs.totalHits == 0) {
			System.out.println("No data found.");
		} else {
			
			
			for (ScoreDoc d : topdocs.scoreDocs) {
				Document doc = null;
				try {
					doc = searcher.doc(d.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // get the next document
				String id = doc.get("doc_ID");
				String url = doc.get("title"); // get its path field
				
				System.out.println("Found in :: DOC ID::"+id);
				System.out.println("Score::"+d.score);
			}
		}
	}
}