package com.gidoc;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Servir archivos estáticos (HTML, CSS, etc.)
        server.createContext("/", new StaticFileHandler());

        // Servir la descarga del APK
        server.createContext("/download", new DownloadHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("✅ Servidor iniciado en: http://localhost:8080");
        System.out.println("📱 Página de descarga lista");
        System.out.println("📥 Ruta de descarga: http://localhost:8080/download");
        System.out.println("⏹️  Presiona Ctrl+C para detener el servidor");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("src/main/webapp" + path);
            if (file.exists() && !file.isDirectory()) {
                exchange.getResponseHeaders().set("Content-Type", getContentType(path));
                exchange.sendResponseHeaders(200, file.length());

                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                String response = "Archivo no encontrado";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            return "text/plain";
        }
    }

    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            File apkFile = new File("src/main/webapp/descargas/calculadora.apk");

            if (apkFile.exists()) {
                exchange.getResponseHeaders().set("Content-Type", "application/vnd.android.package-archive");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"calculadora.apk\"");
                exchange.sendResponseHeaders(200, apkFile.length());

                try (FileInputStream fis = new FileInputStream(apkFile);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("✅ Descarga completada exitosamente");
            } else {
                String response = "APK no encontrado. Coloca calculadora.apk en src/main/webapp/descargas/";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                System.err.println("❌ Error: " + response);
            }
            exchange.close();
        }
    }
}