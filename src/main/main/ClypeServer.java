package main;

import data.ClypeData;
import java.io.*;
import  java.net.*;

public class ClypeServer {
    private int port;
    private boolean closeConnection = false;
    private ClypeData dataToSendToClient;
    private ClypeData dataToReceiveFromClient;
    public static final int defaultPort = 7000;
    private ObjectInputStream inFromClient = null;
    private ObjectOutputStream outToClient = null;

    /**
     * constructor for ClypeServer
     *
     * @param port
     */
    public ClypeServer(int port) {
        try {
            this.port = port;
            dataToSendToClient = null;
            dataToReceiveFromClient = null;
        } catch (IllegalArgumentException aie) {
            System.err.println("Illegal port");
        }
    }

    /**
     * default Constructor for ClypeServer
     */
    public ClypeServer() {
        new ClypeServer(this.defaultPort);
    }

    /**
     * starts server <br>
     * opens server socket and accepts client socket <br>
     * it handles recieving and sending data <br>
     * closes streams and sockets when completed <br>
     */
    public void start() {
        try {
            ServerSocket sskt = new ServerSocket(port);
            System.out.println("socket for server opened");
            Socket cskt = sskt.accept();
            System.out.println("client accepted");
            inFromClient = new ObjectInputStream(cskt.getInputStream());
            outToClient = new ObjectOutputStream(cskt.getOutputStream());
            dataToSendToClient = dataToReceiveFromClient;
            receiveData();
            sendData();

            inFromClient.close();
            outToClient.close();
            cskt.close();
            sskt.close();
        } catch (IOException ioe) {
            System.err.println("IO error: " + ioe.getMessage());
        }
    }

    /**
     * Opens a server and accepts a client socket <br>
     * it recieves and echoes data from client then closes <br>
     */
    public void receiveData() {
        try {
            ServerSocket sskt = new ServerSocket(port);
            Socket cskt = sskt.accept();
            inFromClient = new ObjectInputStream(cskt.getInputStream());
            dataToReceiveFromClient = (ClypeData)inFromClient.readObject();
            //for debugging
            System.out.println(dataToReceiveFromClient);

            inFromClient.close();
            cskt.close();
            sskt.close();
        } catch (IOException ioe) {
            System.err.println("IO error: " + ioe.getMessage());
        } catch (IllegalArgumentException | ClassNotFoundException iae) {
            System.err.println("Illegal Arguement: " + iae.getMessage());
        }
    }

    /**
     * Sends data <br>
     * Opens server socket and accepts client socket <br>
     * sends data to client and closes all sockets and steams when completed <br>
     */
    public void sendData() {
        try {
            ServerSocket sskt = new ServerSocket(port);
            Socket cskt = sskt.accept();
            outToClient.writeObject(dataToSendToClient);

            outToClient.close();
            cskt.close();
            sskt.close();
        } catch (IOException ioe) {
            System.err.println("IO error: " + ioe.getMessage());
        } catch (IllegalArgumentException iae) {
            System.err.println("Illegal Arguement: " + iae.getMessage());
        }
    }

    /**
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + getPort();
        result = 37 * result + (this.closeConnection ? 0 : 1);
        return result;
    }

    /**
     * checks to see if two objects are equal
     *
     * @param o
     * @return bool
     */
    @Override
    public boolean equals(Object o) {
        ClypeServer s = (ClypeServer) o;
        return this.getPort() == s.getPort() && this.closeConnection == s.closeConnection;
    }

    /**
     * @return port number and closedConnection status
     */
    @Override
    public String toString() {
        return ("Port: " + this.getPort() + "\nClosed Connection: " + this.closeConnection);
    }

    public static void main(String args[]) {
        if(args.length==0){
            ClypeServer CS = new ClypeServer();
            CS.start();
        }
        else{
            int port = Integer.parseInt(args[0]);
            ClypeServer CS = new ClypeServer(port);
            CS.start();
        }
    }
}