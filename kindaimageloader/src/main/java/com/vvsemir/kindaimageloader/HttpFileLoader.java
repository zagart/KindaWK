package com.vvsemir.kindaimageloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpFileLoader {
    public static void downloadToFile(File file, URL url) {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = url.openStream();
            outStream = new FileOutputStream(file);

            byte[] buffer = new byte[4*1024];
            int bytesRead;

            while((bytesRead = inStream.read(buffer)) !=-1){
                outStream.write(buffer, 0, bytesRead);
            }

            outStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if ( outStream != null ) {
                    outStream.close();
                }
                inStream.close();
            }
            catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
    }
}
