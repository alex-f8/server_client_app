package af.lab.localservice.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientThread extends Thread {
    private SocketChannel socketChannel;
    ByteBuffer byteBuffer;
    StringBuffer stringBuffer;
    int i;

    ClientThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.byteBuffer = ByteBuffer.allocate(1024);
        stringBuffer = new StringBuffer();
    }

    public void run() {
        try {
            System.out.print(socketChannel.getRemoteAddress() + "-> ");
            while ((i = socketChannel.read(byteBuffer)) > -1) {
                byte[] byteArray = byteBuffer.array();
                for (int c = 0; c < i; c++)
                    stringBuffer.append((char) byteArray[c]);
                System.out.println(stringBuffer);
                stringBuffer.delete(0, stringBuffer.length());
                byteBuffer.clear();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (this.socketChannel != null)
                    this.socketChannel.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
