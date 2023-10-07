package it.fi.meucci;
import java.net.*;
import java.io.*;

public class ServerThread extends Thread{
    protected ServerSocket serverSocket;
    protected Socket clientSocket;
    protected String datiRicevuti;
    protected String rispostaEsito;
    protected int numCasuale;
    protected BufferedReader dataFromClient;
    protected DataOutputStream dataToClient;

    public ServerThread(Socket clientSocket){
        this.clientSocket = clientSocket;
        this.serverSocket = null;
        this.datiRicevuti = null;
        this.rispostaEsito = null;
        this.numCasuale = generaNum();
    }

    private int generaNum(){
        return (int)(Math.random() * 1000);
    }

    public void run(){
        try{
            this.comunica();
        }catch(Exception e){
            System.out.println("Errore nella comunicazione");
        }
    }

    protected void comunica() throws Exception{
        dataFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        dataToClient = new DataOutputStream(clientSocket.getOutputStream());
        
        for(;;){
            datiRicevuti = dataFromClient.readLine();
            try {
                int valoreRicevuto = Integer.parseInt(datiRicevuti);
                if (valoreRicevuto == this.numCasuale) rispostaEsito = "Hai indovinato";
                else if (valoreRicevuto > this.numCasuale) rispostaEsito = "Il numero da invovinare è più basso";
                else rispostaEsito = "Il numero da invovinare è più alto";
            } catch (NumberFormatException e) {
                rispostaEsito = "Non ho ricevuto un numero corretto";
            }
            dataToClient.writeBytes(rispostaEsito+"\n");
            if (rispostaEsito == "Hai indovinato"){
                break;
            }
        }
        clientSocket.close();
    }

    public static class MultiServer{
        public void avvioServer(){
            try {
                ServerSocket serverSckt = new ServerSocket(6789);
                for (;;){
                    System.out.println("Server in attesa...");
                    Socket socket = serverSckt.accept();
                    ServerThread serverMultiThread = new ServerThread(socket);
                    serverMultiThread.start();
                }
            } catch (Exception e) {
                System.out.println("Errore durante istanza del server");
            }
        }
    }

    public static void main( String[] args )
    {
        MultiServer tcpServer = new ServerThread.MultiServer();
        tcpServer.avvioServer();
    }
}
