package com.schneewind.finances;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {
    public static final String FILENAME = "transactions.txt";

    public static void writeData(ArrayList<String> entries, String filename, Context context){
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(entries);
            oos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void writeData(ArrayList<String> entries, Context context) {
        writeData(entries,FILENAME, context);
    }

    public static ArrayList<String> readData(String filename, Context context){
        ArrayList<String> entryList = null;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            entryList = (ArrayList<String>) ois.readObject();
        } catch (FileNotFoundException e) {
            entryList = new ArrayList<>();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return entryList;
    }
    public static ArrayList<String> readData(Context context){
        return readData(FILENAME, context);
    }
}
