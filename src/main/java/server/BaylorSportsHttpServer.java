package server;

import controller.DispatcherServlet;
import controller.PersonController;
import http.HttpRequest;
import http.HttpResponse;
import repository.PersonRepository;
import service.PersonService;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaylorSportsHttpServer {

    private final int port;
    private ServerSocket serverSocket;
    private DispatcherServlet dispatcherServlet;
    private ExecutorService threadPool;
    private volatile boolean running = false;

    public BaylorSportsHttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
        initializeApplication();
    }

    /**
     * Initializes the entire application stack:
     * Repository → Service → Controller → DispatcherServlet
     */
    private void initializeApplication() {

        PersonRepository personRepository = new PersonRepository();
        PersonService personService = new PersonService(personRepository);
        PersonController personController = new PersonController(personService);

        this.dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.registerController("person", personController);

        System.out.println("Application initialized with PersonController");
    }

    /**
     * Start the HTTP server
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("Baylor Sports Registration Server starting...");
        System.out.println("Listening on http://localhost:" + port);
        System.out.println("Visit http://localhost:" + port + "/person to access the application");
        System.out.println("Press Ctrl+C to stop the server");
        System.out.println();

        // Accept connections in a loop
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();

                threadPool.submit(() -> handleClientConnection(clientSocket));

            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Handle an individual client connection
     */
    private void handleClientConnection(Socket clientSocket) {
        String clientAddress = clientSocket.getRemoteSocketAddress().toString();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
             Socket socket = clientSocket) {

            HttpRequest request = parseHttpRequest(in);

            if (request != null) {
                System.out.println(request.getMethod() + " " + request.getPath() + " from " + clientAddress);

                HttpResponse response = dispatcherServlet.handleRequest(request);

                sendHttpResponse(out, response);

                System.out.println(response.getStatusCode() + " " + response.getStatusMessage() + " sent to " + clientAddress);
            } else {
                System.out.println("Invalid HTTP request from " + clientAddress);
                sendBadRequestResponse(out);
            }

        } catch (Exception e) {
            System.err.println("Error handling client " + clientAddress + ": " + e.getMessage());
        }
    }

    /**
     * Parse raw HTTP request into my own HttpRequest object (later parsed by URLParser and FormParser)
     */
    private HttpRequest parseHttpRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            return null;
        }

        System.out.println("Request line: " + requestLine);

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            return null;
        }

        String method = requestParts[0];
        String fullPath = requestParts[1];

        String path = fullPath;
        if (fullPath.contains("?")) {
            String[] pathParts = fullPath.split("\\?", 2);
            path = pathParts[0];
        }

        // This handles cases where the Http Post is actually a Delete or Put
        if ("POST".equals(method) && path.startsWith("/person/delete/")) {
            method = "DELETE";
        } else if ("POST".equals(method) && path.startsWith("/person/update/")) {
            method = "PUT";
        }

        HttpRequest request = new HttpRequest(method, path);

        String headerLine;
        int contentLength = 0;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
            String[] headerParts = headerLine.split(":", 2);
            if (headerParts.length == 2) {
                String headerName = headerParts[0].trim();
                String headerValue = headerParts[1].trim();
                request.addHeader(headerName, headerValue);

                if ("content-length".equalsIgnoreCase(headerName)) {
                    try {
                        contentLength = Integer.parseInt(headerValue);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid Content-Length header: " + headerValue);
                    }
                }
            }
        }

        // Read body if present (for POST requests)
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int bytesRead = in.read(bodyChars, totalRead, contentLength - totalRead);
                if (bytesRead == -1) {
                    break;
                }
                totalRead += bytesRead;
            }
            String body = new String(bodyChars, 0, totalRead);
            request.setBody(body);

            if (!body.isEmpty()) {
                System.out.println("Request body: " + body);
            }
        }

        return request;
    }


    // This is to convert my custom HttpResponse to send back to client
    private void sendHttpResponse(PrintWriter out, HttpResponse response) {
        String httpResponseString = response.toHttpString();
        out.print(httpResponseString);
        out.flush();
    }

    private void sendBadRequestResponse(PrintWriter out) {
        HttpResponse badResponse = new HttpResponse(400, "Bad Request");
        badResponse.setBody("<html><body><h1>400 Bad Request</h1><p>Invalid HTTP request</p></body></html>");
        sendHttpResponse(out, badResponse);
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            System.out.println("Server stopped gracefully");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

}
