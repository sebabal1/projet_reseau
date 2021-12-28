package projet_radiateur;


import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import projet_radiateur.Config.FileFormatException;
import projet_radiateur.Config.Link;
import projet_radiateur.Config.Node;


/**
 * Classe représentant une application numéro *ID* qui envoie périodiquement un message à
 * l'application numéro *ID+1*.
 */
public class App{
    /*
     * Variables de classes
     */
    // taille maximale d'un datagramme, utilisée pour le buffer de reception
    static int MAX_DGRAM_SIZE = 100;
    // Nombre maximum d'applications
    static int NUM_CLIENTS = 3;
    /* Port de départ utilisé lors de l'attribution des ports d'écoute de
    chaque application */
    static int BASE_PORT = 4444;
    // Interval d'envoi de message pour chaque noeud
    int BCAST_INTERVAL = 2000;
    //Chemin vers le fichier scenario.config
    

    /*
     * Variables d'instance
     */
    // Identifiant (unique) de l'application
    private int appId;
    // Port d'écoute du socket
    private final int port;
    // Adresse d'écoute du socket
    private InetAddress address;

    // Objet utiliser pour le log d'événements dans un certain format
    private Logger log;
    
    private final ArrayList<Link> links;
    private final ArrayList<Node> nodes;
    private ArrayList<Integer> voisins;
    private final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    
    private Config config;

    /**
     * Constructeur de l'application
     * @param id entier représentant l'identifiant (unique) de l'application
     */
    public App(int id, int portSource, ArrayList<Link> linkConfig, ArrayList<Node> nodeConfig) throws UnknownHostException{
        this.appId = id;
        // On détermine le port d'écoute sur base de l'ID
        this.port = portSource;
        this.links = linkConfig;
        this.nodes = nodeConfig;
        initLogger();
        log.info("start");
    }
    
    /**
     * Méthode principale qui contient la boucle d'événéments se chargeant 
     * d'envoyer périodiquement un message et de vérifier si un message a
     * été reçu.
     * @throws FileFormatException 
     * @throws IOException 
     */
    public void run(){
        try (DatagramSocket socket = new DatagramSocket(port)){
            /* On configure le socket de telle sorte à ce qu'un appel bloquant
            le soit pendant 100ms, délai après lequel l'appel générera une 
            exception si elle n'a pas remplie sa fonction (e.g. attente de
            réception d'un datagramme. */
            socket.setSoTimeout(100);
            // Temps écoulé (en millisecondes) depuis le démarrage de la machine
            long time = System.currentTimeMillis();
            // Boucle d'événements répétée indéfiniment
            voisins = getVoisins(this.appId);
   
            while (true) {
                /* S'il s'est écoulé plus de 'BCAST_INTERVAL' millisecondes
                depuis le dernier envoi. */
                if (System.currentTimeMillis() > time + BCAST_INTERVAL) {
                    /* On sélectionne la prochaine application sur base de l'ID.
                    l'opération '%NUM_CLIENTS' de revenir à l'ID 0 lorsqu'on
                    séléctionne la destination pour la dernière application. */
                	//int destinationId = (appId+1)%NUM_CLIENTS;
                
                	int portSource = nodes.get(this.appId-1).port;
                	int portDestination = nodes.get(this.appId).port;
                	int idDestination = links.get(this.appId-1).sourceId;
                	int idSource = links.get(this.appId-1).destinationId;
                	int ttl = 114789;
         
                    //int destinationId = (appId+1)%NUM_CLIENTS;
                    //int destinationPort = BASE_PORT+destinationId;
                    String msg = String.format("Hello App#%d from App#%d", portDestination, this.port);

                    log.info(String.format("Sending message '%s' ",msg));                 
                    // Construction du buffer
                    ByteArrayOutputStream out = createBuffer(portSource,portDestination,idDestination,idSource,ttl,msg);
                    byte [] buffer = out.toByteArray();
                    InetAddress addr = InetAddress.getByName("localhost");
                    //Construction du Datagram
                    DatagramPacket rpacket = new DatagramPacket(buffer, buffer.length, addr, portSource);
                    socket.send(rpacket);
                    // On met à jour le temps de dernier envoi de paquet.
                    time = System.currentTimeMillis();
                }
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[MAX_DGRAM_SIZE], MAX_DGRAM_SIZE);
                    /* On regarde si un paquet arrive. L'appel est bloquant 
                    durant 100ms, au maximum. Si un datagramme arrive avant 
                    ce délai, il sera traité. Sinon, une exception sera
                    générée. */
                    
                    socket.receive(packet);
                    ByteArrayInputStream in = new ByteArrayInputStream(packet.getData());
                    int pS = readResultat(in);
                    int idDes = readResultat(in);
                    int idSo = readResultat(in);
                    int ttl = readResultat(in);
                    String msg = readMessage(in);
                    if(idDes != idSo) {
                    	for(int j=0; j < voisins.size(); j++) {
                        	int newIdDest = voisins.get(j);
                        	ttl = ttl-1;
                        	int newPortDest = nodes.get(newIdDest-1).port;
                        	ByteArrayOutputStream out = createBuffer(pS, newPortDest, newIdDest, idSo, ttl, msg);
                        	byte [] buffer = out.toByteArray();
                        	InetAddress addr = InetAddress.getByName("localhost");
                            //Construction du Datagram
                            DatagramPacket rpacket = new DatagramPacket(buffer, buffer.length, addr, newPortDest);
                            socket.send(rpacket);
                        }
                    }
                    
                    
                    
                    
                    // Faire une boucle pour envoyer aux voisins
                   
                    log.info(String.format("Received packet: %s", new String(packet.getData())));
                } catch (SocketTimeoutException e) {
                    // Aucun datagramme n'est arrivé durant les 100ms
                }
            }
        } catch (SocketException e) {
            System.out.println(e);
            throw new RuntimeException("Client closed due to exception", e);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Client closed due to exception", e);
        }
    }
    
    public int readResultat(ByteArrayInputStream in) throws IOException{
    	byte [] buffer = new byte[4];
    	in.read(buffer);
    	return Tools.bytesToInt(buffer);
    }
    
    public String readMessage(ByteArrayInputStream in) throws IOException{	
    	int n = in.available();
    	byte [] message = new byte[n];
    	in.read(message, 0, n);
		return new String(message, StandardCharsets.UTF_8);
	}
    
    public ArrayList<Integer> getVoisins(int id){
    	int sId, dId;
    	ArrayList<Integer> voisin = new ArrayList<Integer>();
    	for(Link lien : links) {
    		sId = lien.sourceId;
    		dId = lien.destinationId;
    		if(sId == id) {
    			voisin.add(dId);
    		}
    	}
    	
    	return voisin;
    }
    
    /**
     * 
     * On passe en paramètres un id Source, le Port de Destination, l'id de Destination, le TTL et le message a affiché
     * Cette fonction permet de construire le buffer qui sera envoyé
     * 
     */
    public ByteArrayOutputStream createBuffer(int portSource,int portDestination,int idDestination, int idSource, int ttl, String msg) throws IOException {
    	byte [] portS = (byte []) Tools.intToBytes(portSource);
    	byte [] portD = (byte []) Tools.intToBytes(portDestination);
        byte [] idDest = (byte []) Tools.intToBytes(idDestination);
        byte [] idSour = (byte []) Tools.intToBytes(idSource);
        byte [] ttlData = (byte []) Tools.intToBytes(ttl);
        //ajout d'un numero de sequence
       
       // On prépare le datagramme à envoyer.
       byte [] sbuf = msg.getBytes();
       ByteArrayOutputStream out = new ByteArrayOutputStream();
       out.write(portS);
       out.write(portD);
       out.write(idSour);
       out.write(idDest);
       out.write(ttlData);
       out.write(sbuf);
       return out;
    	
    }
    
    
    
    /**
     * Initialise l'objet qui sera utilisé pour afficher les événements liés à
     * cette application en particulier, en utilisant le format spécifié dans
     * l'énoncé.
     */
    private void initLogger(){
        log = Logger.getLogger("be.ac.umons.app"+appId);
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "%1$d;%2$d;%3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format, 
                        lr.getMillis(),
                        App.this.appId,
                        lr.getMessage()
                );
            }
        });
        log.addHandler(handler);
    }
}
