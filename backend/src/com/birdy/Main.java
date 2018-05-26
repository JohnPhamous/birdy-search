package com.birdy;

import com.google.gson.JsonSyntaxException;
import org.apache.lucene.index.IndexWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class Main {
    @RequestMapping("/")
    @ResponseBody
    String getTweets() {
        return "Test";
    }

    public static void main(String[] args) throws IOException {
        File dataDir = new File("data");
        File indexDir = new File("index");

        if(!indexDir.exists())
            indexDir.mkdir();
        LuceneWrapper lc = new LuceneWrapper(indexDir);

        if(indexDir.isDirectory() && indexDir.listFiles().length == 0) {
            try(IndexWriter iw = lc.createIndexWriter()) {
                for(File f : dataDir.listFiles()) {
                    String fileName = f.getName();
                    if(!fileName.substring(fileName.lastIndexOf(".") + 1).equals("json"))
                        continue;
                    System.out.println("Processing: " + f.getName());
                    addTweetsFromFile(lc, iw, f);
                }
            }
        } else {
            System.out.println("Using existing index.");
        }

        SpringApplication.run(Main.class, args);

//        Scanner s = new Scanner(System.in);
//        try(IndexReader ir = lc.createIndexReader()) {
//            while(true) {
//                System.out.print("Enter a query: ");
//                try {
//                    String qs = s.nextLine();
//                    if(qs.isEmpty()) break;
//                    List<Document> res = lc.search(ir, qs, 10);
//                    for(int i = 0; i < res.size(); ++i) {
//                        System.out.println("====== Result #" + (i + 1) + " ======");
//                        birdy.LuceneWrapper.printTweetDocument(res.get(i));
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    System.out.println("Invalid query.");
//                }
//            }
//        }
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        return args -> {
//
//            System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            Arrays.sort(beanNames);
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
//
//        };
//    }

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
