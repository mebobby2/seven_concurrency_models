import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

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

            String message = args.length == 0 ? "Hello World" : args[0];

            byte[] messageBytes = message.getBytes();
            out.write(messageBytes, 0, messageBytes.length);
            out.flush();

            byte [] buffer = new byte[1024];
            while (in.read(buffer) != -1) {
                System.out.println("Received back " + new String(buffer));
                socket.close();
            }
        } catch (SocketException e) {
            System.out.println("DONE");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
