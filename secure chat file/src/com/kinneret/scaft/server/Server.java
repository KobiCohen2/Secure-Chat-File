package com.kinneret.scaft.server;


import com.kinneret.scaft.ui.Controller;
import com.kinneret.scaft.ui.Main;
import com.kinneret.scaft.server.files.FileServerListener;
import javafx.application.Platform;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import static com.kinneret.scaft.utils.CommonChars.COLON;

/**
 * A class represented Server
 */
public class Server {

    private static ServerSocket serverSocket;
    private static ServerSocket filesServerSocket;
    private static ServerListener listener;
    private static FileServerListener fileServerListener;
    static Set<HandleClientThread> connectedNeighbors = new HashSet<>();
    public static final int FILE_SERVER_PORT_SHIFTING = 555;

    /**
     * An enum that contains message types
     */
    public enum MessageType {
        HELLO,
        MESSAGE,
        SENDFILE,
        OK,
        NO,
        BYE
    }

    /**
     * A method to start the server
     */
    public static void connect() {
        try {
            String ip = Controller.conf.getIp();
            int port = Integer.parseInt(Controller.conf.getPort());
            serverSocket = new ServerSocket(port, 10, InetAddress.getByName(ip));
            filesServerSocket = new ServerSocket(port + FILE_SERVER_PORT_SHIFTING, 10, InetAddress.getByName(ip));
            Platform.runLater(() -> {
                Main.controller.lblListening.setText("listening on " + ip + COLON + port);
                Main.controller.lblListening.setVisible(true);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        listener = new ServerListener(serverSocket);
        listener.start();
        fileServerListener = new FileServerListener(filesServerSocket);
        fileServerListener.start();
    }

    /**
     * A method to stop the server
     */
    public static void disconnect() {
        connectedNeighbors.forEach(handleClientThread -> handleClientThread.stop = true);
        connectedNeighbors.clear();
        listener.stop = true;
        fileServerListener.stop = true;
        try {
            serverSocket.close();
            filesServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
