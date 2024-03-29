package main;

public class ClientSideServerListener implements Runnable {
    private ClypeClient client;
    public ClientSideServerListener(ClypeClient client) {
        this.client = client;
    }
    @Override
    public void run() {
        while(client.isClosed() == false) {
            client.receiveData();
            client.printData();
        }
    }
}