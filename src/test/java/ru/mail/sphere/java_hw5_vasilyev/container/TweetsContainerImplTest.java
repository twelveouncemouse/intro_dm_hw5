/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.container;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Before;
import static org.junit.Assert.*;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.FavouriteCountTweetComparator;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.RetweetCountTweetComparator;
import ru.mail.sphere.java_hw5_vasilyev.twitteraccessor.Tweet;

/**
 *
 * @author Roman Vasilyev
 */
public class TweetsContainerImplTest {
    private static String BACKUP_FILENAME = "temp.sav";
    private Tweet testTweet1, testTweet2, testTweet3, testTweet4;
    private Tweet cloudTest1, cloudTest2, cloudTest3;
    
    @Before
    public void setUp() {
        testTweet1 = new Tweet("Linux is cumbersome and for sorcerors only. Use Windows",
                Date.from(Instant.now()), 3, 2, "en");
        testTweet2 = new Tweet("M$ must die, Windows suxx. Linux forever!",
                Date.from(Instant.now()), 11, 3, "en");
        testTweet3 = new Tweet("Dort, am Boden, in dem Stein blitzt dien Lächeln mir entgegen",
                Date.from(Instant.now()), 1, 4, "de");
        testTweet4 = new Tweet("В жизни есть две трагедии - не получить того, о чем мечтал, и получить это",
                Date.from(Instant.now()), 21, 7, "ru");
        
        cloudTest1 = new Tweet("win, mac win lnx, lnx!",
                Date.from(Instant.now()), 0, 0, "en");
        cloudTest2 = new Tweet("mac mac lnx win lnx win lnx, lnx!",
                Date.from(Instant.now()), 0, 0, "en");
        cloudTest3 = new Tweet("win, mac win lnx, lnx!",
                Date.from(Instant.now()), 0, 0, "de");
    }

    @org.junit.Test
    public void testAdd() {
        System.out.println("add");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        boolean expResult = true;
        boolean result = instance.add(testTweet1);
        assertEquals(expResult, result);
        Iterator<Tweet> itr = instance.iterator();
        Tweet firstTweet = itr.next();
        assertEquals(testTweet1, firstTweet);
    }

    @org.junit.Test
    public void testAddAll() {
        System.out.println("addAll");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        Collection<Tweet> otherCollection = new ArrayList<>();
        otherCollection.add(testTweet1);
        otherCollection.add(testTweet2);
        boolean result = instance.addAll(otherCollection);
        assertEquals(true, result);
        Iterator<Tweet> itr = instance.iterator();
        Tweet firstTweet = itr.next();
        Tweet secondTweet = itr.next();
        assertEquals(testTweet1, firstTweet);
        assertEquals(testTweet2, secondTweet);
    }

    @org.junit.Test
    public void testRemove() {
        System.out.println("remove");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        boolean result = instance.remove(testTweet1);
        assertEquals(true, result);
        Iterator<Tweet> itr = instance.iterator();
        Tweet firstTweet = itr.next();
        assertEquals(testTweet2, firstTweet);
    }

    @org.junit.Test
    public void testClear() {
        System.out.println("clear");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        instance.clear();
        Iterator<Tweet> itr = instance.iterator();
        boolean isNotEmpty = itr.hasNext();
        assertEquals(false, isNotEmpty);
    }

    @org.junit.Test
    public void testGetOldest() {
        System.out.println("getOldest");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        try {
            Thread.sleep(1100);
        }
        catch (InterruptedException interrupt_exc) {
            System.err.println(interrupt_exc.getMessage());
            fail("Test failed due to InterruptedException");
        }
        Tweet newTestTweet = new Tweet("Some new tweet created with delay",
            Date.from(Instant.now()), 3, 4, "en");
        instance.add(newTestTweet);
        instance.add(testTweet1);
        Tweet result = instance.getOldest();
        Tweet expResult = testTweet1;
        assertEquals(expResult, result);
    }

    @org.junit.Test
    public void testGetTopRated() {
        System.out.println("getTopRated");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        instance.add(testTweet3);
        Tweet topRated1 = instance.getTopRated();
        instance.add(testTweet4);
        Tweet topRated2 = instance.getTopRated();
        assertEquals(testTweet2, topRated1);
        assertEquals(testTweet4, topRated2);
    }

    @org.junit.Test
    public void testSort() {
        System.out.println("sort");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        instance.add(testTweet3);
        instance.add(testTweet4);
        instance.sort(new FavouriteCountTweetComparator<>());
        Iterator<Tweet> itr = instance.iterator();
        assertEquals(testTweet3, itr.next());
        assertEquals(testTweet1, itr.next());
        assertEquals(testTweet2, itr.next());
        assertEquals(testTweet4, itr.next());
        instance.sort(new RetweetCountTweetComparator<>());
        itr = instance.iterator();
        assertEquals(testTweet1, itr.next());
        assertEquals(testTweet2, itr.next());
        assertEquals(testTweet3, itr.next());
        assertEquals(testTweet4, itr.next());
    }

    @org.junit.Test
    public void testGroupByLang() {
        System.out.println("groupByLang");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        instance.add(testTweet3);
        instance.add(testTweet4);
        Map<String, Collection<Tweet>> expResult = new HashMap<>(8);
        ArrayList<Tweet> englishTweets = new ArrayList<>();
        ArrayList<Tweet> germanTweets = new ArrayList<>();
        ArrayList<Tweet> russianTweets = new ArrayList<>();
        englishTweets.add(testTweet1);
        englishTweets.add(testTweet2);
        germanTweets.add(testTweet3);
        russianTweets.add(testTweet4);
        expResult.put("en", englishTweets);
        expResult.put("de", germanTweets);
        expResult.put("ru", russianTweets);
        Map<String, Collection<Tweet>> result = instance.groupByLang();
        assertEquals(expResult, result);
    }

    @org.junit.Test
    public void testGetTagCloud() {
        System.out.println("getTagCloud");
        String lang = "en";
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(cloudTest1);
        instance.add(cloudTest2);
        instance.add(cloudTest3);        
        Map<String, Double> expResult = new HashMap<>();
        expResult.put("mac", 0.225);
        expResult.put("win", 0.325);
        expResult.put("lnx", 0.45);
        Map<String, Double> result = instance.getTagCloud(lang);
        assertEquals(expResult, result);
    }

    @org.junit.Test
    public void testIterator() {
        System.out.println("iterator");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        ArrayList<Tweet> lst = new ArrayList<>();
        lst.add(testTweet1);
        lst.add(testTweet2);
        lst.add(testTweet3);
        lst.add(testTweet4);
        instance.addAll(lst);
        Iterator<Tweet> itr = instance.iterator();
        int idx = 0;
        while (itr.hasNext()) {
            Tweet elem = itr.next();
            assertEquals(lst.get(idx++), elem);
        }
    }   
    
    @org.junit.Test
    public void testSerialization() {
        System.out.println("serialization");
        TweetsContainerImpl instance = new TweetsContainerImpl();
        instance.add(testTweet1);
        instance.add(testTweet2);
        instance.add(testTweet3);
        instance.add(testTweet4);
        int oldHashCode = instance.hashCode();
        instance.saveToFile(BACKUP_FILENAME);
        TweetsContainerImpl secondInstance = new TweetsContainerImpl();
        secondInstance.restoreFromFile(BACKUP_FILENAME);
        int newHashCode = secondInstance.hashCode();
        assertEquals(oldHashCode, newHashCode);
    } 
}
