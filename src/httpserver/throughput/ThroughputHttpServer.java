package httpserver.throughput;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ThroughputHttpServer {

    private static final String INPUT_FILE = "/home/vyom/IdeaProjects/multithreading/src"
            + "/resources/war_and_peace.txt";
    private static final int THREAD_POOL_SIZE = 4;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String s) throws IOException {
        HttpServer server =
                HttpServer.create(new InetSocketAddress(8400), 0);

        server.createContext("/search", new WordCountHandler(s));
        Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        server.setExecutor(executor);
        server.start();
    }

    public static class WordCountHandler implements HttpHandler {

        private final String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];
            if (!"word".equals(action)) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);

            byte[] response = Long.toString(count).getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        public long countWord(String word) {
            long count = 0;
            int index = 0;
            while (index >= 0) {
                index = text.indexOf(word, index);

                if (index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }

}
