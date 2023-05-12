package org.ModularNetworking.Example;

import org.ModularNetworking.Client;

import java.io.IOException;
import java.net.Socket;

public class MainClient {
    public static void main(String[] args) throws IOException {
        ExtendedClient client = new ExtendedClient(new Socket("localhost", 400));
    }
}