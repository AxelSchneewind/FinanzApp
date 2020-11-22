package com.schneewind.finances;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {
    public static final String FILENAME = "transactions.txt";

    public static void writeDataToFile(ArrayList<String> entries, String filename, Context context){
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/Downloads/")
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
    public static void writeDataToDefaultFile(ArrayList<String> entries, Context context) {
        writeDataToFile(entries,FILENAME, context);
    }
    public static void writeDataToExternalFile(ArrayList<String> entries, Context context){
        writeDataToFile(entries,  + FILENAME, context);
    }

    public static ArrayList<String> readDataFromFile(String filename, Context context){
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
    public static ArrayList<String> readDataFromDefaultFile(Context context){
        return readDataFromFile(FILENAME, context);
    }
}
