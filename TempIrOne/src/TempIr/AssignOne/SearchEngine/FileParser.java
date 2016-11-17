package TempIr.AssignOne.SearchEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class FileParser{
	private File parseFile;
	
	String content = "";
	String date = "";
	String title = "";

	
	 IndexWriter writer = null;
	
	 Analyzer analyzer = new StandardAnalyzer();
	public FileParser(File f, SimpleFSDirectory dir) {
		this.parseFile = f;
		
		
		
		 IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
		cfg.setOpenMode(OpenMode.CREATE);
		 try {
			 
			
			writer = new IndexWriter(dir, cfg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void getDocuments(){
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(parseFile.getPath()));
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
		Document doc = Jsoup.parse(html, "UTF-8", Parser.xmlParser());
		Elements docs = doc.getElementsByTag("doc");
		for(Element e: docs){
			date="";
			content="";
				//System.out.println("abc");
				Elements tags = e.getElementsByTag("tag");
				for(Element t : tags){
					if(t.attr("name").equals("date")){
						date = t.text();
					}
					else if(t.attr("name").equals("title"))
						content = t.text()+" "+ content;
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
				try {
					currentDoc.add(new TextField(content, new FileReader(parseFile)));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				currentDoc.add(new Field("date", date, Store.YES, Field.Index.NOT_ANALYZED));
				try {
					writer.addDocument(currentDoc);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					writer.commit();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
		try {
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	}