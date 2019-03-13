package com.mily;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main implements Runnable{

    private Scanner scanner = new Scanner(System.in);
    private String ip;
    private int port;
    private boolean accepted = false;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ServerSocket serverSocket;
    private int errors = 0;

    public static void main(String[] args) {
        Main main = new Main();
        main.game();
    }

    private void game(){
        System.out.println("Wprowadz IP: ");
        ip = scanner.nextLine();
        System.out.println("Wporwadz PORT: ");
        port = scanner.nextInt();
        /*ip="127.0.0.1";
        port=22222;*/

        if (!connect()) initializeServer();
        Thread thread = new Thread(this);
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
            Socket socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
        } catch (IOException e) {
            System.out.println("Nie mozna polaczyc sie z adresem: " + ip + ":" + port + " | Uruchamianie serwera");
            return false;
        }
        System.out.println("Pomyslnie polaczono z serwerem");
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
        //noinspection InfiniteLoopStatement
        while (true) {
            tick();
            if (!accepted) {
                listenForServerRequest();
            }
        }
    }

    private void tick() {
        if(errors > 10){
            System.out.println("Utracono placzenie");
            System.exit(0);
        }
        if (accepted) {
            try {
                String space = dis.readUTF();
                if (!space.isEmpty()) System.out.println(space);
            } catch (IOException e) {
                errors++;
                e.printStackTrace();
            }
        }
    }

    private void listenForServerRequest() {
        Socket socket;
        try {
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("Klient polaczyl się");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
