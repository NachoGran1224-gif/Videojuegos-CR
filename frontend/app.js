const API_URL = 'http://localhost:8080/api/productos';

// Cargamos el carrito guardado en el navegador
let carrito = JSON.parse(localStorage.getItem('carrito_videojuegos')) || [];
let productosCache = [];

// Inicialización al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    actualizarContador();
    cargarProductos();
});

// CARRITO (localStorage)
function agregarAlCarrito(nombre, precio) {
    carrito.push({ nombre, precio });
    localStorage.setItem('carrito_videojuegos', JSON.stringify(carrito));
    actualizarContador();
    alert(`"${nombre}" se ha agregado al carrito exitosamente.`);
}

function actualizarContador() {
    const contador = document.getElementById('contador-carrito');
    if (contador) {
        contador.textContent = `Carrito (${carrito.length})`;
    }
}

// Consultar productos (GET)
async function cargarProductos() {
    try {
        const respuesta = await fetch(API_URL);
        if (!respuesta.ok) throw new Error('Error al consultar la API');

        productosCache = await respuesta.json();

        // Si estamos en index.html
        if (document.getElementById('contenedor-productos')) {
            renderizarCatalogo(productosCache);
        }

        // Si estamos en admin.html
        if (document.getElementById('tabla-productos')) {
            renderizarTablaAdmin(productosCache);
        }
    } catch (error) {
        console.error('Error cargando productos:', error);
    }
}

// Renderizado catalogo (index.html)
function renderizarCatalogo(lista) {
    const contenedor = document.getElementById('contenedor-productos');
    if (!contenedor) return;

    contenedor.innerHTML = '';

    if (lista.length === 0) {
        contenedor.innerHTML = '<p>No hay videojuegos disponibles en este momento.</p>';
        return;
    }

    lista.forEach(juego => {
        const precioFormateado = Number(juego.precio).toLocaleString('es-CR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });

        const rutaImagen = juego.imagen.startsWith('http') || juego.imagen.startsWith('imagenes/')
            ? juego.imagen
            : `imagenes/${juego.imagen}`;

        const card = document.createElement('div');
        card.className = 'card';
        card.setAttribute('data-categoria', juego.categoriaId);

        card.innerHTML = `
            <img src="${rutaImagen}" alt="${juego.nombre}" onerror="this.src='imagenes/cod.jpg'">
            <h3>${juego.nombre}</h3>
            <p class="descripcion">${juego.descripcion}</p>
            <p class="precio">₡${precioFormateado}</p>
            <p class="stock">Disponibles: ${juego.stock}</p>
            <button onclick="agregarAlCarrito('${juego.nombre.replace(/'/g, "\\'")}', ${juego.precio})">
                Agregar al Carrito
            </button>
        `;

        contenedor.appendChild(card);
    });
}

function filtrarPorCategoria() {
    const seleccion = document.getElementById('filtro-categoria').value;
    if (seleccion === 'todas') {
        renderizarCatalogo(productosCache);
    } else {
        const filtrados = productosCache.filter(j => String(j.categoriaId) === seleccion);
        renderizarCatalogo(filtrados);
    }
}

// Panel de administración (admin.html)
function renderizarTablaAdmin(lista) {
    const tabla = document.getElementById('tabla-productos');
    if (!tabla) return;

    tabla.innerHTML = '';

    lista.forEach(juego => {
        const precioFormateado = Number(juego.precio).toLocaleString('es-CR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${juego.id}</td>
            <td>${juego.nombre}</td>
            <td>${juego.categoriaNombre || 'Sin categoría'}</td>
            <td>₡${precioFormateado}</td>
            <td>${juego.stock}</td>
            <td>
                <button type="button" class="btn-editar" onclick="cargarEnFormulario(${juego.id})">Editar</button>
                <button type="button" class="btn-eliminar" onclick="eliminarProducto(${juego.id}, '${juego.nombre.replace(/'/g, "\\'")}')">Eliminar</button>
            </td>
        `;
        tabla.appendChild(fila);
    });
}

// CREAR (POST) O ACTUALIZAR (PUT)
async function guardarProductoAdmin(event) {
    event.preventDefault();

    const id = document.getElementById('producto-id').value;
    const datos = {
        nombre: document.getElementById('nombre').value.trim(),
        categoriaId: document.getElementById('categoria').value,
        precio: document.getElementById('precio').value,
        stock: document.getElementById('stock').value,
        imagen: document.getElementById('imagen').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim()
    };

    const esEdicion = id !== '';
    if (esEdicion) {
        datos.id = id;
    }

    try {
        const respuesta = await fetch(API_URL, {
            method: esEdicion ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.json();

        if (respuesta.ok && resultado.ok) {
            alert(esEdicion
                ? `"${datos.nombre}" actualizado en MySQL.`
                : `"${datos.nombre}" guardado en MySQL.`);
            cancelarEdicion();
            cargarProductos();
        } else {
            alert('No se pudo guardar el videojuego: ' + (resultado.error || 'Error desconocido'));
        }
    } catch (error) {
        console.error('Error al guardar:', error);
        alert('Error de conexión con el servidor.');
    }
}

function cargarEnFormulario(id) {
    const juego = productosCache.find(j => j.id === id);
    if (!juego) return;

    document.getElementById('producto-id').value = juego.id;
    document.getElementById('nombre').value = juego.nombre;
    document.getElementById('categoria').value = juego.categoriaId;
    document.getElementById('precio').value = juego.precio;
    document.getElementById('stock').value = juego.stock;
    document.getElementById('imagen').value = juego.imagen;
    document.getElementById('descripcion').value = juego.descripcion;

    document.getElementById('titulo-formulario').textContent = `Editando juego #${juego.id}`;
    document.getElementById('btn-guardar').textContent = 'Actualizar Videojuego';
    document.getElementById('btn-cancelar').style.display = 'inline-block';
}

function cancelarEdicion() {
    document.getElementById('form-producto').reset();
    document.getElementById('producto-id').value = '';
    document.getElementById('titulo-formulario').textContent = 'Agregar juego';
    document.getElementById('btn-guardar').textContent = 'Guardar Videojuego';
    document.getElementById('btn-cancelar').style.display = 'none';
}

// ELIMINAR (DELETE)
async function eliminarProducto(id, nombre) {
    if (!confirm(`¿Seguro que deseas eliminar "${nombre}" de la base de datos?`)) return;

    try {
        const respuesta = await fetch(`${API_URL}?id=${id}`, {
            method: 'DELETE'
        });

        const resultado = await respuesta.json();

        if (respuesta.ok && resultado.ok) {
            alert(`"${nombre}" eliminado perfectamente.`);
            cargarProductos();
        } else {
            alert('No se elimino el juego.');
        }
    } catch (error) {
        console.error('Error al eliminar:', error);
        alert('Error de conexión con el servidor.');
    }
}