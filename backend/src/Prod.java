public class Prod {
    private int id;
    private String nombre;
    private int categoriaId;
    private String categoriaNombre;
    private double precio;
    private int stock;
    private String imagen;
    private String descripcion;

    public Prod() {}

    public Prod(int id, String nombre, int categoriaId, String categoriaNombre, double precio, int stock, String imagen, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}