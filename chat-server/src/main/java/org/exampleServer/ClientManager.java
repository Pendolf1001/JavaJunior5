package org.exampleServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {

    private final Socket socket;

    private final static ArrayList<ClientManager> clients=new ArrayList<>();

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private String name;


    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name=bufferedReader.readLine();
            clients.add(this);
            System.out.println(name+" : podkluchilsya k chatu! ");
            broadcastMessage("Server: " +name + " podkluchilsya k chatu!");
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String massegeFromClient;
        while(socket.isConnected()){
            try {
                massegeFromClient = bufferedReader.readLine();
                System.out.println(massegeFromClient);
                if (massegeFromClient.equals(name + ": "+"CHAT!")){
                    whoIsChat();
                }
                else {
                    broadcastMessage(massegeFromClient);
                }
            }catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private void whoIsChat() {
        for(ClientManager client: clients){

                if (client.name.equals(name)) {
                    try {
                        client.bufferedWriter.write("Na danniy moment v chate:");
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                        for (ClientManager x : clients) {

                                client.bufferedWriter.write(x.name);
                                client.bufferedWriter.newLine();
                                client.bufferedWriter.flush();
                        }

                    } catch (IOException e) {
                            closeEverything(socket, bufferedWriter, bufferedReader);
                    }
                }

        }

    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader)  {
        removeClient();
        try {
            if(bufferedWriter!=null)
                bufferedWriter.close();
            if(bufferedReader!=null)
                bufferedReader.close();

            if(socket!=null)
                socket.close();


        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void removeClient() {

        clients.remove(this);
        System.out.println(name + " pokinul chat");
        broadcastMessage("Server: " +name + " pokinul chat");
    }


    private void broadcastMessage(String message){
        for(ClientManager client: clients){
            try {
                if (!client.name.equals(name)) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }

}
