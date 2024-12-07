package org.crypto_project.servers;

import org.crypto_project.utils.ParentServer;

import java.io.IOException;

public class ACQServer extends ParentServer {

    private static final int PORT = 9043; // Port défini pour l'ACQ
    private static final int ACS_PORT = 10043;

    public ACQServer(int port) throws IOException {
        super(port);
    }

    public static void main(String[] args) throws IOException {

        ACQServer acqServer = new ACQServer(PORT);

        try {

            // En écoute de données
            String message = acqServer.serverRead();
            acqServer.initClient(ACS_PORT);
            acqServer.clientSend(message);
            String ack = acqServer.clientRead();
            acqServer.serverSend(ack);

         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             acqServer.close();
         }
    }

}
