package com.example.clientapp;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;


public class MainActivity extends AppCompatActivity {

    InetSocketAddress serverAddress = null;
    boolean connectionStatus = false;
    ReentrantLock locker = new ReentrantLock(); // ? ? ?
    TextView textView;
    LinearLayout linearLayout;
    private final String BROADCAST_IP = "255.255.255.255";
    private final int BROADCAST_SERVER_PORT = 65431;
    private static int serverPort;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.connectionStatus);
        linearLayout = findViewById(R.id.linear_layout);

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        serverDisconnected();
                        break;
                    case 1:
                        serverConnected();
                        break;
                }
            }
        };
        handler.sendEmptyMessage(0);

        serviceCheckConnectionStatus();


    }

    public void serverConnected() {
        textView.setText(R.string.connection_status_on);
        textView.setTextColor(getResources().getColor(R.color.green));
        connectionStatus = true;
    }

    public void serverDisconnected() {
        textView.setText(R.string.connection_status_off);
        textView.setTextColor(getResources().getColor(R.color.red));
        connectionStatus = false;
    }


    public void serviceCheckConnectionStatus() {
        Thread checkConnectionStatus = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        while (true) {
                            if (!connectionStatus) {
                                serviceGetServerAddress();
                            }
                            try {
                                sleep(20000); // 20 sec
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        checkConnectionStatus.start();
    }


    public void serviceGetServerAddress() {
        Thread getServerAddress = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        try {
                            DatagramSocket datagramSocket = new DatagramSocket();
                            datagramSocket.setBroadcast(true);
                            byte[] sendByte = new byte[512];

                            sendByte = "Android need Server Address".getBytes(StandardCharsets.UTF_8);

                            DatagramPacket datagramPacketSend = new DatagramPacket(sendByte, sendByte.length,
                                    InetAddress.getByName(BROADCAST_IP), BROADCAST_SERVER_PORT);
                            DatagramPacket datagramPacketReceive = new DatagramPacket(sendByte, sendByte.length);

                            datagramSocket.send(datagramPacketSend);

                            datagramSocket.receive(datagramPacketReceive);


                            String tcpPort = "";

                            if (datagramPacketReceive.getAddress() != null) {
                                connectionStatus = true;
                                handler.sendEmptyMessage(1);
                                byte[] receiveData = datagramPacketReceive.getData();
                                for (int i = 0; i < datagramPacketReceive.getLength(); i++)
                                    tcpPort = tcpPort + (char) receiveData[i];
                                serverPort = Integer.valueOf(tcpPort);
                                serverAddress = new InetSocketAddress(datagramPacketReceive.getAddress(), serverPort);
                                System.out.printf("\n...............Got Server Address %s:%s \n", datagramPacketReceive.getAddress(), serverPort);

                            } else {
                                connectionStatus = false;
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            System.out.println("\n-------------------< E R R O R >---------------------");
                            e.printStackTrace();
                            System.out.println("-----------------------------------------------------");
                        }
                    }
                }
        );
        getServerAddress.start();
    }


    public void sendMessage(View view) {
        EditText message = findViewById(R.id.messageBox);
        message.requestFocus();
        try {
            if (connectionStatus && message.getText().length() > 0) {
                new MessageSender(message.getText().toString(), this.serverAddress, this.locker, this.connectionStatus).start();
                TextView textView = new TextView(this);
                textView.setText(message.getText());
                //textView.setBackgroundColor(getResources().getColor(R.color.white));
                //textView.setPadding(5,5,5,5);
                textView.setTextSize(15);
                textView.setWidth(ConstraintLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(textView);
                message.setText("");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}