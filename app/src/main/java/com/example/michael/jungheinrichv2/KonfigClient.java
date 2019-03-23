package com.example.michael.jungheinrichv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class KonfigClient extends Thread {
    private String ip_address = "192.168.43.66";
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String HOSTNAME = "localhost";
    private final int PORTNR = 8085;

    public KonfigClient() {

    }

    @Override
    public synchronized void run() {
        Socket socket;
        try {
            //socket = new Socket("0.0.0.0",PORTNR);
            //System.out.println(socket.getInetAddress());
            //10.0.2.2
            socket = new Socket(ip_address, PORTNR);
            System.out.println(socket.getInetAddress());

            if(MainActivity.dbdata.size() != 0)
            {
                String benutzer = MainActivity.dbdata.get(0);
                String passwort = MainActivity.dbdata.get(1);
                String hostname = MainActivity.dbdata.get(2);
                String port = MainActivity.dbdata.get(3);
                String sid = MainActivity.dbdata.get(4);
                OutputStream os = socket.getOutputStream();
                out = new ObjectOutputStream(os);
                out.writeObject("dbdata;"+benutzer+";"+passwort+";"+hostname+";"+port+";"+sid);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        this.interrupt();
    }
}
