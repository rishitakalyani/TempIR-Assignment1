package AssignFourTempIR;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import javax.imageio.stream.ImageOutputStreamImpl;

import org.apache.log4j.Logger;

/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
public class AssignFour {

    private static Logger log = Logger.getLogger(AssignFour.class);
    static String pathName="/Users/Rishita/Downloads/Temporalia_Sample3";
    static String savePathName="/Users/Rishita/ITIS/word2Vec.txt";
    InputStream finalInputStream = null;
    static String content = "";
	static String date = "";
	static String title = "";
	static String id = "";
	static int count =0;
	static Word2Vec vec;

    public static void main(String[] args) throws Exception {

        // Gets Path to Text file
        File[] files = new File(pathName).listFiles();
        for(File f: files){
        	if(count==0)
        		count++;
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
			String txtDoc ="";
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
					txtDoc=txtDoc+" "+date+" "+title+" "+content;
					
			}
        //String filePath = new ClassPathResource(f.getName()).getFile().getAbsolutePath();
            InMemoryLookupCache cache = new InMemoryLookupCache();
            WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
                    .vectorLength(100)
                    .useAdaGrad(false)
                    .cache(cache)
                    .lr(0.025f).build();
        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(new ByteArrayInputStream(txtDoc.getBytes(StandardCharsets.UTF_8)));
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();

        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        
        if(count==1){
        vec = new Word2Vec.Builder()
            .layerSize(500)
            .windowSize(5)
            .seed(42)
            .lookupTable(table)
            .vocabCache(cache)
            .iterate(iter)
            .tokenizerFactory(t)
            .build();
        }
        else{
        	vec = WordVectorSerializer.loadFullModel(savePathName);
        	vec.setTokenizerFactory(t);
        	vec.setSentenceIter(iter);
        }

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        // Write word vectors to file
        WordVectorSerializer.writeWordVectors(vec, savePathName);

        
    }
     // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("day", 10);
        System.out.println("10 Words closest to 'day': " + lst);

        // TODO resolve missing UiServer
//        UiServer server = UiServer.getInstance();
//        System.out.println("Started on port " + server.getPort());
        }
}
