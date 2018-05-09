import com.google.gson.JsonSyntaxException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        File dataDir = new File("data");
        File indexDir = new File("index");

        if(!indexDir.exists())
            indexDir.mkdir();
        LuceneWrapper lc = new LuceneWrapper(indexDir);

        if(indexDir.isDirectory() && indexDir.listFiles().length == 0) {
            IndexWriter iw = lc.createIndexWriter();

            for(File f : dataDir.listFiles()) {
                String fileName = f.getName();
                if(!fileName.substring(fileName.lastIndexOf(".") + 1).equals("json"))
                    continue;
                System.out.println("Processing: " + f.getName());
                addTweetsFromFile(lc, iw, f);
            }
            iw.close();
        } else {
            System.out.println("Using existing index.");
        }

        IndexReader ir = lc.createIndexReader();
        Scanner s = new Scanner(System.in);
        while(true) {
            System.out.print("Enter a query: ");
            try {
                String qs = s.nextLine();
                if(qs.isEmpty()) break;
                List<Document> res = lc.search(ir, qs, 10);
                for(int i = 0; i < res.size(); ++i) {
                    System.out.println("====== Result #" + (i + 1) + " ======");
                    LuceneWrapper.printTweetDocument(res.get(i));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Invalid query.");
            }
        }
    }

    public static void addTweetsFromFile(LuceneWrapper lw, IndexWriter iw, File f) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            int line = 0;
            for(String tweetStr; (tweetStr = br.readLine()) != null; line++) {
                try {
                    Tweet t = Tweet.parseTweet(tweetStr);
                    lw.addTweet(iw, t);
                } catch(JsonSyntaxException e) {
                    System.out.println("Failed to parse tweet #" + line + ": " + tweetStr);
                    e.printStackTrace();
                }
            }
        }
    }
}
