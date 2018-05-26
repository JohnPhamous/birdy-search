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

@SpringBootApplication
public class Main {
    public static LuceneWrapper luceneWrapper;
    public static IndexReader indexReader;

    public static void main(String[] args) throws IOException {
        File dataDir = new File("data");
        File indexDir = new File("index");

        if(!indexDir.exists())
            indexDir.mkdir();
        luceneWrapper = new LuceneWrapper(indexDir);

        if(indexDir.isDirectory() && indexDir.listFiles().length == 0) {
            try(IndexWriter iw = luceneWrapper.createIndexWriter()) {
                for(File f : dataDir.listFiles()) {
                    String fileName = f.getName();
                    if(!fileName.substring(fileName.lastIndexOf(".") + 1).equals("json"))
                        continue;
                    System.out.println("Processing: " + f.getName());
                    addTweetsFromFile(luceneWrapper, iw, f);
                }
            }
        } else {
            System.out.println("Using existing index.");
        }
        indexReader = luceneWrapper.createIndexReader();

        SpringApplication.run(Main.class, args);
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
