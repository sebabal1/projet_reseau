package projet_reseau;

import java.io.*; 
import java.net.*;
import java.util.Scanner;


@SuppressWarnings("unused")
public class UdpClient { 
    public static void main(String[] args) throws IOException{ 
    	// Adresse IP de la destination
    	InetAddress serveur = InetAddress.getByName("127.0.0.1");
	    int port = 4567;
    	UdpClient client = new UdpClient();
    	
        // Construction du paquet UDP à envoyer
        byte buffer[] = client.getBufferNombre().toByteArray(); 
        int length = buffer.length; 
        // Important: Le paquet contient la destination et le port
        DatagramPacket data = new DatagramPacket(buffer, length, serveur, port); 

        DatagramSocket socket = new DatagramSocket(); 

        // On envoie le paquet dans le socket
        socket.send(data);        
        DatagramPacket dataReceived = new DatagramPacket(new byte[length],length); 
        socket.receive(dataReceived);
        ByteArrayInputStream in = new ByteArrayInputStream(dataReceived.getData());
        
        int resultat = client.readResultat(in);
   
        System.out.println("Resultat : " + resultat); 
        System.out.println("De : " + dataReceived.getAddress() + ", port " + dataReceived.getPort()); 
        socket.close();
        
        
    }
    Scanner scan = new Scanner(System.in);
    
    public ByteArrayOutputStream getBufferNombre() throws IOException {
    	byte[] n1 = demandeNombre();
    	byte[] n2 = demandeNombre();
    	byte signe = demandeSigne();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	out.write(n1);
    	out.write(n2);
    	out.write(signe);
    	return out;
    }
    
    public byte[] demandeNombre() {
    	System.out.println("Veuillez saisir un nombre entier : ");
    	int nombre = this.scan.nextInt();
    	return (byte[]) Tools.intToBytes(nombre);
    }
    public byte demandeSigne() {
    	System.out.println("Veuillez saisir un nombre signe[+ - / *] : ");
    	char signe = this.scan.next().charAt(0);
    	return (byte) signe;
    }
    
    public int readResultat(ByteArrayInputStream in) throws IOException{
    	byte [] buffer = new byte[4];
    	in.read(buffer);
    	return Tools.bytesToInt(buffer);
    }
    
}