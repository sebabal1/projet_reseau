package projet_reseau;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public class Server {
	public static void main(String[] args) {
		Socket client;
		ServerSocket server;
		InputStream in;
		OutputStream out;
		try {
			server = new ServerSocket(4567); //TCP
			client = server.accept();

			in = client.getInputStream();
			out = client.getOutputStream();

			int data = in.read();
			System.out.println(data);

		} catch (IOException e) {
			System.out.println(e);
			System.exit(-1);
		}
	}
	
	
}
