package org.ModularNetworking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Socket connectedSocket;
    public Server connectedServer; // Will be null if client
    public DataInputStream in;
    public DataOutputStream out;
    public int ID = -1;
    public boolean connected;

    public boolean isServerProp = false;


    public Client(Socket socket){
        try {
            this.connectedSocket = socket;
            connected = true;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread readFromServerThread = new Thread(this::read);
            readFromServerThread.start();

            Thread writeThread = new Thread(this::consoleInput);
            writeThread.start();
        }
        catch (Exception e){
            e.printStackTrace();
            // Disconnected
        }
    }

    private void read(){
        while (true) {
            try {
                String input = in.readUTF();
                messageProcessing(input);

            } catch (Exception e) {
                if(connected){
                    System.out.println("Read Error: " + e);
                }
                break;
            }
        }
    }

    private void consoleInput() {
        while (true){
            if(isServerProp)
                break;
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            try {
                // Send message to server

                if(message.equals("disconnect")) {
                    try {
                        connectedSocket.close();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                send(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String message){
        try {
            byte[]data = message.getBytes();
            if(data.length > 65535){
                System.out.println("Cannot send data in one packet. Send as packets instead.");
                return;
            }
            out.writeUTF(message);
        } catch (Exception e) {
            if(connected){
                System.out.println("Send Error: " + e.getMessage());
            }

        }
    }

    private void messageProcessing(String input){
        // Read as if server
        if(isServerProp) {
            connectedServer.readFromClient(input, this);
            return;
        }
        readFromServer(input);

        // else read as if client
    }

    protected void readFromServer(String input){}
}
