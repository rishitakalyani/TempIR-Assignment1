package TempIr.AssignOne.SearchEngine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jsoup.parser.Parser;

public class queryProcess{
	static String indexDir = "/Users/Rishita/ITIS/semester_02/TempIr";
	public static void main(String args[]){
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter query in the format query@yyyy-yyyy: ");
		String query = reader.nextLine();
		String queryPartA = query.substring(0, query.indexOf("@"));
		String queryPartB = query.substring(query.indexOf("@")+1);
		System.out.println("Searching....");
		IndexReader read = null;
		try {
			 read= DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(read);
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
		BooleanQuery finalQuery = new BooleanQuery();
		finalQuery.add(qA, Occur.MUST);
		finalQuery.add(yearQuery, Occur.MUST);
		TopDocs topdocs = null;
		try {
			topdocs = searcher.search(finalQuery, 10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				System.out.println("Found in ::" + url+" DOC ID::"+id);
			}
		}
	}
}