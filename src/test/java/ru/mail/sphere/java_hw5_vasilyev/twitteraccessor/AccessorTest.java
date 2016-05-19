/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.mail.sphere.java_hw5_vasilyev.twitteraccessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Администратор
 */
public class AccessorTest {

    @Test
    public void testSearch_5args() {
        System.out.println("search");
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            Collection<Tweet> data = Accessor.search(
                    "RealMadrid",
                    dateFormatter.parse("16.05.2016"),
                    dateFormatter.parse("17.05.2016"),
                    null,
                    100);
            assertTrue(data.size() > 0);
        }
        catch (ParseException exc) {
            System.err.println("ParseException?! It couldn't be true! " + exc.getMessage());
            fail();
        }
    }
    
}
