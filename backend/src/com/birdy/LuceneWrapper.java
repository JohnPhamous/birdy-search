package com.birdy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LuceneWrapper {
    public Analyzer analyzer;
    protected Directory index;
    protected IndexWriterConfig config;

    public LuceneWrapper(File indexDir) throws IOException {
        Map<String,Analyzer> analyzerPerField = new HashMap<>() {{
            put("user", new KeywordAnalyzer());
            put("hashtags", new KeywordAnalyzer());
        }};
        analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
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

        if(t.entities != null && t.entities.hashtags != null) {
            for(HashTag hashtag : t.entities.hashtags) {
                doc.add(new StringField("hashtags", '#' + hashtag.text, TextField.Store.YES));
            }
        }

        if(t.title != null)
            doc.add(new TextField("title", t.title, TextField.Store.YES));
        if(t.location != null) {
            doc.add(new LatLonPoint("location", t.location.lat, t.location.lng));
            doc.add(new StoredField("lat", t.location.lat));
            doc.add(new StoredField("lng", t.location.lng));
        }

        writer.addDocument(doc);
    }

    public TweetResponse search(IndexReader reader, Query q, int numResults, int offset) throws IOException, ParseException {
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(1000);
        searcher.search(q, collector);
        TopDocs topDocs = collector.topDocs(offset, numResults);

        List<Document> docList = new ArrayList<>();
        for(ScoreDoc doc : topDocs.scoreDocs) {
            docList.add(searcher.doc(doc.doc));
        }
        List<Tweet> tweets = docList
                .stream()
                .map(Tweet::fromDocument)
                .collect(Collectors.toList());
        TweetResponse response = new TweetResponse(tweets, topDocs.totalHits);
        return response;
    }

//    public TweetResponse search(IndexReader reader, String query, int numResults) throws IOException, ParseException {
//        Query q = new QueryParser("text", analyzer).parse(query);
//        return search(reader, q, numResults);
//    }

    public static void printTweetDocument(Document tweet) {
        System.out.println("id: " + tweet.get("id"));
        System.out.println("text: " + tweet.get("text"));
        System.out.println("user: " + tweet.get("user"));
        System.out.println("title: " + tweet.get("title"));
        System.out.println("timestamp: " + tweet.get("timestamp"));
        System.out.println("location: " + "<" + tweet.get("lat") + ", " + tweet.get("lng") + ">");
        System.out.println("hashtags: " + tweet.get("hashtags"));
    }
}
