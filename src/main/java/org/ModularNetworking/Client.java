package org.ModularNetworking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    Socket connectedSocket;
    public Server connectedServer; // Will be null if client
    public DataInputStream in;
    public DataOutputStream out;
    public int ID = -1;
    public String serverKey = "";
    public boolean connected;
    public boolean printStackTrace = false;

    public boolean isServerProp = false;


    public Client(Socket socket){
        setup(socket);
    }
    public Client(String ip, int port){
        try{
            setup(new Socket(ip,port));
        }catch (ConnectException | UnknownHostException e){
            onDisconnect(DisconnectType.SERVER_DOESNT_EXIST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setup(Socket socket){
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
                    if(isServerProp){
                        System.out.println("Client ID " + ID + " Disconnected.");
                        try {
                            connectedSocket.close();
                        }catch (Exception ignore){}
                        connectedServer.clientSlots[ID] = null;
                    }
                    else
                        onDisconnect(DisconnectType.LOST_CONNECTION_WITH_SERVER);
                }
                else{
                    System.out.println("Read error: " + e.getMessage());
                }
                if(printStackTrace)
                    e.printStackTrace();
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
        if(serverKey.equals("")){
            if(input.startsWith("KEY:"))
            {
                System.out.println("server synced key with client");
                serverKey = input.replace("KEY:","");
            }
            return;
        }
        if(input.startsWith(serverKey)){
            input = input.replace(serverKey,"");

            if(input.startsWith("ID:")){
                ID = Integer.parseInt(input.replace("ID:",""));
                System.out.println("My ID is " + ID);
                return;
            }
            readFromServer(input);
        }

        // else read as if client
    }

    protected void readFromServer(String input){}
    protected void onDisconnect(DisconnectType dt){}
}
