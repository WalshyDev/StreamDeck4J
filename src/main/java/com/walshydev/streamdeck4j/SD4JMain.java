package com.walshydev.streamdeck4j;

public class SD4JMain {

    public static void main(String[] args) {
        StreamDeck4J streamDeck4J = new StreamDeck4J();

        streamDeck4J.connect(args);
    }
}
