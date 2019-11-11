package com.xatkit.plugins.emf;

import com.xatkit.Xatkit;

public class BotTest {

    public static void main(String[] args) {
        Xatkit.main(new String[]{"<Your .properties file>"});
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
