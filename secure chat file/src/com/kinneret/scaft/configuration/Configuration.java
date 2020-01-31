package com.kinneret.scaft.configuration;

import java.util.List;

/**
 * A class represent data model of a configuration file
 */
public class Configuration {

    private String password;
    private String ip;
    private String port;
    private List<Neighbor> neighbors;

    /**
     * Constructor
     * @param password - public key
     * @param neighbors - list of neighbors
     */
    public Configuration(String password, List<Neighbor> neighbors) {
        this.password = password;
        this.neighbors = neighbors;
    }

    /*** Setters ***/
    public void setPassword(String password) {
        this.password = password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setNeighbors(List<Neighbor> neighbors) {
        this.neighbors = neighbors;
    }

    /*** Getters ***/
    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

    /**
     * An inner class, represent data model of neighbor
     */
    public static class Neighbor
    {
        private String name;
        private String ip;
        private String port;

        /**
         * Constructor
         * @param name - the name of the neighbor
         * @param ip - the ip of the neighbor
         * @param port - the port of the neighbor
         */
        public Neighbor(String name, String ip, String port) {
            this.name = name;
            this.ip = ip;
            this.port = port;
        }

        /*** Setters ***/
        public void setName(String name) {
            this.name = name;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setPort(String port) {
            this.port = port;
        }

        /*** Getters ***/
        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public String getPort() {
            return port;
        }
    }
}