/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.util.Comparator;

/**
 *
 * @author Администратор
 * @param <T>
 */
public class RetweetCountTweetComparator <T extends Tweet> implements Comparator<T> {

    @Override
    public int compare(T tweet1, T tweet2) {
        return tweet1.getRetweetCount() - tweet2.getRetweetCount();
    }
    
}
