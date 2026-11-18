import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdDAO {

    // 1. LISTAR TODOS LOS PRODUCTOS (con el nombre de su categoría)
    public List<Prod> listarProductos() throws SQLException {
        List<Prod> lista = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre, p.descripcion, p.precio, p.stock, p.imagen_url, " +
                     "p.id_categoria, c.nombre AS categoria_nombre " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "ORDER BY p.id_producto ASC";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prod prod = new Prod(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("id_categoria"),
                    rs.getString("categoria_nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("imagen_url"),
                    rs.getString("descripcion")
                );
                lista.add(prod);
            }
        }
        return lista;
    }

    // 2. AGREGAR UN NUEVO VIDEOJUEGO
    public boolean agregarProducto(Prod p) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url, id_categoria) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImagen());
            ps.setInt(6, p.getCategoriaId());

            return ps.executeUpdate() > 0;
        }
    }

    // 3. ACTUALIZAR UN VIDEOJUEGO EXISTENTE
    public boolean actualizarProducto(Prod p) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, " +
                     "imagen_url = ?, id_categoria = ? WHERE id_producto = ?";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImagen());
            ps.setInt(6, p.getCategoriaId());
            ps.setInt(7, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // 4. ELIMINAR UN VIDEOJUEGO POR ID
    public boolean eliminarProducto(int idProducto) throws SQLException {
        String sql = "DELETE FROM productos WHERE id_producto = ?";

        try (Connection conn = conexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        }
    }
}