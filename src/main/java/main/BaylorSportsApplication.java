package main;

import server.BaylorSportsHttpServer;

/**
 * This is where my main method resides to run the server
 */
public class BaylorSportsApplication {

    public static void main(String[] args) {
        BaylorSportsHttpServer server = new BaylorSportsHttpServer(8088);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down Baylor Sports Registration Server...");
            server.stop();
        }));

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
