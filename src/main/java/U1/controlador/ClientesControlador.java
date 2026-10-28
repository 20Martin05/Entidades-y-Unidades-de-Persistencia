package U1.controlador;

import U1.modelo.Cliente;
import U1.modelo.Factura;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ClientesControlador {
    private final VentanaEmpresa vista;
    private final EntityManager em = ConexionJPA.crearEntityManager();

    public ClientesControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : palabras) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    public void CLlistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Cédula", "Teléfono", "Correo"}, 0);
        vista.tbl_CLClientes.setRowSorter(null);
        vista.tbl_CLClientes.setModel(modelo);
        List<Cliente> lista = em.createQuery("FROM Cliente", Cliente.class).getResultList();
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellido(), c.getCedula(), c.getTelefono(), c.getCorreo()
            });
        }
    }

    public void cargarComboFacturas() {
        if (vista.cmb_PRFactura != null) {
            vista.cmb_PRFactura.removeAllItems();
            List<Factura> lista = em.createQuery("FROM Factura", Factura.class).getResultList();
            for (Factura f : lista) {
                vista.cmb_PRFactura.addItem(f.getNumero());
            }
        }
    }

    public void CLguardar() {
        try {
            String nombre = capitalizar(vista.txt_CLNombre.getText());
            String apellido = capitalizar(vista.txt_CLApellido.getText());
            String cedula = vista.txt_CLCedula.getText().trim();
            String telefono = vista.txt_CLTelefono.getText().trim();
            String correo = vista.txt_CLCorreo.getText().trim().toLowerCase();

            Cliente nuevo = new Cliente();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setCedula(cedula);
            nuevo.setTelefono(telefono);
            nuevo.setCorreo(correo);

            em.getTransaction().begin();
            em.persist(nuevo);
            em.getTransaction().commit();

            JOptionPane.showMessageDialog(vista, "Cliente guardado con éxito");
            CLlistar();
            cargarComboFacturas(); // Actualiza el combo por si se creó relación
        } catch (RuntimeException ex) {
            CLcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el cliente: " + ex.getMessage());
        }
    }

    public void CLactualizar() {
        int filaVista = vista.tbl_CLClientes.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_CLClientes.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un cliente de la tabla primero");
            return;
        }
        try {
            int id = ((Number) vista.tbl_CLClientes.getValueAt(fila, 0)).intValue();
            String nuevoNombre = capitalizar(vista.txt_CLNombre.getText());
            String nuevoApellido = capitalizar(vista.txt_CLApellido.getText());
            String nuevaCedula = vista.txt_CLCedula.getText().trim();
            String nuevoTelefono = vista.txt_CLTelefono.getText().trim();
            String nuevoCorreo = vista.txt_CLCorreo.getText().trim().toLowerCase();

            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                cliente.setNombre(nuevoNombre);
                cliente.setApellido(nuevoApellido);
                cliente.setCedula(nuevaCedula);
                cliente.setTelefono(nuevoTelefono);
                cliente.setCorreo(nuevoCorreo);
            }
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Cliente actualizado con éxito");
            CLlistar();
        } catch (RuntimeException ex) {
            CLcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el cliente: " + ex.getMessage());
        }
    }

    public void CLeliminar() {
        int filaVista = vista.tbl_CLClientes.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_CLClientes.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un cliente de la tabla primero");
            return;
        }

        int id = ((Number) vista.tbl_CLClientes.getValueAt(fila, 0)).intValue();

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar este cliente?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            em.getTransaction().begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) em.remove(cliente);
            em.getTransaction().commit();

            JOptionPane.showMessageDialog(vista, "Cliente eliminado con éxito");
            CLlistar();
            cargarComboFacturas();
        } catch (RuntimeException ex) {
            CLcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el cliente: " + ex.getMessage());
        }
    }

    public void CLlimpiar() {
        vista.txt_CLNombre.setText("");
        vista.txt_CLApellido.setText("");
        vista.txt_CLCedula.setText("");
        vista.txt_CLTelefono.setText("");
        vista.txt_CLCorreo.setText("");
    }

    public List<Cliente> CLbuscarPorNombre(String nombre) {
        return em.createQuery(
                        "SELECT c FROM Cliente c WHERE LOWER(c.nombre) LIKE LOWER(:nombre) ORDER BY c.nombre",
                        Cliente.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    public void CLbuscarYMostrar(String texto) {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Apellido", "Cédula", "Teléfono", "Correo"}, 0);
        List<Cliente> lista = CLbuscarPorNombre(texto);
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getApellido(), c.getCedula(), c.getTelefono(), c.getCorreo()
            });
        }
        vista.tbl_CLClientes.setModel(modelo);
    }

    private void CLcancelarTransaccionSiNecesario() {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
    }
}
