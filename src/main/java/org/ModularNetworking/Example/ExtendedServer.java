package org.ModularNetworking.Example;

import org.ModularNetworking.Client;
import org.ModularNetworking.Server;
import org.ModularNetworking.ServerConfig;

public class ExtendedServer extends Server {
    public ExtendedServer(ServerConfig config) {
        super(config);
    }

    @Override
    protected void onConnectSendServerInfo(Client connectedClient) {
        serverSendTo(connectedClient, "KEY:" + connectedClient.serverKey);
        serverSendTo(connectedClient, "ID:" + connectedClient.ID);
    }

    @Override
    protected void onServerInput(String consoleInput) {
        serverSendToAll(consoleInput);
    }

    @Override
    public void readFromClient(String input, Client client) {
        System.out.println("FROM CLIENT: " + client.ID + " : " + input);
    }

    @Override
    protected String generateClientKey()
    {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(symbols.length());
            sb.append(symbols.charAt(index));
        }
        return sb.toString();
    }
}
