package projet_reseau;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public class ServerFin{
    public static void main(String[] args){
        try{
            ServerFin server = new ServerFin(4567);
            server.run();
        }
        catch(UnknownHostException e){
            System.out.println(e);
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    ServerSocket server;

    public ServerFin(int port) throws IOException, UnknownHostException{
        this.server = new ServerSocket(port); 
    }

    public void run() throws IOException{
        Socket client = server.accept();
        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();

        int number1 = this.readNumber(in);
        int number2 = this.readNumber(in);
        char operator = this.readOperator(in);

        System.out.println("Client Request : "+number1+" "+operator+" "+number2);

        int result = this.compute(number1, number2, operator);
        System.out.println(result);
        this.sendResponse(out, result);
    }

    public int readNumber(InputStream in) throws IOException{
        byte[] buffer = new byte[4];
        in.read(buffer);
        return Tools.bytesToInt(buffer);
    }

    public char readOperator(InputStream in) throws IOException{
        return (char) in.read();
    }

    public int compute(int number1, int number2, int operator){
        int result;
        switch(operator){
            case '+':
                result = number1+number2;
                break;
            case '-':
                result = number1-number2;
                break;
            case '*':
                result = number1*number2;
                break;
            case '/':
                result = number1/number2;
                break;
            default:
                System.out.println("Operateur non supporté ..");
                result =-1;
        }
        return result;
    }

    public void sendResponse(OutputStream out,int result) throws IOException{
        byte[] bytes = Tools.intToBytes(result);
        out.write(bytes);
    }
}