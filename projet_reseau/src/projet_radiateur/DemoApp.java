package projet_radiateur;

import java.io.IOException;
import java.util.ArrayList;
import java.net.UnknownHostException;

/**
 * Classe de démonstration qui a pour but d'instancier plusieurs applications 
 * sur des fils d'exécution différents.
 */
public class DemoApp{
    public static void main(String[] args){
        // Pour chaque application à instancier
        for(int i=0;i<App.NUM_CLIENTS; i++){
            int appId = i;
            // On crée un fil d'exécution qui va gérer la vie d'une application
            Thread thread = new Thread(){
                public void run(){
                    try{
                        App app = new App(appId);
                        app.run();
                        System.out.println("Thread Running");
                    }
                    catch(UnknownHostException e){
                        System.out.println("Erreur lors de la création de l'application.");
                    }
                }
            };
            // On démarre le fil d'exécution
            thread.start();
        }
    }
}
