package com.example.michael.jungheinrichv2;

import java.util.HashMap;
import java.util.LinkedList;

public class SQLParser
{
    private String statement;
    private String wStatement;
    private int count;
    private LinkedList<String> krits;
    private LinkedList<String> appKrits;

    public SQLParser(String stmt)
    {
        this.statement = stmt;
        krits = new LinkedList<>();
    }

    public void setAppKrits(LinkedList<String> appKrits) {
        this.appKrits = appKrits;

        SetKrits();
    }

    private void SetKrits()
    {
        System.out.println(statement);

        wStatement = statement.replaceAll("\\?\\?\\?", "§\\?");

        

        String[] replace = wStatement.split("§");

        count = replace.length;
        System.out.println(count);

        statement = replace[0];

        for(int i = 1; i <= appKrits.size(); i++) {
            String s = replace[i].replace("?",appKrits.get(i-1));
            statement += " "+s;
        }
    }

    public String getStatement() {
        return statement;
    }

    public int getCount()
    {
        return count;
    }

    public LinkedList<String> getKrits() {
        return krits;
    }

    public boolean parseStatement()
    {


        if(statement.contains("???"))
            {

                wStatement = statement.replace("???", "?");

                String[] replace = wStatement.split("\\?");

                count = replace.length;



                for(int i = 1; i < count; i++)
                {
                    String s = replace[i];

                    char[] labelname =  s.toCharArray();

                    int index = 0;
                    for(int j = 6; i < s.length(); j++)
                    {
                        if(labelname[j] == ' ')
                        {
                            index = j;
                            break;
                        }
                    }

                    String krit = s.substring(6,index);
                krits.add(krit);
                System.out.println(krit);
            }


           return true;
        }
        else {
            return false;
        }
    }

    public static void main(String [] args)
    {
        SQLParser obj = new SQLParser("SELECT   anr Anr /* TYP=ANR PRF=N ADDTEXT=N  */,\n" +
                "         ktxt Beschreibung /* TYP=TEXT PRF=N ADDTEXT=N  */,\n" +
                "         unit VPE1 /* TYP=BDT46 PRF=N ADDTEXT=N  */,\n" +
                "         name_vpe2 VPE2 /* TYP=BDT46 PRF=N ADDTEXT=N  */,\n" +
                "         name_vpe3 VPE3 /* TYP=BDT46 PRF=N ADDTEXT=N  */,\n" +
                "         name_vpe4 VPE4 /* TYP=BDT46 PRF=N ADDTEXT=N  */,\n" +
                "         name_vpe5 VPE5 /* TYP=BDT46 PRF=N ADDTEXT=N  */\n" +
                "FROM     AST\n" +
                "WHERE    unit = ??? /* U VPE TYP=BDT46 PRF=J ADDTEXT=N */ \n" +
                "OR        name_vpe2 = ??? /* U VPE */ \n" +
                "OR        name_vpe3 = ??? /* U VPE */ \n" +
                "OR        name_vpe4 = ??? /* U VPE */ \n" +
                "OR        name_vpe5 = ??? /* U VPE */");
        if(obj.parseStatement())
        {
            LinkedList<String> krits = obj.getKrits();

            LinkedList<String> appKrits = new LinkedList<>();

            for(int i = 0; i < krits.size(); i++)
            {
                appKrits.add("TestKriterium"+(i+1));
            }

            obj.setAppKrits(appKrits);

            System.out.println(obj.statement);
        }

    }
}
