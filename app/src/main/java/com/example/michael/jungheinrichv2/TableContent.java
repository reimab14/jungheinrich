package com.example.michael.jungheinrichv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class TableContent extends Thread
{
    private LinkedList<LinkedList<String>> list;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String HOSTNAME = "localhost";
    private final int PORTNR = 8085;
    private static String table;


    public TableContent()
    {

    }


    @Override
    public void run() {
        list = new LinkedList<LinkedList<String>>();
        Socket socket;
        try {
            socket = new Socket("192.168.8.105", PORTNR);
            OutputStream os = socket.getOutputStream();
            out = new ObjectOutputStream(os);
            out.writeObject(table);


            InputStream is = socket.getInputStream();
            in = new ObjectInputStream(is);
            String data = (String)in.readObject();

            //System.out.println("Daten beim Client:");
            //System.out.println("Data: "+data);
            String [] split = data.split("-");
            String [] splits;
            LinkedList<String> d;
            for(int j = 0; j < split.length; j++) {

                splits = split[j].split(";");

                for (int i = 0; i < splits.length; i++) {
                    //System.out.println("aus Data: "+s);
                    d = new LinkedList<>();
                    d.add(splits[i]);
                    list.add(d);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        this.interrupt();
    }

    public LinkedList<LinkedList<String>> getList(String tableName) {
        table = tableName;
        this.run();

        return list;
    }
}
