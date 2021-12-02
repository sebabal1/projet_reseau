package projet_radiateur;


import java.net.*;
import java.io.IOException;

import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    /**
     * Constructeur de l'application
     * @param id entier représentant l'identifiant (unique) de l'application
     */
    public App(int id) throws UnknownHostException{
        this.appId = id;
        // On détermine le port d'écoute sur base de l'ID
        this.port = BASE_PORT+id;
        initLogger();
        log.info("start");
    }
    
    /**
     * Méthode principale qui contient la boucle d'événéments se chargeant 
     * d'envoyer périodiquement un message et de vérifier si un message a
     * été reçu.
     */
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)){
            /* On configure le socket de telle sorte à ce qu'un appel bloquant
            le soit pendant 100ms, délai après lequel l'appel générera une 
            exception si elle n'a pas remplie sa fonction (e.g. attente de
            réception d'un datagramme. */
            socket.setSoTimeout(100);
            // Temps écoulé (en millisecondes) depuis le démarrage de la machine
            long time = System.currentTimeMillis();
            // Boucle d'événements répétée indéfiniment
            while (true) {
                /* S'il s'est écoulé plus de 'BCAST_INTERVAL' millisecondes
                depuis le dernier envoi. */
                if (System.currentTimeMillis() > time + BCAST_INTERVAL) {
                    /* On sélectionne la prochaine application sur base de l'ID.
                    l'opération '%NUM_CLIENTS' de revenir à l'ID 0 lorsqu'on
                    séléctionne la destination pour la dernière application. */
                    int destinationId = (appId+1)%NUM_CLIENTS;
                    int destinationPort = BASE_PORT+destinationId;
                    String msg = String.format("Hello App#%d from App#%d", destinationId, appId);

                    log.info(String.format("Sending message '%s' ",msg));

                    // On prépare le datagramme à envoyer.
                    byte [] sbuf = msg.getBytes();
                    InetAddress addr = InetAddress.getByName("localhost");
                    DatagramPacket rpacket = new DatagramPacket(sbuf, sbuf.length, addr, destinationPort);
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
