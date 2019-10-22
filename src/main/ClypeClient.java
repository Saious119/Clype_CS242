package main;

import data.ClypeData;
import data.FileClypeData;
import data.MessageClypeData;

import java.io.*;
import java.util.Scanner;
import java.net.*;

public class ClypeClient {
    final static int defaultport = 7000;
    private String userName;
    private String hostName;
    private int port;
    private boolean closeConnection=false;
    private ClypeData dataToSendToServer = null;
    private ClypeData dataToReceiveFromServer =null;
    private Scanner inFromStd;
    private ObjectInputStream inFromServer = null;
    private ObjectOutputStream outToServer = null;

    /**
     * constructor for ClypeClient
     * @param userName
     * @param hostName
     * @param port
     */
    public ClypeClient(String userName, String hostName, int port){
        dataToSendToServer = null;
        dataToReceiveFromServer = null;
        ObjectInputStream inFromServer = null;
        ObjectOutputStream outToServer = null;
        this.userName=userName;
        this.hostName=hostName;
        this.port=port;
        if(userName == null || hostName == null || port < 1024)
            throw new IllegalArgumentException("\nInvalid Input");
    }

    /**
     * alternate constructor for ClypeClient
     * @param userName
     * @param hostName
     * @throws IllegalArgumentException
     */
    public ClypeClient(String userName, String hostName) throws IllegalArgumentException{
        this(userName, hostName, defaultport);
        if(userName == null || hostName == null)
            throw new IllegalArgumentException("\nInvalid Input");
    }

    /**
     * alternate constructor for ClypeClient
     * @param userName
     * @throws IllegalArgumentException
     */
    public ClypeClient(String userName) throws  IllegalArgumentException{
        this(userName, "localhost", defaultport);
        if(userName == null)
            throw new IllegalArgumentException("\nInvalid Input");
    }

    /**
     * default constructor for ClypeClient
     */
    public  ClypeClient(){
        this("anonymous");
    }

    /**
     * starts the client <br>
     * Opens a socket and connects to server<br>
     * then read and prints data <br>
     * also instantiates a scanner called inFromStd <br>
     * Closes all streams and sockets it opened <br>
     */
    public void start(){
        try {
            Socket skt = new Socket(hostName, port);
            inFromServer = new ObjectInputStream(skt.getInputStream());
            outToServer = new ObjectOutputStream(skt.getOutputStream());
            this.inFromStd = new Scanner(System.in);
            dataToSendToServer = readClientData();
            dataToReceiveFromServer = receiveData();
            readClientData();
            receiveData();
            sendData();
            printData();

            outToServer.close();
            inFromServer.close();
            skt.close();
            inFromStd.close();
        }
        catch (IOException ioe){
            System.err.println("IO error: "+ioe.getMessage());
        }
        catch (SocketException se){
            System.err.println("Socket exception: "+se.getMessage());
        }
    }

    /**
     * reads data and changes action based on what is read from the inFromStd Scanner
     */
    public void readClientData(){
        if(inFromStd.toString() == "DONE"){
            closeConnection=true;
        }
        else if(inFromStd.toString() == "SENDFILE"){
            String filename = inFromStd.toString();
            dataToSendToServer = new FileClypeData(userName,filename,2);
        }
        else if(inFromStd.toString() == "LISTUSERS"){
            /*this does nothing for now but
            will eventually return a list of users
            or an error if that's not possible
             */
        }
        else{
            String message = inFromStd.toString();
            dataToSendToServer = new MessageClypeData(userName, message,3);
        }

    }
    /**
     * sends data to server <br>
     *     opens a socket to send data then closes all streams in sockets it opened <br>
    */
    public void sendData(){
        try{
            Socket skt = new Socket(hostName,port);
            outToServer = new ObjectOutputStream(skt.getOutputStream());
            outToServer.writeObject(dataToSendToServer);

            outToServer.close();
            skt.close();
        }
        catch (IOException ioe){
            System.err.println("IO error: "+ioe.getMessage());
        }
        catch (SocketException se){
            System.err.println("Socket exception: "+se.getMessage());
        }
    }
    /**
     * recieves data from server <br>
     *     opens a socket to receive data then closes all streams in sockets it opened <br>
     */
    public void receiveData(){
        try{
            Socket skt = new Socket(hostName,port);
            inFromServer = new ObjectInputStream(skt.getInputStream());
            inFromServer.readObject(dataToReceiveFromServer);
        }
        catch (IOException ioe){
            System.err.println("IO error: "+ioe.getMessage());
        }
        catch (SocketException se){
            System.err.println("Socket exception: "+se.getMessage());
        }
        catch (IllegalArgumentException iae){
            System.err.println("Illegal Arguement: "+iae.getMessage());
        }
    }

    /**
     * prints data that is being sent or received
     */
    public void printData(){
        this.dataToReceiveFromServer = this.dataToSendToServer;
        System.out.println(this.dataToReceiveFromServer.toString());
    }

    /**
     *
     * @return userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     *
     * @return hostName
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result =17;
        result = 37 * result + this.getUserName().hashCode();
        result = 37 * result + this.getHostName().hashCode();
        result = 37 * result + this.getPort();
        result = 37 *result + (this.closeConnection ? 0:1);
        return result;

    }

    /**
     * checks to see if two objects are equal
     * @param o
     * @return bool
     */
    @Override
    public boolean equals(Object o) {
        ClypeClient c = (ClypeClient) o;
        return( this.userName.equals(c.getUserName())&&
                this.hostName.equals(c.getHostName())&&
                this.port == getPort() &&
                this.closeConnection == c.closeConnection);
    }

    /**
     *
     * @return every piece of data the object has which can be userName,hostName, port, closed connection <br>
     *  and may include dataToSendToServer and dataToReceiveFromServer
     */
    @Override
    public String toString() {
        if(this.dataToReceiveFromServer != null && this.dataToSendToServer != null){
            return ("Description: this is client"+
                    "\nUser name: "+this.getUserName() +
                    "\nHost Name: "+this.getHostName() +
                    "\nPort: "+this.getPort()+
                    "\nClose Connection: "+this.closeConnection+
                    "\nData to Send to Server: "+this.dataToSendToServer+
                    "\nData to Recieve From Server: "+this.dataToReceiveFromServer);
        }
        else{
            return ("Description: this is client"+
                    "\nUser name: "+this.getUserName() +
                    "\nHost Name: "+this.getHostName() +
                    "\nPort: "+this.getPort()+
                    "\nClose Connection: "+this.closeConnection);
        }
    }
    public static void main(String args[]) {
        if(args[0] == null){
            ClypeClient CC = new ClypeClient();
            CC.start();
        }
        else if(args[0] != null){
            String input = args[0];
            if(input.contains("@") && input.contains(":")){
                String userName="";
                String hostName="";
                String strPort="";
                int port;
                int startAfterAT = 1;
                for(int i=0; input.charAt(i) != '@'; i++){
                    userName = userName+input.charAt(i);
                    startAfterAT++;
                }
                int startAfterColon = startAfterAT+1;
                for(int i=startAfterAT;input.charAt(i) != ':'; i++){
                    hostName = hostName+input.charAt(i);
                    startAfterColon++;
                }
                for(int i = startAfterColon; i <= input.length();i++){
                    strPort = strPort+input.charAt(i);
                }
                port = Integer.parseInt(strPort);
                ClypeClient CC = new ClypeClient(userName,hostName,port);
                CC.start();

            }
            else if(input.contains("@")){
                String userName="";
                String hostName="";
                int startAfterAT = 1;
                for(int i=0; input.charAt(i) != '@'; i++){
                    userName = userName+input.charAt(i);
                    startAfterAT++;
                }
                int startAfterColon = startAfterAT+1;
                for(int i=startAfterAT;input.charAt(i) != ':'; i++){
                    hostName = hostName+input.charAt(i);
                    startAfterColon++;
                }
                ClypeClient CC = new ClypeClient(userName, hostName);
                CC.start();
            }
            else {
                ClypeClient CC = new ClypeClient(input);
                CC.start();
            }
        }
        else{
            System.err.println("invalid Command Line Arguments");
        }
    }
}
