package com.ztemt.test.platform.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

public class HttpPost {

    private static final String BOUNDARY = UUID.randomUUID().toString();
    private static final String PREFIX = "--";
    private static final String LINEND = "\r\n";
    private static final String CHARSET = HTTP.UTF_8;

    public static String post(String url, Map<String, String> params,
            File file, ProgressListener listener)
            throws IOException {
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setChunkedStreamingMode(10 * 1024);
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", CHARSET);
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                + BOUNDARY);

        StringBuilder sb1 = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("Content-Disposition: form-data; name=\""
                    + entry.getKey() + "\"" + LINEND);
            sb1.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb1.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb1.append(LINEND);
            sb1.append(entry.getValue());
            sb1.append(LINEND);
        }

        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.write(sb1.toString().getBytes());

        StringBuilder sb2 = new StringBuilder();
        sb2.append(PREFIX);
        sb2.append(BOUNDARY);
        sb2.append(LINEND);
        sb2.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                + file.getName() + "\"" + LINEND);
        sb2.append("Content-Type: application/octet-stream; charset="
                + CHARSET + LINEND);
        sb2.append(LINEND);
        dos.write(sb2.toString().getBytes());

        int bytesRead, bytesAvailable, bufferSize;
        int maxBufferSize = 10 * 1024;
        int progress = 0;
        long totalSize = file.length();
        long length = 0;
        byte[] buffer;

        InputStream is = new FileInputStream(file);
        bytesAvailable = is.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        bytesRead = is.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            length += bufferSize;
            int p = (int) ((length * 100) / totalSize);
            if (progress != p) {
                progress = p;
                listener.onProgressUpdate(progress);
            }
            bytesAvailable = is.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = is.read(buffer, 0, bufferSize);
        }

        is.close();
        dos.write(LINEND.getBytes());

        byte[] endData = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        dos.write(endData);
        dos.flush();

        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        StringBuilder sb = new StringBuilder();
        if (res == HttpStatus.SC_OK) {
            int ch;
            while ((ch = in.read()) != -1) {
                sb.append((char) ch);
            }
        }
        in.close();
        dos.close();
        conn.disconnect();
        return sb.toString();
    }

    public interface ProgressListener {
        public void onProgressUpdate(int progress);
    }
}
