package U1.controlador;

import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import U1.modelo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacturaControlador {
    private final VentanaEmpresa vista;

    public FacturaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    public void Flistar() {
        vista.tbl_FFacturas.setRowSorter(null);
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"ID", "Cliente ID", "Cliente", "Número", "Fecha", "Subtotal", "Impuesto", "Total"}, 0);
        vista.tbl_FFacturas.setModel(m);

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            for (Factura f : em.createQuery("FROM Factura", Factura.class).getResultList()) {
                Integer clienteId = f.getCliente() != null ? f.getCliente().getId() : null;
                String clienteNombre = f.getCliente() != null ? f.getCliente().getNombre() : "";
                m.addRow(new Object[]{
                        f.getId(), clienteId, clienteNombre,
                        f.getNumero(), f.getFecha(), f.getSubtotal(), f.getImpuesto(), f.getTotal()
                });
            }
        }
    }

    public void cargarCombos() {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            if (vista.cmb_FCliente != null) {
                vista.cmb_FCliente.removeAllItems();
                for (Cliente c : em.createQuery("FROM Cliente", Cliente.class).getResultList()) {
                    vista.cmb_FCliente.addItem(c.getNombre());
                }
            }
            if (vista.cmb_FProducto != null) {
                vista.cmb_FProducto.removeAllItems();
                for (Productos p : em.createQuery("FROM Productos", Productos.class).getResultList()) {
                    vista.cmb_FProducto.addItem(p.getNombre());
                }
            }
        }
    }

    public void Fguardar() {
        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Factura f = leerFactura(em, new Factura());
            em.persist(f);
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Factura guardada con éxito");
            Flistar();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar la factura: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Factualizar() {
        int filaVista = vista.tbl_FFacturas.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_FFacturas.convertRowIndexToModel(filaVista) : -1;
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una factura");
            return;
        }

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            int id = ((Number) vista.tbl_FFacturas.getValueAt(fila, 0)).intValue();
            tx.begin();
            Factura f = em.find(Factura.class, id);
            if (f != null) {
                leerFactura(em, f);
            }
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Factura actualizada con éxito");
            Flistar();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar la factura: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Feliminar() {
        int filaVista = vista.tbl_FFacturas.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_FFacturas.convertRowIndexToModel(filaVista) : -1;
        if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una factura");
            return;
        }

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            int id = ((Number) vista.tbl_FFacturas.getValueAt(fila, 0)).intValue();
            tx.begin();
            Factura f = em.find(Factura.class, id);
            if (f != null) em.remove(f);
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Factura eliminada con éxito");
            Flistar();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar la factura: " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Flimpiar() {
        vista.txt_FNumero.setText("");
        vista.txt_FFecha.setText("");
        vista.txt_FSubTotal.setText("");
        vista.txt_FImpuesto.setText("");
        vista.txt_FTotal.setText("");
    }

    public List<Factura> FbuscarPorCliente(String cliente) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery(
                            "SELECT f FROM Factura f JOIN f.cliente c WHERE LOWER(c.nombre) LIKE LOWER(:cliente) ORDER BY f.id",
                            Factura.class)
                    .setParameter("cliente", "%" + cliente + "%")
                    .getResultList();
        }
    }

    public void FbuscarYMostrar(String texto) {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Cliente ID", "Cliente", "Número", "Fecha", "Subtotal", "Impuesto", "Total"}, 0);
        for (Factura f : FbuscarPorCliente(texto)) {
            Integer clienteId = f.getCliente() != null ? f.getCliente().getId() : null;
            modelo.addRow(new Object[]{
                    f.getId(), clienteId, f.getCliente() == null ? "" : f.getCliente().getNombre(),
                    f.getNumero(), f.getFecha(), f.getSubtotal(), f.getImpuesto(), f.getTotal()
            });
        }
        vista.tbl_FFacturas.setRowSorter(null);
        vista.tbl_FFacturas.setModel(modelo);
    }

    private Factura leerFactura(EntityManager em, Factura f) {
        f.setNumero(vista.txt_FNumero.getText().trim());
        f.setFecha(LocalDate.parse(vista.txt_FFecha.getText().trim()));
        f.setSubtotal(new BigDecimal(vista.txt_FSubTotal.getText().trim()));
        f.setImpuesto(new BigDecimal(vista.txt_FImpuesto.getText().trim()));
        f.setTotal(new BigDecimal(vista.txt_FTotal.getText().trim()));

        Object clienteSel = vista.cmb_FCliente.getSelectedItem();
        if (clienteSel != null) {
            String cliente = clienteSel.toString();
            Cliente cEncontrado = em.createQuery("FROM Cliente c WHERE c.nombre=:n", Cliente.class)
                    .setParameter("n", cliente)
                    .getResultStream().findFirst().orElse(null);
            f.setCliente(cEncontrado);
        }

        Object productoSel = vista.cmb_FProducto.getSelectedItem();
        if (productoSel != null) {
            String producto = productoSel.toString();
            Productos productoSeleccionado = em.createQuery("FROM Productos p WHERE p.nombre=:n", Productos.class)
                    .setParameter("n", producto)
                    .getResultStream().findFirst().orElse(null);
            f.getProductos().clear();
            if (productoSeleccionado != null) {
                f.getProductos().add(productoSeleccionado);
            }
        }
        return f;
    }
}