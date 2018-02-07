import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Downloader
 */
public class Downloader extends Thread {
    private InputStream in;
    private OutputStream out;
    private ArrayList<ProgressListener> listeners;

    /**
     * ProgressListener
     */

    // Nested interfaces are static by default. You don’t have to
    // mark them static explicitly as it would be redundant.
    public interface ProgressListener {
        void onProgress(int total);
    }

    public Downloader(URL url, String outputFilename) throws IOException {
        in = url.openConnection().getInputStream();
        out = new FileOutputStream(outputFilename);
        listeners = new ArrayList<ProgressListener>();
    }

    public synchronized void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    // updateProgress() calls an alien method—a method it knows nothing
    // about. That method could do anything, including acquiring another lock.
    // If it does, then we’ve acquired two locks without knowing whether
    // we’ve done so in the right order. That can lead to deadlock.
    // private synchronized void updateProgress(int n) {
    //     for (ProgressListener listener : listeners)
    //         listener.onProgress(n);
    // }

    // The only solution is to avoid calling alien methods while holding a lock
    // 1. Remove the method lock on updateProgress
    // 2. Have a lock inside the method that clones the listeners collection
    // 3. Now, the call to onProgress is outside of a lock, hence no deadlocks
    //    can happen even if onProgress tries to obtain a lock.
    private void updateProgress(int n) {
        ArrayList<ProgressListener> listenersCopy;
        synchronized(this) {
            listenersCopy = (ArrayList<ProgressListener>)listeners.clone();
        }
        for (ProgressListener listener : listenersCopy)
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
        Downloader downloader = new Downloader(url, "/Users/bob/Documents/Development/learn/seven_concurrency_models/ThreadsAndLocks/progress.txt");

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
