package com.kinneret.scaft.server.files;

import com.kinneret.scaft.client.Client;
import com.kinneret.scaft.configuration.Configuration;
import com.kinneret.scaft.ui.Controller;
import com.kinneret.scaft.ui.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.kinneret.scaft.security.Security.decryptFile;
import static com.kinneret.scaft.server.HandleClientThread.getNeighborByIpPort;
import static com.kinneret.scaft.server.Server.MessageType.OK;
import static com.kinneret.scaft.utils.CommonChars.COLON;
import static com.kinneret.scaft.utils.CommonChars.SLASH;

/**
 * A class represented a thread that listen to the client files
 */
public class HandleFileTransferThread extends Thread {

    private Socket clientSocket;
    private boolean stop = false;
    private Configuration.Neighbor neighbor;
   private AtomicBoolean succeed = new AtomicBoolean(false);

    HandleFileTransferThread(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * A method that the thread will run when starts
     */
    @Override
    public void run() {
        System.out.println("Received connection from: " + clientSocket);
        try (InputStream in = clientSocket.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String input;

            while (!stop) {
                input = br.readLine();
                if ((input != null) && (!input.trim().isEmpty()) && (!stop)) {
                    processFile(input);
                }
            }
            if (succeed.get()) {
                Client.sendToNeighbor(OK, Controller.userName + " Received the file successfully", neighbor);
            } else {
                Client.sendToNeighbor(OK, Controller.userName + " Canceled file saving after receiving it", neighbor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to process an incoming encrypted file
     * @param encryptedFile - the encrypted file to process
     */
    private void processFile(String encryptedFile) {
        Pair<String, byte[]> decryptedFile = decryptFile(encryptedFile);
        if (decryptedFile == null) {
            stop = true;
            return;
        }
        String[] tokens = decryptedFile.getKey().trim().split(SLASH);
        String[] ipPort = tokens[0].trim().split(COLON);
        String fileName = tokens[1].trim();
        neighbor= getNeighborByIpPort(ipPort[0], ipPort[1]);

        Platform.runLater(() -> {
            File receivedFile = Main.controller.chooseDirectory(fileName);
            if (receivedFile != null) {
                try (FileOutputStream fos = new FileOutputStream(receivedFile)) {
                    fos.write(decryptedFile.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Main.controller.showMessageBox("File Transfer", "File transfer completed successfully", Alert.AlertType.INFORMATION);
                succeed.set(true);
            }
            stop = true;
        });
    }
}
