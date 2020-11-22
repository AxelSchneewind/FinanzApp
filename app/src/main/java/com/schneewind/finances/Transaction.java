package com.schneewind.finances;

public class Transaction {
    double height;
    private String date;
    private String time;
    public String name;

    int getMonth(){
        int m = Integer.valueOf(date.split("\\.")[1]);
        return m;
    }

    String displayedHeight(){
        String h = String.valueOf(height);
        if(h.substring(h.length() - 2).contains(".")) h = h.concat("0");
        if(h.toCharArray()[0] != "-".toCharArray()[0]) h = "+" + h;
        h = h.concat(" €").replace(".",",");
        return h;
    }
    String displayedHeight(double height){
        String h = String.valueOf(height);
        while(h.split("\\.")[1].length() > 2){
            h = h.substring(0,h.length() - 1);
        }
        if(h.substring(h.length() - 2).contains(".")) h = h.concat("0");
        h = h.concat(" €").replace(".",",");
        return h;
    }

    String displayedDate(){
        String[] d = date.split("\\.");
        d[1] = String.valueOf(Integer.valueOf(d[1]) + 1);
        String date = d[0] + "." + d[1] + "." + d[2];
        return date;
    }

    String displayedTime(){
        String t = time;
        if(t.split(":")[0].length() < 2) t = 0 + t;
        if(t.split(":")[1].length() < 2) t = t.split(":")[0] + ":0" + t.split(":")[1];
        return t;
    }

    Transaction(double transaction_height, String transaction_date, String transaction_time, String transaction_name){
        height =  transaction_height;
        date = transaction_date;
        time = transaction_time;
        name = transaction_name;
    }
}
