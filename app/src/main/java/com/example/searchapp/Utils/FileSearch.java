package com.example.searchapp.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        Log.d(TAG, "getDirectoryPaths: direcotry " + directory);
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        Log.d(TAG, "getDirectoryPaths: pathArray " + pathArray);
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        Log.d(TAG, "getFilePaths: getting filepaths from " +directory );
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        if(listfiles != null) {
            for (int i = 0; i < listfiles.length; i++) {
                if (listfiles[i].isFile()) {
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        }
        Log.d(TAG, "getFilePaths: all paths: " + pathArray);
        return pathArray;
    }
}
