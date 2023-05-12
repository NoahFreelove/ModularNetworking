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
        if(input.startsWith("ID:")){
            ID = Integer.parseInt(input.replace("ID:",""));
            System.out.println("My ID is now " + ID);
        }
        else{
            System.out.println("From server: " + input);
        }
    }
    @Override
    protected void onDisconnect(DisconnectType dt){
        System.out.println("Disconnected: " + dt.name());
    }
}
