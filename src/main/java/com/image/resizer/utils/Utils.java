package com.image.resizer.utils;

public class Utils {

    public static String getFilePath(String filename) {
        return System.getProperty("user.dir") + "/src/main/resources/static/" + filename;
    }

    public static String getResizedFilename(String filename) {
        return filename.replace(".mp4", "_resize.mp4");
    }
}
