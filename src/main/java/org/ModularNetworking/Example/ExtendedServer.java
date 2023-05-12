package org.ModularNetworking.Example;

import org.ModularNetworking.Client;
import org.ModularNetworking.Server;
import org.ModularNetworking.ServerConfig;

public class ExtendedServer extends Server {
    public ExtendedServer(ServerConfig config) {
        super(config);
    }

    @Override
    protected void onServerInput(String consoleInput) {
        serverSendToAll(consoleInput);
    }

    @Override
    public void readFromClient(String input, Client client) {
        System.out.println("FROM CLIENT: " + client.ID + " : " + input);
    }
}
