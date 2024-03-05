package com.icebreaker.utils;

import java.security.MessageDigest;

public class HashUserId {

    public static StringBuilder hashUserId(String name, MessageDigest md, int newUserID) {
        String nameID = name + newUserID;
        md.update(nameID.getBytes());
        byte[] userBytes = md.digest();
        StringBuilder usb = new StringBuilder();
        for (byte hashByte : userBytes) {
            usb.append(String.format("%02x", hashByte));
        }
        return usb;
    }

}
