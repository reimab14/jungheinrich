package com.example.michael.jungheinrichv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class TableClient  extends Thread {
    //TableClient Class
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String HOSTNAME = "localhost";
    private final int PORTNR = 8085;
    private Socket socket;
    private String statement;
    private String colNames;
    private String report;

    private static ArrayList<String> list;
    private LinkedList<ArrayList<String>> contentList;
    private static Scanner scan = new Scanner(System.in);
    private TableContent content = new TableContent();


    public static void main(String [] args)
    {
        TableClient obj = new TableClient();
        obj.setReport("Test");
        obj.receiveStatement();

        obj.start();
        while(!obj.isInterrupted());

        //System.out.println("Vor outputlist");
        obj.outputList();
        //String table = "";
        //while(!table.equals("no")) {
        //    System.out.println("\nTabelle angeben: ");
        //    obj.setTable(scan.next());
        //    while(!obj.isInterrupted());
        //    obj.outputList();
        //}
    }

    public TableClient() {

        contentList = new LinkedList<>();
    }

    public void setReport(String report)
    {
        this.report = report;
    }

    public String getColNames()
    {
        return colNames;
    }

    public synchronized void receiveStatement()
    {
        try {
            socket = new Socket("10.151.1.189", PORTNR);
            System.out.println(socket.getInetAddress());
            OutputStream os = socket.getOutputStream();
            out = new ObjectOutputStream(os);
            out.writeObject(report);


            InputStream is = socket.getInputStream();
            in = new ObjectInputStream(is);
            statement = (String)in.readObject();

            //System.out.println("Daten beim Client:");
            //System.out.println("Data: "+data);



        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }catch(NullPointerException ex)
        {
            System.out.println(ex.getMessage());
        }
        //disconnect();
        this.interrupt();
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement)
    {
        this.statement = statement;
    }

    @Override
    public synchronized void run() {

        try {
            socket = new Socket("10.151.1.189", PORTNR);
            System.out.println(socket.getInetAddress());
            OutputStream os = socket.getOutputStream();
            out = new ObjectOutputStream(os);
            out.writeObject(statement);


            InputStream is = socket.getInputStream();
            in = new ObjectInputStream(is);
            String [] receive = (String[]) in.readObject();
            colNames = receive[0];
            String data = receive[1];

            //System.out.println("Daten beim Client:");
            //System.out.println("Data: "+data);

            list = new ArrayList<String>();


            for(String r : data.split("_")) {
                for (String s : r.split(";")) {

                   // System.out.println(s);
                    list.add(s);
                }

                contentList.add(list);
                list = new ArrayList<>();
            }
            System.out.println("Draußen aus der schleife");

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }catch(NullPointerException ex)
        {
            System.out.println(ex.getMessage());
        }
        //disconnect();
        this.interrupt();
    }



    public LinkedList<ArrayList<String>> getList()
    {
        return contentList;
    }

    public void outputList()
    {



        for(ArrayList<String> rows : contentList)
        {

            for(String s : rows)
            {
                //System.out.println("Outputlist");
                System.out.print(s+" | ");
            }
            System.out.println("\n");
        }
    }

    public void disconnect()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}