package com.birdy;

import com.google.gson.JsonSyntaxException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class Main {
    public static LuceneWrapper luceneWrapper;
    public static IndexReader indexReader;
    public static final double STARTING_RANK = 1;
    public static final double EPSILON = 0.0001;
    public static final int MAX_ITERATIONS = 1000000000;

    public static void main(String[] args) throws IOException {
        File dataDir = new File("data");
        File indexDir = new File("index");

        if(!indexDir.exists())
            indexDir.mkdir();
        luceneWrapper = new LuceneWrapper(indexDir);
        List<Tweet> tweets = new LinkedList<>();
        Map<String, TweetNode> graph = new HashMap<>();

        if(indexDir.isDirectory() && indexDir.listFiles().length == 0) {
            try(IndexWriter iw = luceneWrapper.createIndexWriter()) {
                for(File f : dataDir.listFiles()) {
                    String fileName = f.getName();
                    if(!fileName.substring(fileName.lastIndexOf(".") + 1).equals("json"))
                        continue;
                    System.out.println("Processing: " + f.getName());
                    readTweetsFromFile(f, tweets, graph);
//                    addTweetsFromFile(luceneWrapper, iw, f);
                }
            }

            System.out.println("Running PageRank");
            AtomicReference<Double> diff = new AtomicReference<>((double) 1);
            int iterations = 0;
            double d = 0.85;

            while(diff.get() > EPSILON || iterations > MAX_ITERATIONS) {
                diff.set((double) 0);
                graph.forEach((id, node) -> {
                    if(node.quoted != null) {
                        graph.get(node.quoted).rank += node.rank;
                        diff.updateAndGet(v -> v + node.rank);
                    } else {
                        graph.forEach((subID, subNode) -> {
                            subNode.rank += d * (node.rank / graph.size());
                            diff.updateAndGet(v -> d * (v + node.rank / graph.size()));
                        });
                    }
                });
                iterations++;
            }

            System.out.println("Done with PageRank after " + iterations + " iterations.");
        } else {
            System.out.println("Using existing index.");
        }


        indexReader = luceneWrapper.createIndexReader();

        SpringApplication.run(Main.class, args);
    }

    public static List<Tweet> readTweetsFromFile(File f, List<Tweet> tweets, Map<String, TweetNode> graph) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            int line = 0;
            for(String tweetStr; (tweetStr = br.readLine()) != null; line++) {
                try {
                    Tweet t = Tweet.parseTweet(tweetStr);
                    tweets.add(t);
                    if(!graph.containsKey(t.id_str))
                        graph.put(t.id_str, new TweetNode(STARTING_RANK, t.quoted_status_id_str));

                    if(t.quoted_status_id_str != null && !graph.containsKey(t.quoted_status_id_str))
                        graph.put(t.quoted_status_id_str, new TweetNode(STARTING_RANK, null));

                } catch(JsonSyntaxException e) {
                    System.out.println("Failed to parse tweet #" + line + ": " + tweetStr);
                    e.printStackTrace();
                }
            }
        }
        return tweets;
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
