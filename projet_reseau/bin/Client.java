package projet_reseau;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.IOException;

public class Client {
	public static void main(String[] args) {

		try {
			Socket socket = new Socket("127.0.0.1", 4567);
			OutputStream out = socket.getOutputStream();
			
			out.write(42);

			out.close();
			socket.close();
		} catch (UnknownHostException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
