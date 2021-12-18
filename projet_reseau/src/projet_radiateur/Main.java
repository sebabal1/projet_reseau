package projet_radiateur;

import java.util.ArrayList;
import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
	private static void printUsage(){
		System.out.println("Usage: java DemoConfig CONFIG_PATH");
	}
	public static void main(String[] args){
		if(args.length != 1){
			printUsage();
			System.exit(1);
		}
		try{
			Config config = new Config(args[0]);

			int nbNodes = config.getNodes().size();

			// Pour chaque application à instancier

			for(int i=0;i<nbNodes; i++){
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
		catch(Config.FileFormatException e){
			System.out.println("Le fichier de configuration n'est pas valide");
		}
		catch(IOException e){
			System.out.println("Erreur lors de la lecture du fichier");
		}
	}
}
