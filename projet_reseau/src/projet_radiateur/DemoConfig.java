package projet_radiateur;

import java.util.ArrayList;
import java.io.IOException;

/**
 * Classe de démonstration qui met en évidence la lecture d'un fichier de 
 * configuration.
 */
public class DemoConfig{
    private static void printUsage(){
        System.out.println("Usage: java DemoConfig CONFIG_PATH");
    }
    public static void main(String[] args){
        /*if(args.length != 1){
            printUsage();
            System.exit(1);
        }*/
        try{
            Config config = new Config("scenario1.config");

            System.out.println("Nodes:");
            for(Config.Node node : config.getNodes()){
                System.out.println(node);
            }
            
            System.out.println("\nLinks:");
            for(Config.Link link : config.getLinks()){
                System.out.println(link);
            }
            
            System.out.println("\nEvents:");
            for(Config.Event event : config.getEvents()){
                System.out.println(event);
            }

        }
        catch(Config.FileFormatException e){
            System.out.println("Le fichier de configuration n'est pas valide");
        }
        catch(IOException e){
            System.out.println("Erreur lors de la lecture du fichier");
        }
    }
}
