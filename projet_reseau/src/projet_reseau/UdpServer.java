package projet_reseau;

import java.io.*; 
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

class UdpServer{ 
	public static void main(String argv[]){ 
		try{
			DatagramSocket socket = new DatagramSocket(4567); 

			// Création d'un paquet (vide) qui servira à stocker un paquet entrant
			int tailleMax = 1024; 
			byte buffer[] = new byte[tailleMax];
			DatagramPacket data = new DatagramPacket(buffer,buffer.length); 

			// On attend de recevoir un paquet
			socket.receive(data);
			UdpServer serveur = new UdpServer();
			serveur.run(data, buffer, socket);
			
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	public void run(DatagramPacket data, byte buffer[], DatagramSocket socket) throws IOException {
		byte [] buff = data.getData();
		ByteArrayInputStream in = new ByteArrayInputStream(buff);
		int n1 = this.readNumber(in);
		int n2 = this.readNumber(in);
		char signe = this.readSigne(in);

		int resultat = this.calculResultat(n1, n2, signe);
		//serveur.readNumber(data.getData());
		
		System.out.println("Client Request : "+n1+" "+signe+" "+n2);
		System.out.println("Resultat : "+resultat);

		// On affiche la source du paquet
		System.out.println("De : " + data.getAddress() + ", port " + data.getPort()); 

		// Construction du paquet UDP à envoyer
		// Le paquet contient le texte reçu mis en majuscules.
		buffer = Tools.intToBytes(resultat); 
		int length = buffer.length; 
		// Important: Le paquet contient la destination et le port
		DatagramPacket data2 = new DatagramPacket(buffer, length, data.getAddress(), data.getPort()); 

		// On envoie le même paquet en retour
		socket.send(data2); 
	}
	
	public int readNumber(ByteArrayInputStream in) throws IOException {
		byte [] buffer = new byte[4];
		in.read(buffer);
		return Tools.bytesToInt(buffer);
	}

	public char readSigne(ByteArrayInputStream in) throws IOException{	 
		return (char) in.read();
	}
	public int calculResultat (int n1, int n2, int signe) {
		int result;
		switch (signe) {
		case '+': {
			result = n1 + n2;
			break;
		}
		case '-': {
			result = n1 - n2;
			break;
		}
		case '*': {
			result = n1 * n2;
			break;
		}
		case '/': {
			result = n1 / n2;
			break;
		}
		default:
			System.out.println("Signe incorrecte.. ");
			result = -1;
			break;
		}
		return result;
	}
}
