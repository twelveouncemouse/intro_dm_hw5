package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.io.Serializable;
import java.util.Date;

public class Tweet implements Serializable {
    // An optimal prime number to generate hash codes
    private static final int optimusPrime = 31;

    private final String content;
    private final Date timestamp;
    private final int favoriteCount;
    private final int retweetCount;
    private final String lang;

    public Tweet(String content, Date timestamp, int favoriteCount, int retweetCount, String lang) {
        this.content = content;
        this.timestamp = timestamp;
        this.favoriteCount = favoriteCount;
        this.retweetCount = retweetCount;
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tweet) {
            Tweet otherTweet = (Tweet)o;
            return this.content.equals(otherTweet.content) &&
                    this.timestamp.equals(otherTweet.timestamp);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // Start with a non-zero constant. Prime is preferred
        int result = 17;
        result = Tweet.optimusPrime * result + timestamp.hashCode();
        result = Tweet.optimusPrime * result + content.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Tweet { ");
        builder.append(String.format("Language: '%s' , ", lang));
        builder.append(String.format("Timestamp: '%s' , ", timestamp.toString()));
        builder.append(String.format("Content: '%s' , ", content));
        builder.append(String.format("favoriteCount: '%d' , ", favoriteCount));
        builder.append(String.format("retweetCount: '%d' }", retweetCount));
        return builder.toString();
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getLang() {
        return lang;
    }
}
