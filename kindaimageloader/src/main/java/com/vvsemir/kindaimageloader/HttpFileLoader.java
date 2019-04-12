package com.vvsemir.kindaimageloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpFileLoader {
    private static int BUFFER_SIZE = 8 * 1024;

    public static void downloadToFile(File file, URL url) {
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = url.openStream();
            outStream = new FileOutputStream(file);

            byte[] buffer = new byte[BUFFER_SIZE];
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

    public static byte[] downloadBytes(URL url) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        try {
            inputStream = url.openStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }

        return byteBuffer.toByteArray();
    }
}
