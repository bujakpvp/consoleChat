package com.mily;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main implements Runnable{

    Scanner scanner = new Scanner(System.in);
    String ip;
    int port;
    private boolean accepted = false;
    private Socket socket;
    private Thread thread;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ServerSocket serverSocket;
    private int errors = 0;

    public static void main(String[] args) {
        Main main = new Main();
        main.game();
    }

    private void game(){
        System.out.println("Please input the IP: ");
        ip = scanner.nextLine();
        System.out.println("Please input the port: ");
        port = scanner.nextInt();
        /*ip="127.0.0.1";
        port=22222;*/

        if (!connect()) initializeServer();
        thread = new Thread(this, "TicTacToe");
        thread.start();

        Thread thread1 = new Thread(() ->{
            while (true) {
                String s=scanner.nextLine();
                if(accepted){
                    try {
                        dos.writeUTF(s);
                        dos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread1.start();

    }

    private boolean connect() {
        try {
            socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
        } catch (IOException e) {
            System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server.");
        return true;
    }

    private void initializeServer() {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            tick();
            if (!accepted) {
                listenForServerRequest();
            }
        }
    }

    private void tick() {
        if(errors > 10){
            System.out.println("Connection timed out");
            System.exit(0);
        }
        if (accepted) {
            try {
                String space = dis.readUTF();
                System.out.println(space);
            } catch (IOException e) {
                errors++;
                e.printStackTrace();
            }
        }
    }

    private void listenForServerRequest() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
