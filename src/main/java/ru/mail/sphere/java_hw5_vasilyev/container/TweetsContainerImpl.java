/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.container;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.FavouriteCountTweetComparator;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.TimestampTweetComparator;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.Tweet;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;
import static ru.mail.sphere.java_hw5_vasilyev.Program.EXTERMINATUS;

/**
 *
 * @author Администратор
 * @param <T>
 */
public class TweetsContainerImpl<T extends Tweet> implements TweetsContainer<T> {
    private static final int optimusPrime = 31;
    private final List<T> tweetsList = new ArrayList<>();
    
    @Override
    public boolean add(T tweet) {
        if (tweet == null) {
            System.out.println("Warning! Trying to add 'null' to a TweetsContainer");
            return false;
        }
        else {
            int oldHashCode = hashCode();
            int hashOfElement = tweet.hashCode();
            for (Tweet existing : tweetsList) {
                if (existing.hashCode() == hashOfElement) {
                    return false;
                }
            }
            synchronized(this) {
                tweetsList.add(tweet);
            }
            return hashCode() != oldHashCode;
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> tweets) {
        int oldHashCode = hashCode();
        for (T tweet : tweets) {
            add(tweet);
        }
        return hashCode() != oldHashCode;
    }

    @Override
    public boolean remove(T tweet) {
        if (tweet == null) {
            System.out.println("Warning! Trying to remove 'null' from a TweetsContainer");
            return false;
        }
        else {
            int oldHashCode = hashCode();
            synchronized(this) {
                tweetsList.remove(tweet);
            }
            return hashCode() != oldHashCode;
        }
    }

    @Override
    public void clear() {
        synchronized(this) {
            tweetsList.clear();
        }
    }

    @Override
    public Tweet getOldest() {
        return Collections.min(tweetsList, new TimestampTweetComparator<>());
    }

    @Override
    public Tweet getTopRated() {
        return Collections.max(tweetsList, new FavouriteCountTweetComparator<>());
    }

    @Override
    public void sort(Comparator<T> comparator) {
        synchronized(this) {
            tweetsList.sort(comparator);
        }
    }

    @Override
    public Map<String, Collection<T>> groupByLang() {
        Map<String, Collection<T>> result = new HashMap<>();
        for (T tweet : tweetsList) {
            if (result.containsKey(tweet.getLang())) {
                Collection<T> chain = result.get(tweet.getLang());
                chain.add(tweet);
            }
            else {
                Collection<T> chain = new ArrayList<>();
                chain.add(tweet);
                result.put(tweet.getLang(), chain);
            }
        }
        return result;
    }

    @Override
    public Map<String, Double> getTagCloud(String lang) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Collection<T>> langHash = this.groupByLang();
        Collection<T> tweetsOfLanguage = langHash.get(lang);

        for (Tweet tweet : tweetsOfLanguage) {
            Map<String, Double> tweetStats = getTweetStatistics(tweet);
            for (String token : tweetStats.keySet()) {
                result.put(token, result.getOrDefault(token, 0.0) + tweetStats.get(token));
            }
        }
        
        for (String token : result.keySet()) {
            result.put(token, result.get(token) / tweetsOfLanguage.size());
        }
        return result;
    }

    private Map<String, Double> getTweetStatistics(Tweet tweet) {
        Map<String, Double> tweetStats = new HashMap<>();
        try {
            StandardTokenizer tokenizer = new StandardTokenizer(
                Version.LUCENE_36,
                AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY,
                new StringReader(tweet.getContent())
            );
            tokenizer.reset();
            CharTermAttribute attribute = tokenizer.addAttribute(CharTermAttribute.class);
            int countOfMeanings = 0;
            while (tokenizer.incrementToken()) {
                String currentToken = attribute.toString();
                // Check if token is not shorter than 3 characters
                if (currentToken.length() >= 3) {
                    tweetStats.put(currentToken, tweetStats.getOrDefault(currentToken, 0.0) + 1.0);
                    countOfMeanings++;
                }
            }
            // Normalization
            if (countOfMeanings > 0) {
                for (String key : tweetStats.keySet()) {
                    tweetStats.put(key, tweetStats.get(key) / countOfMeanings);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TweetsContainerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tweetStats;
    }
    
    @Override
    public Iterator<T> iterator() {
        return tweetsList.iterator();
    }
    
    @Override
    public int hashCode() {
        // Start with a non-zero constant. Prime is preferred
        int result = 17;
        for (Tweet elem : tweetsList) {
            result = TweetsContainerImpl.optimusPrime * result + elem.hashCode();
        }
        return result;
    }
    
    public void saveToFile(String path) {
        try {
            try (FileOutputStream fos = new FileOutputStream(path)) {
                try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(this);
                    oos.flush();
                    oos.close();
                }
            }
        }
        catch (IOException io_exc) {
            System.err.println(String.format("Error occured while serializing: ", io_exc.getMessage()));
            System.err.println(EXTERMINATUS);
        }
    }
    
    public void restoreFromFile(String path) {
        try {
            try (FileInputStream file_is = new FileInputStream(path)) {
                try (ObjectInputStream ois = new ObjectInputStream(file_is)) {
                    Object obj = ois.readObject();
                    if (obj instanceof TweetsContainerImpl) {
                        Object sync = new Object();
                        synchronized(sync) {
                            TweetsContainerImpl<T> restored = (TweetsContainerImpl<T>)obj;
                            this.clear();
                            this.addAll(restored.tweetsList);
                        }
                    }
                    ois.close();
                }
            }
        }
        catch (ClassNotFoundException | IOException exc) {
            System.err.println(String.format("Error occured while deserializing: ", exc.getMessage()));
            System.err.println(EXTERMINATUS);
        }
    }
    
    public int size() {
        return tweetsList.size();
    }
}
