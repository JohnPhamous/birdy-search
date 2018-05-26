package com.birdy;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
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
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneWrapper {
    protected StandardAnalyzer analyzer;
    protected Directory index;
    protected IndexWriterConfig config;

    public LuceneWrapper(File indexDir) throws IOException {
        analyzer = new StandardAnalyzer();
        index = FSDirectory.open(indexDir.toPath());
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

        doc.add(new TextField("text", t.text, TextField.Store.YES));
        doc.add(new StringField("id", t.id_str, TextField.Store.YES));
        doc.add(new StringField("user", t.user.screen_name, TextField.Store.YES));
        doc.add(new DoublePoint("time", t.timestamp));
        doc.add(new StoredField("timestamp", t.timestamp_ms));

        if(t.title != null)
            doc.add(new TextField("title", t.title, TextField.Store.YES));
        if(t.location != null) {
            doc.add(new LatLonPoint("location", t.location.lat, t.location.lng));
            doc.add(new StoredField("lat", t.location.lat));
            doc.add(new StoredField("lng", t.location.lng));
        }

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
        System.out.println("user: " + tweet.get("user"));
        System.out.println("title: " + tweet.get("title"));
        System.out.println("timestamp: " + tweet.get("timestamp"));
        System.out.println("location: " + "<" + tweet.get("lat") + ", " + tweet.get("lng") + ">");
    }
}
