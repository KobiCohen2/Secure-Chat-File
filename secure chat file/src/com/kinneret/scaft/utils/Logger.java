package com.kinneret.scaft.utils;

import com.kinneret.scaft.configuration.Configuration;
import com.kinneret.scaft.server.Server;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static com.kinneret.scaft.ui.Controller.conf;
import static com.kinneret.scaft.utils.CommonChars.space;
import static com.kinneret.scaft.utils.DateAndTime.getCurrentDateTimeStamp;

public class Logger {

    private static File logger;

    static {
        logger = new File("SCAFT-Logger.log");
    }

    /**
     * An enum that contains log level types
     */
    public enum LOG_LEVEL {
        INFO,
        DEBUG,
        ERROR
    }

    public static void writeToLog(LOG_LEVEL TAG, Configuration.Neighbor neighbor, String userName, Server.MessageType type, String message, String iv) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logger, true))) {
            if(type == null)//error in decryption
            {
                bw.write(getCurrentDateTimeStamp() + space + TAG + " - " + "Received Message - " +
                        "Error decryption, probably due to incorrect key. " + "content: " + message + ", iv: " + iv + "\n");
                bw.newLine();
            }
            else {
                if (neighbor == null) {
                    bw.write(getCurrentDateTimeStamp() + space + TAG + " - " + "Sent Message - " + "ip: " + conf.getIp() +
                            ", port: " + conf.getPort() + ", name: " + userName + " (me) " + ", type: " + type +
                            ", message: " + message + ", iv: " + iv + "\n");
                    bw.newLine();
                } else {
                    bw.write(getCurrentDateTimeStamp() + space + TAG + " - " + "Received Message - " + "ip: " + neighbor.getIp() +
                            ", port: " + neighbor.getPort() + ", name: " + userName + ", type: " + type +
                            ", message: " + message + ", iv: " + iv + "\n");
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while writing to log");
        }
    }
}
