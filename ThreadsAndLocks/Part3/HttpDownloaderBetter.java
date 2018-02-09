import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Downloader
 */
public class HttpDownloaderBetter extends Thread {
    private InputStream in;
    private OutputStream out;
    private CopyOnWriteArrayList<ProgressListener> listeners;

    /**
     * ProgressListener
     */

    // Nested interfaces are static by default. You don’t have to
    // mark them static explicitly as it would be redundant.
    public interface ProgressListener {
        void onProgress(int total);
    }

    public HttpDownloaderBetter(URL url, String outputFilename) throws IOException {
        in = url.openConnection().getInputStream();
        out = new FileOutputStream(outputFilename);
        listeners = new CopyOnWriteArrayList<ProgressListener>();
    }

    public synchronized void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    private void updateProgress(int n) {
        for (ProgressListener listener : listeners)
            listener.onProgress(n);
    }

    public void run() {
        int n = 0, total = 0;
        byte[] buffer = new byte[1024];

        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                total += n;
                updateProgress(total);
            }
            out.flush();
        } catch (IOException e) {
            //TODO: handle exception
        }
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html");
        HttpDownloaderBetter downloader = new HttpDownloaderBetter(url, "/Users/bob/Documents/Development/learn/seven_concurrency_models/ThreadsAndLocks/progress.txt");

        downloader.addListener(new ProgressListener(){
            @Override
            public void onProgress(int total) {
                System.out.println("Received " + total + " bytes");
            }
        });

        downloader.start();
        downloader.join();
    }

}
