/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Roman Vasilyev
 * @param <T>
 */
public class TimestampTweetComparator<T extends Tweet> implements Comparator<T> {

    @Override
    public int compare(T tweet1, T tweet2) {
        Date ts1 = tweet1.getTimestamp(), ts2 = tweet2.getTimestamp();
        return ts1.compareTo(ts2);
    }
    
}
