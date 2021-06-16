package com.java.kaboome.presentation.helpers;

import java.util.UUID;

public class MessageIdGenerator {

    public static String getNewMessageId(){
        return UUID.randomUUID().toString();
    }
}
