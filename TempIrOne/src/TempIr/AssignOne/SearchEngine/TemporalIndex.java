package TempIr.AssignOne.SearchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class TemporalIndex{
	static String content = "";
	static String date = "";
	static String title = "";
	static String id = "";
	static IndexWriter writer = null;

	public static void main(String args[]){
	//Indexing the directory 
	String inputPath = "/Users/Rishita/Downloads/Temporalia_Sample3";

	String saveDir = "/Users/Rishita/ITIS/semester_02/TempIr";
	SimpleFSDirectory dir = null;
	 try {
		dir = new SimpleFSDirectory(Paths.get(saveDir));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
		
		 
		
		 Analyzer analyzer = new StandardAnalyzer();
		 IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
			cfg.setOpenMode(OpenMode.CREATE);
			 try {
				 
				
				writer = new IndexWriter(dir, cfg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 
	 File[] files = new File(inputPath).listFiles();
	 for (File f: files) {
		 System.out.println(f.getName());
	
	 getDocuments(f);
	
	 }
	 try {
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	}
		 public static void getDocuments(File f){
			byte[] encoded = null;
			try {
				encoded = Files.readAllBytes(Paths.get(f.getPath()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String html="";
			try {
				html = new String(encoded,"UTF8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8", Parser.xmlParser());
			Elements docs = doc.getElementsByTag("doc");
			
			for(Element e: docs){
				date="";
				content="";
				if(e.hasAttr("id")){
					id = e.attr("id");
				}
					//System.out.println("abc");
					Elements tags = e.getElementsByTag("tag");
					for(Element t : tags){
						if(t.attr("name").equals("date")){
							date = t.text();
						}
						else if(t.attr("name").equals("title")){
							content = t.text()+" "+ content;
							title = t.text();
						}
					}
					Elements text = e.getElementsByTag("text");
					for(Element t: text){
						content = content + t.text() + " ";
					}
					date = date.trim();
					content = content.trim();
					//System.out.println(date);
					//System.out.println(content);
					org.apache.lucene.document.Document currentDoc = new org.apache.lucene.document.Document();
					currentDoc.add(new TextField("content", new StringReader(content)));
					currentDoc.add(new StringField("title", title, Store.YES));
					currentDoc.add(new StringField("doc_ID", id, Store.YES));
					currentDoc.add(new StringField("date", date, Store.YES));
					try {
						writer.addDocument(currentDoc);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	
	
	}}
	
	}