package edu.wpi.cs3733d18.teamF.controller;

public class Encryption {
    public static String encryptSHA256(String input) {
        input = "MA" + input + "TT";
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(input);
    }
}
