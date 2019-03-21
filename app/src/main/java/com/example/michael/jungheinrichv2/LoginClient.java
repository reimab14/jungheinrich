package com.example.michael.jungheinrichv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class LoginClient extends Thread {
    private String ip_address = "10.96.245.145";
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private final String HOSTNAME = "localhost";
    private final int PORTNR = 8085;
    private static String anfrage = "";

    private static LinkedList<String> list;
    private TableContent content = new TableContent();
    private String result="";


    public static void main(String [] args)
    {
        ReportClient obj = new ReportClient();
        obj.start();
        while(!obj.isInterrupted());

        obj.outputList();
    }

    public LoginClient() {
    }

    @Override
    public synchronized void run() {

        list = new LinkedList<>();
        Socket socket;
        try {
            //socket = new Socket("0.0.0.0",PORTNR);
            //System.out.println(socket.getInetAddress());
            //10.0.2.2
            socket = new Socket(ip_address, PORTNR);
            System.out.println(socket.getInetAddress());

            if(anfrage.contains("checkPersNr"))
            {
                OutputStream os = socket.getOutputStream();
                out = new ObjectOutputStream(os);
                out.writeObject(anfrage);

                InputStream is = socket.getInputStream();
                in = new ObjectInputStream(is);
                result = (String)in.readObject();
                socket.close();
            }
            else if(anfrage.equals("savedata"))
            {
                OutputStream os = socket.getOutputStream();
                out = new ObjectOutputStream(os);
                out.writeObject(anfrage);
                os.close();
                socket.close();
            }
            else if(anfrage.equals("loaddata"))
            {
                OutputStream os = socket.getOutputStream();
                out = new ObjectOutputStream(os);
                out.writeObject(anfrage);

                InputStream is = socket.getInputStream();
                in = new ObjectInputStream(is);
                result = (String)in.readObject();
                socket.close();
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        this.interrupt();
    }

    public void setAnfrage(String name)
    {
        anfrage = name;
    }

    public String getResult()
    {
        return result;
    }
}
