import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionDB {
    // Ajusta el nombre de la BD, usuario y contraseña si en tu MySQL son distintos
    private static final String URL = "jdbc:mysql://localhost:3306/videojuegos_cr?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Nachito1224"; 

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL en backend/lib", e);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = conectar()) {
            if (conn != null) {
                System.out.println("Se hizo una conexion exitosa a MySQL (videojuegos_cr)");
            }
        } catch (SQLException e) {
            System.err.println("Error al tratar de conectarse con MySQL: " + e.getMessage());
        }
    }
}