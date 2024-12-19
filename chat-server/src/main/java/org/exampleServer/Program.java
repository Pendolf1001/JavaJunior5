package org.exampleServer;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args)  {

        System.out.println("Hello server!");
        try
            (ServerSocket serverSocket=new ServerSocket(1400)){
            Server server=new Server(serverSocket);
            server.runServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }



    }
}