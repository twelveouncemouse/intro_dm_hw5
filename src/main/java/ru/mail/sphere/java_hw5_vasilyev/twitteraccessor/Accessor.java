/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mail.sphere.java_hw5_vasilyev.Program;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Roman Vasilyev
 */
public class Accessor {
    private static final String OAUTH_CONSUMER_KEY = "foeRC0Oz45PjcH9bei0D8u0BB";
    private static final String OAUTH_CONSUMER_SECRET = "nSETjWJYx43WlS7f9QJ7EHOmPJGJqstSclDXUL44i0ZuetcguO";
    private static final String OAUTH_ACCESS_TOKEN = "708020045776084992-QviJTODqDyOBwXMdvarVjZLNyEF9hQF";
    private static final String OAUTH_ACCESS_TOKEN_SECRET = "GCIGXnkgCO34Xo91221HXRWBY1AlsgqT8oiRpWdmjf3Nd";
    public static final int MAX_QUERY_SIZE = 100;
    
    private static final Configuration config;
    
    static {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(OAUTH_CONSUMER_KEY)
            .setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET)
            .setOAuthAccessToken(OAUTH_ACCESS_TOKEN)
            .setOAuthAccessTokenSecret(OAUTH_ACCESS_TOKEN_SECRET);
        config = cb.build();
    }
    
    private static Tweet buildTweetFromStatus(final Status status) {
        return new Tweet(
            status.getText(),
            status.getCreatedAt(),
            status.getFavoriteCount(),
            status.getRetweetCount(),
            status.getLang()
        );
    }
    
    private static void meetRateLimits(Twitter twitter) throws TwitterException {
        RateLimitStatus rateLimitStatus = 
                    twitter.getRateLimitStatus("search").get("/search/tweets");
        if (rateLimitStatus.getRemaining() == 0) {
            int secondsToWait = rateLimitStatus.getSecondsUntilReset() + 1;
            System.out.println(
                String.format("Rate limit exceeded, waiting %d seconds...", secondsToWait));
            try {
                Thread.sleep(secondsToWait * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Accessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static Query buildSearchQuery(String query, Date since, Date until, String lang, int querySize) {
        Query queryObject = new Query(query);
        queryObject.setCount(querySize <= MAX_QUERY_SIZE ?
            querySize :
            MAX_QUERY_SIZE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        queryObject.setSince(dateFormat.format(since));
        if (until != null) {
            queryObject.setUntil(dateFormat.format(until));
        }
        if (lang != null) {
            queryObject.setLang(lang);
        }
        return queryObject;
    }
    
    public static Collection<Tweet> search(String query, Date since, int querySize) {
        return search(query, since, null, null, querySize);
    }
    
    public static Collection<Tweet> search(String query, Date since, Date until, String lang, int querySize) {
        Twitter twitter = new TwitterFactory(config).getInstance();
        Collection<Tweet> methodResult = new ArrayList<>();
        try {
            Accessor.meetRateLimits(twitter);
            Query queryObject = Accessor.buildSearchQuery(query, since, until, lang, querySize);
            QueryResult result = twitter.search(queryObject);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                methodResult.add(Accessor.buildTweetFromStatus(tweet));
            }
        }
        catch (TwitterException te) {
            System.err.println(String.format("TwitterException occured: %s", te.getMessage()));
            System.err.println(Arrays.toString(te.getStackTrace()));
            System.err.println(Program.EXTERMINATUS);
        }
        return methodResult;
    }
}
