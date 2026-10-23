package U1.gui;

import U1.entity.Productos;
import U1.gui.base.VentanaProductosUI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// Ventana Swing que permite consultar y administrar productos mediante JPA.
public class VentanaProductos extends VentanaProductosUI {

    // Recursos JPA utilizados para ejecutar consultas y transacciones.
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgres");
    EntityManager em = emf.createEntityManager();

    public VentanaProductos() {
        // Configuración inicial de la ventana y sus controles.
        setTitle("Gestión de Productos - Corporación Favorita");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        // Categorías disponibles para clasificar los productos.
        Categorias.addItem("LÁCTEOS");
        Categorias.addItem("CARNES");
        Categorias.addItem("FRUTAS");
        Categorias.addItem("VERDURAS");
        Categorias.addItem("GRANOS");
        Categorias.addItem("PANADERÍA");
        Categorias.addItem("BEBIDAS");
        Categorias.addItem("ENLATADOS");
        Categorias.addItem("SNACKS");

        // Carga los datos existentes al abrir la aplicación.
        listar();

        // Conecta cada botón con la operación CRUD correspondiente.
        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        Productos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Copia la fila seleccionada hacia los campos del formulario.
                int fila = Productos.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(Productos.getValueAt(fila, 1).toString());
                    txtPrecio.setText(Productos.getValueAt(fila, 2).toString());
                    txtCantidad.setText(Productos.getValueAt(fila, 3).toString());
                    Categorias.setSelectedItem(Productos.getValueAt(fila, 4).toString());
                }
            }
        });

        setLocationRelativeTo(null);
    }

    public void listar() {
        // Consulta todos los productos y actualiza el modelo de la tabla.
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Cantidad", "Categoria"}, 0);
        Productos.setModel(modelo);
        List<Productos> lista = em.createQuery("FROM Productos ", Productos.class).getResultList();
        for (Productos p : lista) {
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getCantidad(), p.getCategoria()});
        }
    }

    public void guardar() {
        // Lee el formulario, crea la entidad y la persiste en una transacción.
        String nombre = txtNombre.getText();
        double precio = Double.parseDouble(txtPrecio.getText());
        int cantidad = Integer.parseInt(txtCantidad.getText());
        String categoria = (String) Categorias.getSelectedItem();
        Productos nuevo = new Productos(0, nombre, precio, cantidad, categoria);
        em.getTransaction().begin();
        em.persist(nuevo);
        em.getTransaction().commit();
        JOptionPane.showMessageDialog(this, "Guardado con Exito");
        listar();
    }

    public void actualizar() {
        // Verifica la selección antes de modificar el producto elegido.
        int fila = Productos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla primero");
            return;
        }

        int id = (int) Productos.getValueAt(fila, 0);
        String nuevoNombre = txtNombre.getText();
        double nuevoprecio = Double.parseDouble(txtPrecio.getText());
        int nuevacantidad = Integer.parseInt(txtCantidad.getText());
        String nuevaCategoria = (String) Categorias.getSelectedItem();

        em.getTransaction().begin();
        Productos actualizar = em.find(Productos.class, id);
        if (actualizar != null) {
            actualizar.setNombre(nuevoNombre);
            actualizar.setPrecio(nuevoprecio);
            actualizar.setCantidad(nuevacantidad);
            actualizar.setCategoria(nuevaCategoria);
            em.merge(actualizar);
        }
        em.getTransaction().commit();
        JOptionPane.showMessageDialog(this, "Actualizado con Éxito");
        listar();
    }

    public void eliminar() {
        // Solicita confirmación y elimina el producto seleccionado.
        int fila = Productos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla primero");
            return;
        }

        int id = (int) Productos.getValueAt(fila, 0);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        em.getTransaction().begin();
        Productos eliminar = em.find(Productos.class, id);
        if (eliminar != null) {
            em.remove(eliminar);
        }
        em.getTransaction().commit();
        JOptionPane.showMessageDialog(this, "Eliminado con Éxito");
        listar();
    }

    public void limpiar() {
        // Restablece los campos editables del formulario.
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
    }
}
