package TempIr.BonusAssign;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import TempIr.AssignOne.SearchEngine.LMDirichlet;
import TempIr.AssignOne.SearchEngine.LMMercer;

class queryProcessingFinal{
	static String indexDir = "/Users/Rishita/ITIS/semester_02/TempIrBonus";
	static String saveDir="/Users/Rishita/ITIS/semester_02/txtCorpus1000/";
	public static void main(String args[]){
		
		if(Files.exists(Paths.get(saveDir))){
			try {
				FileUtils.cleanDirectory(new File(saveDir));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//File corpFile = new File(saveDir);
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter query: ");
		String query = reader.nextLine();
		String queryPartA = query.trim();
		//String queryPartB = query.substring(query.indexOf("@")+1).trim();
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
		//String lowerDate = queryPartB.substring(0, queryPartB.indexOf("-"))+"-01-01";
		//String upperDate = queryPartB.substring(queryPartB.indexOf("-")+1)+"-12-31";
		//TermRangeQuery yearQuery = new TermRangeQuery("date", new BytesRef(lowerDate), new BytesRef(upperDate), Boolean.TRUE, Boolean.TRUE); 
		Builder finalQueryBuilder = new BooleanQuery.Builder().add(qA, Occur.MUST);
		BooleanQuery finalQuery = finalQueryBuilder.build();
		TopDocs finalDocs = null;
		try {
			finalDocs = searcher.search(finalQuery, 1000);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int noDocs = finalDocs.totalHits;
		System.out.println(noDocs);
		if (finalDocs.totalHits == 0) {
			System.out.println("No data found.");
		} else {
			
			
			for (ScoreDoc d : finalDocs.scoreDocs) {
				Document doc = null;
				try {
					doc = searcher.doc(d.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // get the next document
				String id = doc.get("doc_ID");
				String url = doc.get("title").trim(); // get its path field
				IndexableField content=doc.getField("content");
				String cont=content.stringValue();
				String finalContent = url+" "+cont+" ";
				Path file = Paths.get(saveDir+id+".txt");
				File fileCreate = new File(saveDir+id+".txt");
				try {
					fileCreate.createNewFile();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				finalContent = finalContent.replaceAll("\n", "");
				//finalContent = finalContent.replaceAll("$*.\\.*$", "");
				
				try {
					Files.write(file, finalContent.getBytes(), StandardOpenOption.APPEND);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println("Found in :: DOC ID::"+id);
				
			}
		}
		try {
			Process p = Runtime.getRuntime().exec("python word2vec.py -c /Users/Rishita/ITIS/semester_02/txtCorpus1000/");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//String res = in.readLine();
			//System.out.println("value is : "+res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try {
			TimeUnit.MINUTES.sleep(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		String res="";
		try {
			String argument = queryPartA.trim().replaceAll(" ",",");
			Process p = Runtime.getRuntime().exec("python getResults.py -w "+argument);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			res = in.readLine();
			if(!res.isEmpty()){
				res = res.replaceAll("[^a-zA-Z0-9\\s]", "");
				/*res= res.replace("[", "");
				res=res.replace("]", "");
				res=res.replace("\'", "");
				res=res.replace(",", "");*/
				
			}
			System.out.println("value is : "+res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Modifying query with similar terms!
		String[] newQueryWords= res.trim().split(" ");
		Builder secondQueryBuilder = new BooleanQuery.Builder().add(qA, Occur.MUST);
		for(String qw: newQueryWords){
			Query q = null;
			try {
				q = qp.parse(qw);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			secondQueryBuilder.add(q, Occur.SHOULD);
		}
		ArrayList<String> docIds = new ArrayList<String>();
		BooleanQuery secondQuery = secondQueryBuilder.build();
		TopDocs finalDocs1 = null;
		try {
			finalDocs1 = searcher.search(secondQuery, 10);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noDocs = finalDocs1.totalHits;
		System.out.println(noDocs);
		if (finalDocs1.totalHits == 0) {
			System.out.println("No data found.");
		} else {
			
			
			for (ScoreDoc d : finalDocs1.scoreDocs) {
				Document doc = null;
				try {
					doc = searcher.doc(d.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // get the next document
				String id = doc.get("doc_ID");
				docIds.add(id.trim());
				System.out.println("DocID: "+id);
			}
		
	}
	//Calculating precision!
		int count5 = 0, count10=0;
		Scanner readerx = new Scanner(System.in);
		System.out.println("Enter the subtopic id of Answer for groundTruth: ");
		String subt=readerx.nextLine();
		for(String i: docIds){
			File groundTruth = new File("/Users/Rishita/ITIS/groundTruth.txt");
			LineIterator it = null;
			try {
				it = FileUtils.lineIterator(groundTruth, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				
				
			    while (it.hasNext()) {
			    String line = it.nextLine();
			    if(line.startsWith(subt)){
			    if(line.contains(i)){
			    	if(line.contains("L1") || line.contains("L2"))
			    		if(docIds.indexOf(i)<=5)
			    		{ 
			    			count5++;
			    			count10++;
			    		}
			    		else
			    			count10++;
			    }
			    }}
			} finally {
			    it.close();
			}
			
		}
		System.out.println("Precision@5: "+count5+"/5 Precision@10: "+count10+"/10"); //To prevent rounding up of small values
		
	}
}