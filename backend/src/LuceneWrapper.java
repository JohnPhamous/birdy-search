import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneWrapper {
    protected StandardAnalyzer analyzer;
    protected Directory index;
    protected IndexWriterConfig config;

    public LuceneWrapper() {
        analyzer = new StandardAnalyzer();
        index = new RAMDirectory();
        config = new IndexWriterConfig(analyzer);
    }

    public IndexWriter createIndexWriter() throws IOException {
        return new IndexWriter(index, config);
    }

    public DirectoryReader createIndexReader() throws IOException {
        return DirectoryReader.open(index);
    }

    public void addTweet(IndexWriter writer, Tweet t) throws IOException {
        Document doc = new Document();
        if(t.title != null)
            doc.add(new TextField("title", t.title, TextField.Store.YES));
        doc.add(new TextField("text", t.text, TextField.Store.YES));
        doc.add(new StringField("id", t.id_str, TextField.Store.YES));
        writer.addDocument(doc);
    }

    public List<Document> search(IndexReader reader, String query, int numResults) throws IOException, ParseException {
        Query q = new QueryParser("text", analyzer).parse(query);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(q, numResults);

        List<Document> docList = new ArrayList<>();
        for(ScoreDoc doc : topDocs.scoreDocs) {
            docList.add(searcher.doc(doc.doc));
        }
        return docList;
    }

    public static void printTweetDocument(Document tweet) {
        System.out.println("id: " + tweet.get("id"));
        System.out.println("text: " + tweet.get("text"));
    }
}
