package org.ModularNetworking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Server {
    protected transient ServerConfig config;

    public Server(ServerConfig config){
        this.config = config;
    }

    ServerSocket serverSocket;
    volatile public Client[] clientSlots = new Client[4];

    public void open() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(config.ip), config.port));

            Thread inputThread = new Thread(this::serverInput);
            inputThread.start();

            Thread queue = new Thread(() -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        if(config.bannedIPs.contains(s.getInetAddress().getHostAddress()) || (config.whitelist && !config.whitelistIPs.contains(s.getInetAddress().getHostAddress()))) {
                            try {
                                s.close();
                            }catch (Exception ignore){}
                            return;
                        }

                        Client newClient = new Client(s);

                        newClient.connectedServer = this;
                        newClient.connectedSocket = s;
                        int i = 0;
                        for (Client c : clientSlots) {
                            if(c == null)
                            {
                                clientSlots[i] = newClient;
                                break;
                            }
                            if(i == clientSlots.length-1){
                                newClient.connectedSocket.close();
                                System.out.println("rejected new connection as server is full.");
                            }
                            i++;
                        }
                        newClient.ID = i;
                        newClient.serverKey = generateClientKey();
                        newClient.isServerProp = true;
                        System.out.println("New Client: " + newClient.connectedSocket.getInetAddress().getHostAddress());
                        onConnectSendServerInfo(newClient);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            System.out.println("Now accepting incoming connections on port: " + config.port + "; " + serverSocket.getInetAddress().getHostAddress());

            queue.start();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    protected abstract void onConnectSendServerInfo(Client connectedClient);

    private void serverInput() {
        while (true){

            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            try {
                onServerInput(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected abstract void onServerInput(String consoleInput);

    public abstract void readFromClient(String input, Client client);

    public void serverSendTo(Client c, String message){
        c.send(c.serverKey + message);
    }

    public void serverSendToAll(String message){
        for (Client c : clientSlots) {
            if(c != null)
                serverSendTo(c, message);
        }
    }

    protected abstract String generateClientKey();

}
