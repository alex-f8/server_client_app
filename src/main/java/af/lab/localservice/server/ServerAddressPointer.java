package af.lab.localservice.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ServerAddressPointer extends Thread {
    int UDP_SERVER_PORT = 65431;
    int serverPort;
    StringBuffer message = new StringBuffer();
    String response = "";

    ServerAddressPointer(int serverPort){
        this.serverPort = serverPort;
    }

    public void run() {
        try {
            // Start UDP Listener on port 65431
            DatagramSocket datagramSocket = new DatagramSocket(UDP_SERVER_PORT);
            datagramSocket.setBroadcast(true);

            byte[] receiveBytes = new byte[512];
            DatagramPacket datagramPacketReceive = new DatagramPacket(receiveBytes, receiveBytes.length);
            DatagramPacket datagramPacketSend = null;

            while (true) {
                // Receive Request
                datagramSocket.receive(datagramPacketReceive);
                for (int i = 0; i < datagramPacketReceive.getLength(); i++) {
                    if (receiveBytes[i] != (byte) 0)
                        message.append((char) receiveBytes[i]);
                    else break;
                }
                System.out.printf("............Server Address sent to> %s:%s / message: > %s\n",
                        datagramPacketReceive.getAddress(), datagramPacketReceive.getPort(), message);
                message.delete(0, message.length());


                // Send Response
                response =  String.valueOf(serverPort);
                datagramPacketSend = new DatagramPacket(response.getBytes(StandardCharsets.UTF_8), response.length(),
                        datagramPacketReceive.getAddress(), datagramPacketReceive.getPort());
                datagramSocket.send(datagramPacketSend);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
