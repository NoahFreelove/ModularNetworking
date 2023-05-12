package org.ModularNetworking.Example;

import org.ModularNetworking.Client;

import java.net.Socket;

public class ExtendedClient extends Client {
    public ExtendedClient(Socket socket) {
        super(socket);
    }

    @Override
    protected void readFromServer(String input) {
        System.out.println("From server: " + input);
    }
}
