package projet_radiateur;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Config{

    private ParsingState currentState;
    private ArrayList<Node> nodes;
    private ArrayList<Link> links;
    private ArrayList<Event> events;

    public Config(String path) throws IOException, FileFormatException{
        currentState = ParsingState.NODES;

        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        events = new ArrayList<Event>();

        FileReader fr= new FileReader(path);
        BufferedReader br= new BufferedReader(fr);
        parseFile(br);
    }

    public ArrayList<Node> getNodes(){
        return nodes;
    }

    public ArrayList<Link> getLinks(){
        return links;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }

    private void parseFile(BufferedReader reader) throws IOException, FileFormatException {
        String line;
        while((line = reader.readLine()) != null){
            if(line.equals("")){
               currentState = currentState.nextState(); 
            }
            else{
                parseLine(line);

            }
        }
    }

    private void parseLine(String line) throws IOException, FileFormatException{
        switch(currentState){
            case NODES:
                String[] args = line.split(" ");
                if(args.length != 3){
                    throw new FileFormatException("Format for node definition must be 'NODEID ADDRESS PORT'");
                }
                int nodeId = Integer.parseInt(args[0]);
                String address =  args[1];
                int port = Integer.parseInt(args[2]);
                nodes.add(new Node(nodeId, address, port));
                break;
            case LINKS:
                args = line.split(" ");
                if(args.length < 2){
                    throw new FileFormatException("Format for link definition must be 'NODEID NEIGHBOR1 NEIGHBOR2 ..'");
                }
                nodeId = Integer.parseInt(args[0]);
                for(int i = 1; i < args.length; i++){
                    int neighborId = Integer.parseInt(args[i]);
                    links.add(new Link(nodeId,neighborId));
                }
                break;
            case EVENTS:
                args = line.split(" ");
                if(args.length < 3){
                    throw new FileFormatException("Format for event definition must contain at least 3 arguments");
                }
                nodeId = Integer.parseInt(args[0]);
                int delay = Integer.parseInt(args[1]);
                ArrayList<String> eventArgs = new ArrayList<String>();
                for(int i = 2; i < args.length; i++){
                    eventArgs.add(args[i]);
                }
                events.add(new Event(nodeId, delay, eventArgs));
                break;
            default:
                break;
        }
    }
    
    //
    // Internal classes
    //

    public class Node{
        public final int id;
        public final InetAddress address;
        public final int port;

        public Node(int id, String address, int port) throws UnknownHostException{
            this.id = id;
            this.address = InetAddress.getByName(address);
            this.port = port;
        }
        public String toString(){
            return String.format("[Node#%d | address:%s | port:%d]",id, address, port);
        }
    }

    public class Link{
        public final int sourceId, destinationId;

        public Link(int sourceId, int destinationId){
            this.sourceId = sourceId;
            this.destinationId = destinationId;
        }
        public String toString(){
            return String.format("[Node#%d is connected to Node#%d]",sourceId, destinationId);
        }
    }
    
    public class Event{
        public final int nodeId;
        public final int delay;
        public final ArrayList<String> args;

        public Event(int nodeId, int delay, ArrayList<String> args){
            this.nodeId = nodeId;
            this.delay = delay;
            this.args = args;
        }
        public String toString(){
            return String.format("[Event for Node#%d | delay:%dms | args:%s]",nodeId, delay, args);
        }
    }

    private enum ParsingState{
        NODES,
        LINKS,
        EVENTS;

        public ParsingState nextState(){
            switch(this){
                case NODES:
                    return ParsingState.LINKS;
                case LINKS:
                    return ParsingState.EVENTS;
            }
            return ParsingState.NODES;
        }
    }

    public class FileFormatException extends Exception{
        public FileFormatException(String text) {
            super(text);
        }
    }
}
