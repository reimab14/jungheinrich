package com.example.michael.jungheinrichv2;

import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportClient extends Thread {

    private String ip_address = "10.0.0.14";
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String HOSTNAME = "localhost";
    private final int PORTNR = 8085;
    private static String table = "report";

    private static LinkedList<String> list;
    private LinkedList<LinkedList<String>> contentList;
    private static Scanner scan = new Scanner(System.in);
    //private TableContent content = new TableContent();


    public static void main(String [] args)
    {
        ReportClient obj = new ReportClient();
        obj.start();
        while(!obj.isInterrupted());

        obj.outputList();
        //String table = "";
        //while(!table.equals("no")) {
        //    System.out.println("\nTabelle angeben: ");
        //    obj.setTable(scan.next());
        //    while(!obj.isInterrupted());
        //    obj.outputList();
        //}
    }

    public ReportClient() {
    }

    @Override
    public synchronized void run() {

    list = new LinkedList<>();
        Socket socket;
        try {
            socket = new Socket(ip_address, PORTNR);
            System.out.println(socket.getInetAddress());
            OutputStream os = socket.getOutputStream();
            out = new ObjectOutputStream(os);
            out.writeObject(table+";"+MainActivity.persNr);
            InputStream is = socket.getInputStream();
            in = new ObjectInputStream(is);
            String data = (String)in.readObject();

            for(String s : data.split(";"))
            {
                addToList(s);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        this.interrupt();
    }

    public void addToList(String s)
    {
        list.add(s);
    }

    public LinkedList<String> getList()
    {
        return list;
    }

    public void outputList()
    {
        System.out.println(list.size());
        for(String s : list)
        {
            System.out.println(s);
        }
    }

    public void setTable(String name)
    {
        table = name;
        this.run();
    }

    public void outputContent()
    {
        String output;

        for(LinkedList<String> l : contentList)
        {
            output = "";

            for(String s : l)
            {
                output += s + " ";
            }
            System.out.println(output);
        }
    }
}
