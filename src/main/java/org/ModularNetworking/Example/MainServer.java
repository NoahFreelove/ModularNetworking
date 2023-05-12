package org.ModularNetworking.Example;

import org.ModularNetworking.Server;
import org.ModularNetworking.ServerConfig;

public class MainServer {
    public static void main(String[] args) {
        ServerConfig sc = new ServerConfig();
        ExtendedServer server = new ExtendedServer(sc);
        server.open();

    }
}
