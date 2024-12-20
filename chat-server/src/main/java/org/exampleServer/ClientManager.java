package org.exampleServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientManager implements Runnable {

    private final Socket socket;

//    private final static ArrayList<ClientManager> clients=new ArrayList<>();
    private final static HashMap<String, ClientManager> clients = new HashMap<>();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private String name;


    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name=bufferedReader.readLine();
//            clients.add(this);
            clients.put (name, this);
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
                } else if (massegeFromClient.contains("@")) {
                    privateMessage(massegeFromClient);

                } else {
                    broadcastMessage(massegeFromClient);
                }
            }catch (IOException e){
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private void privateMessage(String messageFromClient) {

        List<String> adressatNames=searchNames(messageFromClient);
        System.out.println("Spisok imen: ");
        adressatNames.stream().forEach(System.out::println);
        System.out.println("konec!");
        if (adressatNames.isEmpty()) {
            broadcastMessage(messageFromClient);
        } else {
            String messageWithoutNames=searchMessage(messageFromClient);

            for(String adressatName: adressatNames){
                sendPrivateMessage(adressatName,messageWithoutNames);
            }
        }

    }

    private void sendPrivateMessage(String adressatName, String messageWithoutNames) {
        if (clients.containsKey(adressatName)){
            ClientManager client= clients.get(adressatName);
            try {

                        client.bufferedWriter.write(messageWithoutNames);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();


                    } catch (IOException e) {
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }
        }else{
            sendServerPrivateMessage(name, "Takogo imeni net v spiske!");
        }


//        if(!clients.contains(adressatName)) {
//            System.out.println("imya ne naydeno " + adressatName + " peresylau "+ name);
//            clients.stream().forEach((x)->System.out.println(x.name));
//            sendServerPrivateMessage(name, "Takogo imeni net v spiske!");
//
//        }
//        else {
//
//
//            for (ClientManager client : clients) {
//
//                if (client.name.equals(adressatName)) {
//                    try {
//
//                        client.bufferedWriter.write(messageWithoutNames);
//                        client.bufferedWriter.newLine();
//                        client.bufferedWriter.flush();
//
//
//                    } catch (IOException e) {
//                        closeEverything(socket, bufferedWriter, bufferedReader);
//                    }
//                }
//
//            }
//        }

    }

    private void sendServerPrivateMessage(String name, String message) {
        ClientManager client= clients.get(name);
        try {
//
            client.bufferedWriter.write("Server: " +message);
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();


        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

//        for (ClientManager client : clients) {
//
//            if (client.name.equals(name)) {
//                try {
//
//                    client.bufferedWriter.write("Server: " +message);
//                    client.bufferedWriter.newLine();
//                    client.bufferedWriter.flush();
//
//
//                } catch (IOException e) {
//                    closeEverything(socket, bufferedWriter, bufferedReader);
//                }
//            }
//
//        }
    }

    private String searchMessage(String messageFromClient) {
        String [] words=messageFromClient.split(" ");
        String messageWithoutNames = "";
        for(int i=1; i< words.length; i++){
            if(notName(words[i]))
                messageWithoutNames += words[i] +" ";
        }

        messageWithoutNames += "from "+ name;

        return messageWithoutNames;
    }

    private boolean notName(String word) {
        if(word.startsWith("@") && word.endsWith("!") ){
            return false;
        }
        else {
            return true;
        }
    }


    private List<String> searchNames(String messageFromClient) {
        String [] words=messageFromClient.split(" ");
        List<String> adressats = new ArrayList<>();
        
        for(String word: words){
            if(word.startsWith("@") && word.endsWith("!"))
                adressats.add(word.substring( 1 ,word.length()-1));
        }
        return adressats;
    }

    private void whoIsChat() {

        ClientManager client= clients.get(name);
        try {
            client.bufferedWriter.write("Na danniy moment v chate:");
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();

            for(String name: clients.keySet()){
                client.bufferedWriter.write(name);
                client.bufferedWriter.newLine();
                client.bufferedWriter.flush();
            }

            client.bufferedWriter.write("Konec spiska");
            client.bufferedWriter.newLine();
            client.bufferedWriter.flush();
        }catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
//        for(ClientManager client: clients){
//
//                if (client.name.equals(name)) {
//                    try {
//                        client.bufferedWriter.write("Na danniy moment v chate:");
//                        client.bufferedWriter.newLine();
//                        client.bufferedWriter.flush();
//                        for (ClientManager x : clients) {
//
//                                client.bufferedWriter.write(x.name);
//                                client.bufferedWriter.newLine();
//                                client.bufferedWriter.flush();
//                        }
//
//                    } catch (IOException e) {
//                            closeEverything(socket, bufferedWriter, bufferedReader);
//                    }
//                }
//
//        }

    }

    private void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader)  {
        removeClient(name);
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

    private void removeClient(String name) {

        clients.remove(name);
        System.out.println(name + " pokinul chat");
        broadcastMessage("Server: " + name + " pokinul chat");
    }


    private void broadcastMessage(String message){
        try {
        for(String name: clients.keySet()){
            if (clients.get(name)!=this){
                clients.get(name).bufferedWriter.write(message);
                clients.get(name).bufferedWriter.newLine();
                clients.get(name).bufferedWriter.flush();
             }
        }
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

//        for(ClientManager client: clients){
//            try {
//                if (!client.name.equals(name)) {
//                    client.bufferedWriter.write(message);
//                    client.bufferedWriter.newLine();
//                    client.bufferedWriter.flush();
//                }
//            }catch (IOException e){
//                closeEverything(socket, bufferedWriter, bufferedReader);
//            }
//        }
    }

}
