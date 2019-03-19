package com.example.michael.jungheinrichv2;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
                    ///////////////////////////////////////////////////////////////
                    //*****beschreibung zu bezeichnung ändern nd vergessen*******//
                    ///////////////////////////////////////////////////////////////
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
                else if(s.contains("checkPersNr"))
                {
                    System.out.println("check Personalnummer");
                    boolean check = access.checkPersNr(s.split(";")[1]);
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(os);
                    if(check)
                    {
                        out.writeObject("ok");
                    }
                    else out.writeObject("ungültig");
                }
                else if(s.equals("savedata"))
                {
                    System.out.println("save data");
                    access.saveData();
                    socket.close();
                }
                else if(s.equals("loaddata"))
                {
                    System.out.println("load data");
                    data = access.loadData();
                    OutputStream os = socket.getOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(os);
                    out.writeObject(data);
                    socket.close();
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
                data = "Exception;"+ex.getMessage();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                data = "Exception;"+ex.getMessage();
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
                data = "Exception;"+ex.getMessage();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                data = "Exception;"+ex.getMessage();
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
                data = "Exception;"+ex.getMessage();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                data = "Exception;"+ex.getMessage();
            }
            if(data==""){
                data = "Exception;nodata";
            }
            System.out.println("\n\n");
            return data;
        }

        public Boolean checkPersNr(String PersNr)
        {
            boolean check = false;

            try {
                Class.forName(db_driver);

                con = DriverManager.getConnection(db_url, db_username, db_password);
                System.out.println(PersNr);
                String cmd = "SELECT r.beschreibung FROM report r INNER JOIN reportbenutzergruppe rbg ON r.querynr = rbg.querynr INNER JOIN personal p ON rbg.benutzergrp = p.benutzergrp " +
                        "WHERE p.persnr = "+PersNr;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(cmd);

                if(rs.next())
                {
                    check = true;
                }

                con.close();

            } catch (ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
                check=true;
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
                check=true;
            }
            return check;
        }

        public void saveData() {
            AESUtils util = new AESUtils();
            try {
                File file = new File("D:/Jungheinrich/Jungheinrich/Jungheinrichv2/app/src/main/res/konfigdata.txt");
                OutputStream out = new FileOutputStream(file);
                String ergebnis = db_username+";";

                String encrypted = "";
                String sourceStr = db_password;
                try {
                    encrypted = util.encrypt(sourceStr);
                    System.out.println("Verschlüsselt: " + encrypted);
                    encrypted+=";";
                    ergebnis += encrypted;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                ergebnis += db_hostname+";"+db_port+";"+db_sid;

                System.out.println(ergebnis);
                out.write(ergebnis.getBytes());
                out.close();
                System.out.println("Daten wurden gespeichert");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public String loadData() {
            String daten = "";
            AESUtils util = new AESUtils();
            try {
                System.out.println("Loaddata aufgerufen");
                File file = new File("D:/Jungheinrich/Jungheinrich/Jungheinrichv2/app/src/main/res/konfigdata.txt");
                InputStream in = new FileInputStream(file);
                //FileInputStream fileInputStream = openFileInput("savedata");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                daten = reader.readLine();

                System.out.println(daten);

                String[] line = daten.split(";");

                String encrypted = line[1];
                System.out.println("encrypted: "+encrypted);
                String decrypted = "";
                try {
                    decrypted = util.decrypt(encrypted);
                    System.out.println("decrypted:" +decrypted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                db_username = line[0];
                db_password = decrypted;
                db_hostname = line[2];
                db_port = line[3];
                db_sid = line[4];

                daten = db_username+";"+db_password+";"+db_hostname+";"+db_port+";"+db_sid;
                System.out.println(daten);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                daten = db_username+";"+db_password+";"+db_hostname+";"+db_port+";"+db_sid;
            }
            db_url = "jdbc:oracle:thin:@"+db_hostname+":"+db_port+":"+db_sid;
            return daten;
        }
    }

    class AESUtils
    {

        private final byte[] keyValue =
                new byte[]{'c', 'o', 'd', 'i', 'n', 'g', 'a', 'f', 'f', 'a', 'i', 'r', 's', 'c', 'o', 'm'};

        public AESUtils()
        {
        }

        public String encrypt(String cleartext)
                throws Exception {
            byte[] rawKey = getRawKey();
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            return toHex(result);
        }

        public String decrypt(String encrypted)
                throws Exception {

            byte[] enc = toByte(encrypted);
            byte[] result = decrypt(enc);
            return new String(result);
        }

        private byte[] getRawKey() throws Exception {
            SecretKey key = new SecretKeySpec(keyValue, "AES");
            byte[] raw = key.getEncoded();
            return raw;
        }

        private byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
            SecretKey skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        }

        private byte[] decrypt(byte[] encrypted)
                throws Exception {
            SecretKey skeySpec = new SecretKeySpec(keyValue, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        }

        public byte[] toByte(String hexString) {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                        16).byteValue();
            return result;
        }

        public String toHex(byte[] buf) {
            if (buf == null)
                return "";
            StringBuffer result = new StringBuffer(2 * buf.length);
            for (int i = 0; i < buf.length; i++) {
                appendHex(result, buf[i]);
            }
            return result.toString();
        }

        private final static String HEX = "0123456789ABCDEF";

        private void appendHex(StringBuffer sb, byte b) {
            sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
        }
    }
}
