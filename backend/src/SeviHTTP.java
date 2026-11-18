import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeviHTTP {

    private static final int PUERTO = 8080;
    private static final ProdDAO prodDAO = new ProdDAO();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);

        // Ruta de la API para productos (GET, POST, PUT, DELETE)
        server.createContext("/api/productos", SeviHTTP::manejarProductos);

        // Ruta para servir los archivos estáticos de la carpeta frontend/
        server.createContext("/", SeviHTTP::manejarArchivosEstaticos);

        server.setExecutor(null);
        server.start();
        System.out.println("Servidor de Videojuegos CR corriendo en: http://localhost:" + PUERTO);
    }

    private static void manejarProductos(HttpExchange exchange) throws IOException {
        // Cabeceras CORS por si abres el HTML desde Live Server en otro puerto
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String metodo = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(metodo)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(metodo)) {
                List<Prod> lista = prodDAO.listarProductos();
                String json = convertirListaAJson(lista);
                enviarRespuesta(exchange, 200, json, "application/json; charset=UTF-8");

            } else if ("POST".equalsIgnoreCase(metodo)) {
                String body = leerCuerpo(exchange.getRequestBody());
                Map<String, String> datos = parsearDatos(body);

                Prod nuevo = new Prod();
                nuevo.setNombre(datos.getOrDefault("nombre", ""));
                nuevo.setDescripcion(datos.getOrDefault("descripcion", ""));
                nuevo.setPrecio(Double.parseDouble(datos.getOrDefault("precio", "0")));
                nuevo.setStock(Integer.parseInt(datos.getOrDefault("stock", "0")));
                nuevo.setImagen(datos.getOrDefault("imagen", "default.jpg"));
                nuevo.setCategoriaId(Integer.parseInt(datos.getOrDefault("categoriaId", "1")));

                boolean ok = prodDAO.agregarProducto(nuevo);
                enviarRespuesta(exchange, ok ? 201 : 400, "{\"ok\":" + ok + "}", "application/json; charset=UTF-8");

            } else if ("PUT".equalsIgnoreCase(metodo)) {
                String body = leerCuerpo(exchange.getRequestBody());
                Map<String, String> datos = parsearDatos(body);

                Prod editado = new Prod();
                editado.setId(Integer.parseInt(datos.get("id")));
                editado.setNombre(datos.getOrDefault("nombre", ""));
                editado.setDescripcion(datos.getOrDefault("descripcion", ""));
                editado.setPrecio(Double.parseDouble(datos.getOrDefault("precio", "0")));
                editado.setStock(Integer.parseInt(datos.getOrDefault("stock", "0")));
                editado.setImagen(datos.getOrDefault("imagen", "default.jpg"));
                editado.setCategoriaId(Integer.parseInt(datos.getOrDefault("categoriaId", "1")));

                boolean ok = prodDAO.actualizarProducto(editado);
                enviarRespuesta(exchange, ok ? 200 : 400, "{\"ok\":" + ok + "}", "application/json; charset=UTF-8");

            } else if ("DELETE".equalsIgnoreCase(metodo)) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parsearDatos(query != null ? query : "");
                int id = Integer.parseInt(params.get("id"));

                boolean ok = prodDAO.eliminarProducto(id);
                enviarRespuesta(exchange, ok ? 200 : 400, "{\"ok\":" + ok + "}", "application/json; charset=UTF-8");

            } else {
                enviarRespuesta(exchange, 405, "{\"error\":\"Método no permitido\"}", "application/json; charset=UTF-8");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            enviarRespuesta(exchange, 500, "{\"error\":\"" + escaparJson(e.getMessage()) + "\"}", "application/json; charset=UTF-8");
        }
    }

    private static void manejarArchivosEstaticos(HttpExchange exchange) throws IOException {
        String rutaSolicitada = exchange.getRequestURI().getPath();
        if (rutaSolicitada.equals("/")) {
            rutaSolicitada = "/index.html";
        }

        File archivo = new File("frontend" + rutaSolicitada);
        if (archivo.exists() && !archivo.isDirectory()) {
            String tipoContenido = obtenerMimeType(archivo.getName());
            byte[] bytes = Files.readAllBytes(archivo.toPath());
            exchange.getResponseHeaders().add("Content-Type", tipoContenido);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            String noEncontrado = "404 - Archivo no encontrado: " + rutaSolicitada;
            enviarRespuesta(exchange, 404, noEncontrado, "text/plain; charset=UTF-8");
        }
    }

    // --- UTILIDADES PARA JSON Y PARÁMETROS SIN LIBRERÍAS EXTERNAS ---

    private static String convertirListaAJson(List<Prod> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Prod p = lista.get(i);
            sb.append("{")
              .append("\"id\":").append(p.getId()).append(",")
              .append("\"nombre\":\"").append(escaparJson(p.getNombre())).append("\",")
              .append("\"descripcion\":\"").append(escaparJson(p.getDescripcion())).append("\",")
              .append("\"precio\":").append(p.getPrecio()).append(",")
              .append("\"stock\":").append(p.getStock()).append(",")
              .append("\"imagen\":\"").append(escaparJson(p.getImagen())).append("\",")
              .append("\"categoriaId\":").append(p.getCategoriaId()).append(",")
              .append("\"categoriaNombre\":\"").append(escaparJson(p.getCategoriaNombre())).append("\"")
              .append("}");
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    private static String leerCuerpo(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    // Soporta tanto JSON simple {"clave":"valor"} como formato formulario clave=valor&clave2=valor2
    private static Map<String, String> parsearDatos(String cuerpo) {
        Map<String, String> mapa = new HashMap<>();
        if (cuerpo == null || cuerpo.isBlank()) return mapa;

        cuerpo = cuerpo.trim();
        if (cuerpo.startsWith("{") && cuerpo.endsWith("}")) {
            cuerpo = cuerpo.substring(1, cuerpo.length() - 1);
            String[] pares = cuerpo.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String par : pares) {
                String[] kv = par.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].trim().replaceAll("^\"|\"$", "");
                    String v = kv[1].trim().replaceAll("^\"|\"$", "");
                    mapa.put(k, v);
                }
            }
        } else {
            for (String par : cuerpo.split("&")) {
                String[] kv = par.split("=", 2);
                if (kv.length == 2) {
                    String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    mapa.put(k, v);
                }
            }
        }
        return mapa;
    }

    private static void enviarRespuesta(HttpExchange exchange, int codigo, String respuesta, String contentType) throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String obtenerMimeType(String nombreArchivo) {
        String lower = nombreArchivo.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}