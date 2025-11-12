package com.gidoc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DownloadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la ruta real del archivo APK
        String contextPath = getServletContext().getRealPath("/");
        String apkPath = contextPath + "descargas/calculadora.apk";

        File apkFile = new File(apkPath);

        System.out.println("Buscando APK en: " + apkPath);
        System.out.println("Archivo existe: " + apkFile.exists());

        // Verificar si el archivo existe
        if (!apkFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Archivo calculadora.apk no encontrado. Colócalo en: /descargas/");
            return;
        }

        // Configurar headers para descarga
        response.setContentType("application/vnd.android.package-archive");
        response.setContentLengthLong(apkFile.length());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"calculadora.apk\"");

        // Streams para la descarga
        try (FileInputStream inputStream = new FileInputStream(apkFile);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            System.out.println("Descarga completada exitosamente");

        } catch (IOException e) {
            System.err.println("Error durante la descarga: " + e.getMessage());
            throw e;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}