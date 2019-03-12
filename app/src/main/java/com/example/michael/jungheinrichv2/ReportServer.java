package com.example.michael.jungheinrichv2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class ReportServer {

    private ServerSocket server;
    private Socket client;
    private final int port = 8085;
    private DBAccess access;

    public static void main(String[] args)
    {
        ReportServer obj = new ReportServer();
    }

    public ReportServer()
    {
        System.out.println("Im Konstruktor");
        ServerThread st = new ServerThread();
        st.start();
    }


    class ServerThread extends Thread
    {
        public ServerThread()
        {
            //while (!isInterrupted()) {
            System.out.println("Im Serverthread");
                try {
                    //byte[] addr = {10,0,2,15};
                    //server = new ServerSocket(port, 0, InetAddress.getByName("192.168.8.102"));

                    server = new ServerSocket(port);


                    System.out.println(server.getInetAddress());
                    server.setSoTimeout(100000);
                    System.out.println("Server started on Port " + port);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            //}
        }


        @Override
        public void run() {

            while(!interrupted())
            {
                try {
                    Socket socket = server.accept();
                    ClientCommunicationThread cct = new ClientCommunicationThread(socket);
                    cct.start();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }catch(NullPointerException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }

        }

    }

    class ClientCommunicationThread extends Thread
    {
        private Socket socket;
        String data;
        String colNames;

        public ClientCommunicationThread(Socket st)
        {
            socket = st;
            if(access == null)
            {
                access = new DBAccess();
            }
            System.out.println("ClientCommunicationThread gestartet");
        }

        @Override
        public synchronized void run()
        {
            InputStream is;
            try {


                System.out.println("Socket Connected: "+socket.isConnected());
                is = socket.getInputStream();
                ObjectInputStream in = new ObjectInputStream(is);
                String s = (String) in.readObject();
                System.out.println(s);
                if(s.contains("report")) {
                    System.out.println("s equals report");
                    data = access.ExecuteReport("beschreibung", s.split(";")[1]);
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(os);
                    out.writeObject(data);
                }
                else if(s.contains("dbdata"))
                {
                    System.out.println("change dbdata");
                    String benutzer = s.split(";")[1];
                    String passwort = s.split(";")[2];
                    String hostname = s.split(";")[3];
                    String port = s.split(";")[4];
                    String sid = s.split(";")[5];
                    System.out.println(benutzer+";"+passwort+";"+hostname+";"+port+";"+sid);
                    access = new DBAccess(benutzer, passwort, hostname, port, sid);
                }
                else if(s.contains("SELECT") && s.contains("FROM"))
                {
                    System.out.println("Select abfrage");
                    data = access.ExecuteSQL(s);
                    colNames = access.getColNames();

                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(os);
                    String[] send = {colNames, data};
                    out.writeObject(send);
                    socket.close();
                }
                else {
                    data = access.Execute(s, "sql");
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(os);
                    out.writeObject(data);
                }
                //System.out.println(data);


                this.interrupt();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }


        }


    }

    class DBAccess
    {
        private String db_driver = "oracle.jdbc.driver.OracleDriver";
        private String db_username = "";
        private String db_password = "";
        private String db_hostname = "";
        private String db_port = "";
        private String db_sid = "";
        private String db_url = "";
        private Connection con;
        private String colNames = "";

        public DBAccess()
        {
            System.out.println("Konstruktor Standard aufgerufen!");
            this.db_username = "reimab14";
            this.db_password = "reimab14";
            this.db_hostname = "db2.htl-kaindorf.at";
            this.db_port = "1521";
            this.db_sid = "orcl";
            this.db_url = "jdbc:oracle:thin:@"+db_hostname+":"+db_port+":"+db_sid;
        }

        public DBAccess(String benutzer, String passwort, String hostname, String port, String sid)
        {
            System.out.println("Konstruktor aufgerufen!");
            System.out.println(benutzer);
            this.db_username = benutzer;
            this.db_password = passwort;
            this.db_hostname = hostname;
            this.db_port = port;
            this.db_sid = sid;
            this.db_url = "jdbc:oracle:thin:@"+db_hostname+":"+db_port+":"+db_sid;
        }

        public String getColNames()
        {
            return colNames;
        }

        public String Execute(String beschreibung, String cols)
        {
            String data = "";


            try {
                Class.forName(db_driver);

                con = DriverManager.getConnection(db_url, db_username, db_password);
                String cmd = "SELECT "+cols+" FROM report WHERE LOWER(beschreibung) = '"+beschreibung.toLowerCase()+"'";
              //  System.out.println(cmd);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(cmd);
              //  System.out.println("Statement abgerufen");

              //  System.out.println("Neues resultset geladen "+rs.toString());
                ResultSetMetaData metaData = rs.getMetaData();

                rs.next();
                data = rs.getString(1);



                System.out.println(data);

                con.close();

            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }



            return data;
        }

        public String ExecuteSQL(String sql)
        {
            String data = "";
            colNames = "";

            try {
                Class.forName(db_driver);

                con = DriverManager.getConnection(db_url, db_username, db_password);

                  System.out.println(sql);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                  System.out.println("Statemet abgerufen");

                //  System.out.println("Statement: "+cmd);

                //  System.out.println("Neues resultset geladen "+rs.toString());
                ResultSetMetaData metaData = rs.getMetaData();

                for(int i = 1; i <= metaData.getColumnCount(); i++)
                {

                    colNames += metaData.getColumnName(i) + ";";
                }

                System.out.println("\n\nDaten am server\n");
                System.out.println(colNames);
                while(rs.next())
                {


                    for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                    {
                        //System.out.println(rs.getString(i));
                        data += rs.getString(i) +";";
                    }
                    data += "_";
                }
                System.out.println(data);

                con.close();

            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }



            return data;
        }

        public String ExecuteReport(String col, String persNr)
        {
            String data = "";
            System.out.println("Set Table Names");
            System.out.println(db_username+";"+db_password+";"+db_hostname+";"+db_port+";"+db_sid);
            System.out.println(db_url);
            try {
                Class.forName(db_driver);

                con = DriverManager.getConnection(db_url, db_username, db_password);
                System.out.println(""+col);
                System.out.println(""+persNr);
                String cmd = "SELECT r."+col+" FROM report r INNER JOIN reportbenutzergruppe rbg ON r.querynr = rbg.querynr INNER JOIN personal p ON rbg.benutzergrp = p.benutzergrp " +
                        "WHERE p.persnr = "+persNr;
                //String cmd = "SELECT "+col+" FROM report";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(cmd);
                ResultSetMetaData meta = rs.getMetaData();


                while(rs.next())
                {
                    data += rs.getString(col)+";";
                    System.out.println(rs.getString(col));

                }

                con.close();

            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            //System.out.println("Daten am Server: ");
            //for(String s : data.split(";"))
            //{
            //    System.out.println(s);
            //}
            System.out.println("\n\n");



            return data;
        }
    }
}
