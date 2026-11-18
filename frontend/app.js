//Cargamos el carrito guardado en el navegador 
let carrito = JSON.parse(localStorage.getItem('carrito_videojuegos')) || [];

//Actualizacion del contador del menú apenas carga la página
actualizarContador();

//Función para cuando se hace clic en "Agregar al Carrito"
function agregarAlCarrito(nombre, precio) {
    carrito.push({ nombre, precio });
    
// Guardamos la lista en el historial del navegador
    localStorage.setItem('carrito_videojuegos', JSON.stringify(carrito));
    
    actualizarContador();
    alert(`"${nombre}" se a agregado al carrito exitosamente`);
}

//Función para cambiar el numero que sale a la par del carrito
function actualizarContador() {
    const contador = document.getElementById('contador-carrito');
    if (contador) {
        contador.textContent = `Carrito (${carrito.length})`;
    }
}

function filtrarPorCategoria() {
    const seleccion = document.getElementById('filtro-categoria').value;
    const tarjetas = document.querySelectorAll('.card');

    tarjetas.forEach(tarjeta => {
        const categoriaJuego = tarjeta.getAttribute('data-categoria');
        
        if (seleccion === 'todas' || seleccion === categoriaJuego) {
            tarjeta.style.display = 'flex';
        } else {
            tarjeta.style.display = 'none';
        }
    });
}

let contadorIdAdmin = 4;

function agregarProductoAdmin(event) {
    event.preventDefault(); 

    const nombre = document.getElementById('nombre').value;
    const selectCategoria = document.getElementById('categoria');
    const nombreCategoria = selectCategoria.options[selectCategoria.selectedIndex].text;
    const precio = parseFloat(document.getElementById('precio').value).toLocaleString('es-CR', { minimumFractionDigits: 2 });
    const stock = document.getElementById('stock').value;

    const tabla = document.getElementById('tabla-productos');
    const nuevaFila = document.createElement('tr');

    nuevaFila.innerHTML = `
        <td>${contadorIdAdmin++}</td>
        <td>${nombre}</td>
        <td>${nombreCategoria}</td>
        <td>₡${precio}</td>
        <td>${stock}</td>
        <td>
            <button class="btn-eliminar" onclick="eliminarFila(this)">Eliminar</button>
        </td>
    `;

    tabla.appendChild(nuevaFila);
    document.getElementById('form-producto').reset();
    alert(` "${nombre}" agregado al catalogo.`);
}

function eliminarFila(boton) {
    if (confirm('¿Seguro que deseas eliminar este juego del catalogo?')) {
        const fila = boton.closest('tr');
        fila.remove();
    }
}