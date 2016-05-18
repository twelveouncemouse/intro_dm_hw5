/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Администратор
 */
public class TweetTest {
    private Tweet testTweet1, testTweet2;
    
    @Before
    public void setUp() {
        testTweet1 = new Tweet("Linux is cumbersome and for sorcerors only. Use Windows",
                Date.from(Instant.now()), 3, 2, "en");
        testTweet2 = new Tweet("M$ must die, Windows suxx. Linux forever!",
                Date.from(Instant.now()), 11, 3, "en");
    }

    @Test
    public void testEquals() {
        System.out.println("equals");        
        Date now = Date.from(Instant.now());
        Tweet someTweet = new Tweet("sample", now, 0, 0, "en");
        Tweet someEqualTweet = new Tweet("sample", now, 0, 0, "en");
        boolean result = someTweet.equals(someEqualTweet);
        boolean expResult = true;
        assertEquals(expResult, result);
        result = testTweet1.equals(testTweet2);
        expResult = false;
        assertEquals(expResult, result);
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Date now = Date.from(Instant.now());
        Tweet someTweet = new Tweet("sample", now, 0, 0, "en");
        Tweet someEqualTweet = new Tweet("sample", now, 0, 0, "en");
        int h1 = someTweet.hashCode();
        int h2 = someEqualTweet.hashCode();
        assertEquals(h1, h2);
    }

    @Test
    public void testGetContent() {
        System.out.println("getContent");
        String expResult = "Linux is cumbersome and for sorcerors only. Use Windows";
        String result = testTweet1.getContent();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        Date now = Date.from(Instant.now());
        Tweet someTweet = new Tweet("sample", now, 0, 0, "en");
        Date result = someTweet.getTimestamp();
        Date expResult = now;
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFavoriteCount() {
        System.out.println("getFavoriteCount");
        Tweet instance = testTweet1;
        int expResult = 3;
        int result = instance.getFavoriteCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetRetweetCount() {
        System.out.println("getRetweetCount");
        Tweet instance = testTweet1;
        int expResult = 2;
        int result = instance.getRetweetCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetLang() {
        System.out.println("getLang");
        Tweet instance = testTweet1;
        String expResult = "en";
        String result = instance.getLang();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSetOfTweets() {
        System.out.println("setOfTweets");
        Set<Tweet> set = new HashSet<>();
        Date now = Date.from(Instant.now());
        Tweet someTweet = new Tweet("sample", now, 0, 0, "en");
        Tweet someEqualTweet = new Tweet("sample", now, 0, 0, "en");
        boolean result = set.add(someTweet);
        boolean expResult = true;
        assertEquals(expResult, result);
        result = set.add(someEqualTweet);
        expResult = false;
        assertEquals(expResult, result);
        result = set.add(testTweet1);
        expResult = true;
        assertEquals(expResult, result);
    }    
}
