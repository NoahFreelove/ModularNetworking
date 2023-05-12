package org.ModularNetworking.Example;

import org.ModularNetworking.Client;
import org.ModularNetworking.DisconnectType;

import java.net.Socket;

public class ExtendedClient extends Client {
    public ExtendedClient(String ip, int port) {
        super(ip, port);
    }

    @Override
    protected void readFromServer(String input) {    
        System.out.println("From server: " + input);
    }
    @Override
    protected void onDisconnect(DisconnectType dt){
        System.out.println("Disconnected: " + dt.name());
    }
}
