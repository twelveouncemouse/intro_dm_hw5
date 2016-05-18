package ru.mail.sphere.java_hw5_vasilyev;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mail.sphere.java_hw5_vasilyev.container.TweetsContainerImpl;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.*;

// I wish I could write some more unit tests, but I don't want to ;)

public class Program {
    private static final String REAL_MADRID_TAG = "Real Madrid";
    private static final String ATLETICO_MADRID_TAG = "Atletico Madrid";
    private static final String SAVED_CACHE_REAL_MADRID_FILENAME = "cache_realmadrid.sav";
    private static final String SAVED_CACHE_ATLETICO_MADRID_FILENAME = "cache_atlmadrid.sav";
    private static final int TWEETS_COUNT = 5000;
    private static final int DELAY_BETWEEN_REQUESTS = 60;
    private static final String START_DATE = "01.05.2016";
    private static final String STATS_FILENAME = "stats.csv";
    
    public static final String EXTERMINATUS = "By the power vested in me by the Holy Inquisition I sentence this program to EXTERMINATUS";
    
    public static void main(String[] args) {
        System.out.println("Technosphere Java project");
        final TweetsContainerImpl<Tweet> realMadridData = new TweetsContainerImpl<>();
        final TweetsContainerImpl<Tweet> atleticoMadridData = new TweetsContainerImpl<>();
        Thread realMadridThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchTweets(realMadridData,
                            REAL_MADRID_TAG,
                            SAVED_CACHE_REAL_MADRID_FILENAME,
                            TWEETS_COUNT,
                            DELAY_BETWEEN_REQUESTS);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "RealMadridLoader");
        Thread atleticoMadridThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchTweets(atleticoMadridData,
                            ATLETICO_MADRID_TAG,
                            SAVED_CACHE_ATLETICO_MADRID_FILENAME,
                            TWEETS_COUNT,
                            DELAY_BETWEEN_REQUESTS);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, "AtleticoMadridLoader");
        realMadridThread.start();
        atleticoMadridThread.start();
        
        try {
            realMadridThread.join();
            atleticoMadridThread.join();
            
            // Data processing
            Map<String, Collection<Tweet>> rmGrouped = realMadridData.groupByLang();
            Map<String, Collection<Tweet>> amGrouped = atleticoMadridData.groupByLang();
            Map<String, Point> finalStatistics = new HashMap<>();
            for (String lang : rmGrouped.keySet()) {
                int rmFreq = rmGrouped.get(lang).size();
                int amFreq = 0;
                if (amGrouped.containsKey(lang)) {
                    amFreq = amGrouped.get(lang).size();
                }
                finalStatistics.put(lang, new Point(rmFreq, amFreq));
            }
            for (String lang : amGrouped.keySet()) {
                if (!rmGrouped.containsKey(lang)) {
                    finalStatistics.put(lang, new Point(0, amGrouped.get(lang).size()));
                }
            }
            outputStats(finalStatistics, STATS_FILENAME);
        }
        catch (InterruptedException exc) {
            System.err.println("Threads were interrupted: " + exc.getMessage());
        }
        finally {
            // Worker threads mustn't do anything to the data by this moment
            if (realMadridThread.isAlive()) {
                realMadridThread.interrupt();
            }
            if (atleticoMadridThread.isAlive()) {
                atleticoMadridThread.interrupt();
            }
            realMadridData.saveToFile(SAVED_CACHE_REAL_MADRID_FILENAME);
            atleticoMadridData.saveToFile(SAVED_CACHE_ATLETICO_MADRID_FILENAME);
        }
    }
    
    private static void fetchTweets(
            TweetsContainerImpl<Tweet> container,
            String tag,
            String cacheFilename,
            int count,
            int delayInSeconds) throws InterruptedException {
        File savFile = new File(cacheFilename);
        if (savFile.exists()) {
            container.restoreFromFile(cacheFilename);
        }
        
        Object foo = new Object();
        synchronized (foo) {
            System.out.println(String.format("[%s]: %d tweets read from container", tag, container.size()));
        }
                
        if (container.size() < count) {
            try {                
                // Loading the rest from the Internet
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                do {
                    Collection<Tweet> freshData = 
                            Accessor.search(tag, dateFormatter.parse(START_DATE), Accessor.MAX_QUERY_SIZE);
                    
                    synchronized (foo) {
                        System.out.println(String.format("[%s]: %d fresh tweets loaded", tag, freshData.size()));
                    }
                    
                    for (Tweet freshTweet : freshData) {
                        container.add(freshTweet);
                        if (container.size() == count) {
                            break;
                        }
                    }
                    synchronized (foo) {
                        System.out.println(String.format("[%s]: New size of container = %d", tag, container.size()));
                    }
                    if (container.size() < count) {
                        Thread.sleep(delayInSeconds * 1000);
                    }
                }
                while (container.size() < count);
            }
            catch (ParseException exc) {
                System.err.println("Date was in incorrect format: " + exc.getMessage());
            }
        }
    }
    
    private static void outputStats(Map<String, Point> finalStatistics, String path) {
        try {
            try (PrintWriter outFile = new PrintWriter(path)){
                outFile.println(String.format("\"lang\",\"Real Madrid\",\"Atletico Madrid\""));
                for (String lang : finalStatistics.keySet()) {
                    Point data = finalStatistics.get(lang);
                    outFile.println(String.format("\"%s\",%d,%d", lang, data.x, data.y));
                }
                outFile.close();
            }
        }
        catch (IOException ex) {
            Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
