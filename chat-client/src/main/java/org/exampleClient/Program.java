package org.exampleClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args)  {




        try (Socket socket = new Socket("localhost", 1400)) {
            System.out.println("Hello Client!");

            Scanner sc=new Scanner(System.in);
            System.out.println("vvedite imya");

            String name= sc.nextLine();

            Client client=new Client(socket,name);

            InetAddress inetAddress=socket.getInetAddress();
            System.out.println("InetAdress: " + inetAddress);
            String remoteIp=inetAddress.getHostAddress();
            System.out.println("Remote IP: "+ remoteIp);
            System.out.println("localPort: "+ socket.getLocalPort());

            client.listenForMessage();
            client.sendMessage();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}