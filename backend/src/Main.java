import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        File f = new File("data/tweets-0.json");
        Scanner s = new Scanner(f);

        LuceneWrapper lc = new LuceneWrapper();
        IndexWriter iw = lc.createIndexWriter();

        while(s.hasNextLine()) {
            String tweetStr = s.nextLine();
            Tweet t = Tweet.parseTweet(tweetStr);
            lc.addTweet(iw, t);
//            System.out.println(tweetStr);
//            System.out.println(Tweet.parseTweet(tweetStr));
        }
        iw.close();
        s.close();

        IndexReader ir = lc.createIndexReader();
        s = new Scanner(System.in);
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
}
