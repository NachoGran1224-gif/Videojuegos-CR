CREATE DATABASE IF NOT EXISTS videojuegos_cr;
USE videojuegos_cr;


CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen_url VARCHAR(300),
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON DELETE SET NULL
);


CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE
);


INSERT INTO categorias (nombre) VALUES 
('Acción'),
('Aventura'),
('RPG'),
('Deportes'),
('Estrategia');


INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url, id_categoria) VALUES 
('Call of Duty Modern Warfare', 'Juego de acción en primera persona con una campaña increible.', 10000.00, 20, 'cod.jpg', 1),
('The Legend of Zelda Ocarina of Time', 'juego de aventura en mundo abierto para Nintendo switch con muchas horas de juego.', 45000.00, 15, 'zelda.jpg', 2),
('FC 27', 'Juego deportivo de futbol que cuenta con muchos modos de juego y con muchas horas de diversión con compas.', 32000.00, 25, 'fc27.jpg', 3);


INSERT INTO clientes (nombre, correo, contrasena) VALUES 
('Ignacio', 'Ignacio@gmail.com', '12345');

SELECT * FROM categorias;
SELECT * FROM productos;
SELECT * FROM clientes;