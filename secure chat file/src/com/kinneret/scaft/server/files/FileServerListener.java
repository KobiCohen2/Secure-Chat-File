package com.kinneret.scaft.server.files;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A class represent a listener of incoming connections for file transfer
 */
public class FileServerListener extends Thread {

    private ServerSocket serverSocket;
    public volatile boolean stop;

    public FileServerListener(ServerSocket serverSocket) {
        this.stop = false;
        this.serverSocket = serverSocket;
    }

    /**
     * A method that the thread will run when starts
     */
    @Override
    public void run() {
        try {
            while (!stop) {
                if (!stop && serverSocket != null && !serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    HandleFileTransferThread fileTransferThread = new HandleFileTransferThread(clientSocket);
                    fileTransferThread.start();
                }
            }
        }
        catch(Exception e) { e.printStackTrace(); }
        finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed())
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
