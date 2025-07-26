package main;

import server.BaylorSportsHttpServer;

public class BaylorSportsApplication {

    public static void main(String[] args) {
        // Create server on port 8088 (as required by assignment)
        BaylorSportsHttpServer server = new BaylorSportsHttpServer(8088);

        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔄 Shutting down Baylor Sports Registration Server...");
            server.stop();
        }));

        try {
            // Start the server (this blocks until server is stopped)
            server.start();
        } catch (Exception e) {
            System.err.println("💥 Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
