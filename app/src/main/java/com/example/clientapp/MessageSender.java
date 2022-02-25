package com.example.clientapp;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

public class MessageSender extends Thread {
    ByteBuffer byteBuffer;
    String message;
    InetSocketAddress serverAddress;
    ReentrantLock locker;
    boolean connectionStatus;

    MessageSender(String message, InetSocketAddress serverAddress, ReentrantLock locker, boolean connectionStatus) {
        this.message = message;
        this.serverAddress = serverAddress;
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.locker = locker;
        this.connectionStatus = connectionStatus;
    }

    public void run() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(serverAddress);
            socketChannel.write(byteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
            byteBuffer.clear();
            socketChannel.close();
        } catch (IOException e) {
            try {
                connectionStatus = false;
            } catch (Exception er) {
                System.out.println(er.getMessage());
            }
            System.out.println(e.getMessage());
        }
    }
}
