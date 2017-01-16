package TempIr.BonusAssign;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class xmlToText{
	static String content = "";
	static String date = "";
	static String title = "";
	static String id = "";
	static String saveDir="/Users/Rishita/ITIS/semester_02/txtCorpus1000/";
	static String inputPath;

	public static String getInputPath() {
		return inputPath;
	}

	public static void setInputPath(String inputPath) {
		xmlToText.inputPath = inputPath;
	}

	public static void main(String args[]){
		try {
			Process p = Runtime.getRuntime().exec("python getResults.py -w computer");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String res = in.readLine();
			res = res.replace("[", "");
			res = res.replace("]", "");
			res= res.replaceAll("[[(|)]|(|)|\']", "");
			res = res.replaceAll("[0-9]", "");
			res=res.replace(".", "");
			res=res.replace(", , ", " ");
			res=res.replace(",,", "");
			System.out.println("value is : "+res);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 /*File[] files = new File(inputPath).listFiles();
		 for (File f: files) {
			 System.out.println(f.getName());
		
		 getDocuments(f);
		 }*/}
		
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
				String name="";
				if(f.getName().contains(".xml"))
				 name = f.getName().substring(0, f.getName().lastIndexOf(".xml"));
				else
					name = f.getName().substring(0, f.getName().lastIndexOf(".txt"));
				Path file = Paths.get(saveDir+name+".txt");
				File fileCreate = new File(saveDir+name+".txt");
				try {
					fileCreate.createNewFile();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
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
						//content = content.replace("\n", " ");
						String finalContent=title+" "+date+" "+content;
						finalContent = finalContent.replace("\n", " ");
						finalContent=finalContent+" ";
						
						try {
							Files.write(file, finalContent.getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						}
				System.out.println(file.getFileName());
				
	}
}