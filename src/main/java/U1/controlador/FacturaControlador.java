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

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
                new String[]{"ID", "Cliente ID", "Cliente", "Número", "Fecha", "Subtotal", "Impuesto", "Total"}, 0);
    }

    private void llenar(DefaultTableModel m, List<Factura> facturas) {
        for (Factura f : facturas) {
            Integer clienteId = f.getCliente() != null ? f.getCliente().getId() : null;
            String clienteNombre = f.getCliente() != null ? f.getCliente().getNombre() : "";
            m.addRow(new Object[]{
                    f.getId(), clienteId, clienteNombre,
                    f.getNumero(), f.getFecha(), f.getSubtotal(), f.getImpuesto(), f.getTotal()
            });
        }
    }

    public void Flistar() {
        vista.tbl_FFacturas.setRowSorter(null);
        DefaultTableModel m = crearModelo();
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(m, em.createQuery("FROM Factura", Factura.class).getResultList());
        }
        vista.tbl_FFacturas.setModel(m);
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
            Factura f = new Factura();
            leerCabecera(em, f);
            em.persist(f);

            Productos p = productoSeleccionado(em);
            int cantidad = leerCantidad();
            descontarStock(p, cantidad);

            FacturaDetalle linea = crearDetalle(f, p, cantidad);
            f.getDetalles().add(linea);
            em.persist(linea);

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

    // Nota: la factura se trata como de una sola línea. Si necesitas varias,
    // hay que cambiar la lógica para actualizar o crear cada detalle.
    public void Factualizar() {
        int id = idSeleccionado();
        if (id == -1) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Factura f = em.find(Factura.class, id);
            if (f == null) throw new IllegalStateException("La factura ya no existe");

            FacturaDetalle detalle = f.getDetalles().isEmpty() ? null : f.getDetalles().get(0);
            if (detalle != null) {
                devolverStock(detalle.getProducto(), detalle.getCantidad());
            }

            leerCabecera(em, f);
            Productos p = productoSeleccionado(em);
            int cantidad = leerCantidad();
            descontarStock(p, cantidad);

            if (detalle == null) {
                FacturaDetalle linea = crearDetalle(f, p, cantidad);
                f.getDetalles().add(linea);
                em.persist(linea);
            } else {
                detalle.setProducto(p);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(BigDecimal.valueOf(p.getPrecio()));
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
        int id = idSeleccionado();
        if (id == -1) return;

        if (JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar esta factura? El stock se devolverá.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Factura f = em.find(Factura.class, id);
            if (f != null) {
                for (FacturaDetalle d : f.getDetalles()) {
                    devolverStock(d.getProducto(), d.getCantidad());
                }
                em.remove(f);
            }
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
        vista.txt_FCantidad.setText("");
    }

    public void Fcargar() {
        int filaVista = vista.tbl_FFacturas.getSelectedRow();
        if (filaVista < 0) return;
        int fila = vista.tbl_FFacturas.convertRowIndexToModel(filaVista);

        vista.txt_FNumero.setText(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 3)));
        vista.txt_FFecha.setText(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 4)));
        vista.txt_FSubTotal.setText(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 5)));
        vista.txt_FImpuesto.setText(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 6)));
        vista.txt_FTotal.setText(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 7)));
        vista.cmb_FCliente.setSelectedItem(String.valueOf(vista.tbl_FFacturas.getValueAt(fila, 2)));

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            Factura f = em.find(Factura.class, ((Number) vista.tbl_FFacturas.getValueAt(fila, 0)).intValue());
            if (f != null && !f.getDetalles().isEmpty()) {
                FacturaDetalle d = f.getDetalles().get(0);
                vista.cmb_FProducto.setSelectedItem(d.getProducto().getNombre());
                vista.txt_FCantidad.setText(String.valueOf(d.getCantidad()));
            }
        }
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
        vista.tbl_FFacturas.setRowSorter(null);
        DefaultTableModel modelo = crearModelo();
        llenar(modelo, FbuscarPorCliente(texto));
        vista.tbl_FFacturas.setModel(modelo);
    }

    private FacturaDetalle crearDetalle(Factura f, Productos p, int cantidad) {
        FacturaDetalle linea = new FacturaDetalle();
        linea.setFactura(f);
        linea.setProducto(p);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(BigDecimal.valueOf(p.getPrecio()));
        return linea;
    }

    private void leerCabecera(EntityManager em, Factura f) {
        f.setNumero(vista.txt_FNumero.getText().trim());
        f.setFecha(LocalDate.parse(vista.txt_FFecha.getText().trim()));
        f.setSubtotal(new BigDecimal(vista.txt_FSubTotal.getText().trim()));
        f.setImpuesto(new BigDecimal(vista.txt_FImpuesto.getText().trim()));
        f.setTotal(new BigDecimal(vista.txt_FTotal.getText().trim()));

        Object clienteSel = vista.cmb_FCliente.getSelectedItem();
        if (clienteSel == null) throw new IllegalStateException("Selecciona un cliente");
        Cliente c = em.createQuery("FROM Cliente c WHERE c.nombre = :n", Cliente.class)
                .setParameter("n", clienteSel.toString())
                .getResultStream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Cliente no encontrado"));
        f.setCliente(c);
    }

    private Productos productoSeleccionado(EntityManager em) {
        Object sel = vista.cmb_FProducto.getSelectedItem();
        if (sel == null) throw new IllegalStateException("Selecciona un producto");
        return em.createQuery("FROM Productos p WHERE p.nombre = :n", Productos.class)
                .setParameter("n", sel.toString())
                .getResultStream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Producto no encontrado"));
    }

    private int leerCantidad() {
        int cantidad;
        try {
            cantidad = Integer.parseInt(vista.txt_FCantidad.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("La cantidad debe ser un número entero");
        }
        if (cantidad <= 0) throw new IllegalStateException("La cantidad debe ser mayor que cero");
        return cantidad;
    }

    private void descontarStock(Productos p, int cantidad) {
        if (p.getCantidad() < cantidad) {
            throw new IllegalStateException("Stock insuficiente de " + p.getNombre()
                    + " (disponible: " + p.getCantidad() + ")");
        }
        p.setCantidad(p.getCantidad() - cantidad);
    }

    private void devolverStock(Productos p, int cantidad) {
        p.setCantidad(p.getCantidad() + cantidad);
    }

    private int idSeleccionado() {
        int filaVista = vista.tbl_FFacturas.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una factura");
            return -1;
        }
        return ((Number) vista.tbl_FFacturas.getValueAt(vista.tbl_FFacturas.convertRowIndexToModel(filaVista), 0)).intValue();
    }
}