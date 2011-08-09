package org.spoutcraft.launcher.AsyncDownload;

import java.io.*;
import java.net.*;

/**
 * Downloads stuff asynchroniously.
 * In fact, it's a modified version of StackOverflow sample ;)
 */
public class Download implements Runnable {

    private static final int BUFFER = 1024;

    private URL url;
    private int size = -1;
    private int downloaded = 0;
    private String outPath;
    private DownloadListener listener;

    public Download(String url, String outPath) throws MalformedURLException {
        this.url = new URL(url);
        this.outPath = outPath;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

            connection.connect();

            if (connection.getResponseCode() / 100 != 2) {
                throw new IOException("Incorrect response code: " + connection.getResponseCode());
            }

            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                if (listener != null) listener.stateChanged(outPath, 0);
                stream = connection.getInputStream();
                FileOutputStream out = new FileOutputStream(outPath);
                byte[] buffer = new byte[BUFFER];
                int length;
                while ((length = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                stream.close();
                out.close();
                if (listener != null) listener.stateChanged(outPath, 100);
                return;
            }

            if (size == -1) {
                size = contentLength;
                stateChanged();
            }

            file = new RandomAccessFile(outPath, "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (true) {
                byte buffer[];
                if (size - downloaded > BUFFER) {
                    buffer = new byte[BUFFER];
                } else {
                    buffer = new byte[size - downloaded];
                }

                int read = stream.read(buffer);
                if (read == -1)
                    break;

                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ignored) {
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void stateChanged() {
        if (listener != null) listener.stateChanged(outPath, getProgress());
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }
}
