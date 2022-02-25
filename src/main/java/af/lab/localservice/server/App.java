package af.lab.localservice.server;

import java.lang.ref.Cleaner;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class App {
    public static final int SERVER_PORT = 65432;
    public static void main(String[] args) throws Exception {

        /* Start Server/Service.  Service listen port No 65432 */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress(SERVER_PORT);
        serverSocketChannel.socket().bind(socketAddress);

        /* Start Broadcast listener Service aka Service Ip Adderess Pointer */
        new ServerAddressPointer(SERVER_PORT).start();

        while(true){
            SocketChannel socketChannel = serverSocketChannel.accept();
            new ClientThread(socketChannel).start();
        }
    }
}
