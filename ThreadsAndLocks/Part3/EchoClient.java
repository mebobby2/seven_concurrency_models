import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * EchoClient
 */
public class EchoClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4567);
            System.out.println("Connected to server");

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            byte[] message = "Hello World".getBytes();
            out.write(message, 0, message.length);
            out.flush();

            while (in.read(buffer) == -1) {
                System.out.println("Received back " + new String(buffer));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
