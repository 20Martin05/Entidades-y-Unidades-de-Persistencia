package U1.vista;

import U1.controlador.*;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Comparator;

public class VentanaEmpresa extends VentanaEmpresaUI {

    private final ProductosControlador Pcontrolador;
    private final CategoriaControlador Ccontrolador;
    private final FacturaControlador Fcontrolador;
    private final ProveedorControlador PRcontrolador;
    private final ClientesControlador CLcontrolador;

    private TableRowSorter<DefaultTableModel> Psorter;
    private TableRowSorter<DefaultTableModel> Fsorter;
    private TableRowSorter<DefaultTableModel> PRsorter;
    private TableRowSorter<DefaultTableModel> CLsorter;

    public VentanaEmpresa() {
        // Inicializa los controladores asociándolos a esta vista
        this.Pcontrolador = new ProductosControlador(this);
        this.Ccontrolador = new CategoriaControlador(this);
        this.Fcontrolador = new FacturaControlador(this);
        this.PRcontrolador = new ProveedorControlador(this);
        this.CLcontrolador = new ClientesControlador(this);

        // Configuración visual básica de la ventana
        setTitle("Gestión de Productos - Corporación Favorita");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Método general que orquesta todas las inicializaciones
        iniciar();
    }

    public void iniciar() {
        cargarCategorias();
        cargarEstados();
        configurarEventos();

        // Productos
        Pcontrolador.Plistar();
        Psorter = new TableRowSorter<>((DefaultTableModel) tbl_PProductos.getModel());
        Psorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_PProductos.setRowSorter(Psorter);

        // Categorías
        Ccontrolador.Clistar();

        // Facturas
        Fcontrolador.cargarCombos();
        Fcontrolador.Flistar();
        Fsorter = new TableRowSorter<>((DefaultTableModel) tbl_FFacturas.getModel());
        Fsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_FFacturas.setRowSorter(Fsorter);

        // Proveedores (Incluye listado y carga de su JComboBox)
        PRcontrolador.PRlistar();
        PRcontrolador.cargarComboCategorias();
        PRsorter = new TableRowSorter<>((DefaultTableModel) tbl_PRProveedores.getModel());
        PRsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_PRProveedores.setRowSorter(PRsorter);

        // Clientes (Incluye listado y carga de su JComboBox)
        CLcontrolador.CLlistar();
        CLcontrolador.cargarComboFacturas();
        CLsorter = new TableRowSorter<>((DefaultTableModel) tbl_CLClientes.getModel());
        CLsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_CLClientes.setRowSorter(CLsorter);

        // Listeners de selección de filas en tablas
        configurarEventosTablas();
    }

    private void cargarCategorias() {
        cmb_CNombre.removeAllItems();
        cmb_CNombre.addItem("LÁCTEOS");
        cmb_CNombre.addItem("CARNES");
        cmb_CNombre.addItem("FRUTAS");
        cmb_CNombre.addItem("VERDURAS");
        cmb_CNombre.addItem("GRANOS");
        cmb_CNombre.addItem("PANADERÍA");
        cmb_CNombre.addItem("BEBIDAS");
        cmb_CNombre.addItem("ENLATADOS");
        cmb_CNombre.addItem("SNACKS");
        cmb_PCategoria.removeAllItems();
        for (int i = 0; i < cmb_CNombre.getItemCount(); i++) {
            cmb_PCategoria.addItem(cmb_CNombre.getItemAt(i));
        }
    }

    private void cargarEstados() {
        cmb_CEstado.removeAllItems();
        cmb_CEstado.addItem("DISPONIBLE");
        cmb_CEstado.addItem("AGOTADO");
        cmb_CEstado.setSelectedIndex(0);
    }

    private void configurarEventos() {
        // PRODUCTOS
        btn_PGuardar.addActionListener(e -> Pcontrolador.Pguardar());
        btn_PActualizar.addActionListener(e -> Pcontrolador.Pactualizar());
        btn_PEliminar.addActionListener(e -> Pcontrolador.Peliminar());
        btn_PLimpiar.addActionListener(e -> Pcontrolador.Plimpiar());

        // CATEGORIA
        btn_CGuardar.addActionListener(e -> Ccontrolador.Cguardar());
        btn_CActualizar.addActionListener(e -> Ccontrolador.Cactualizar());
        btn_CEliminar.addActionListener(e -> Ccontrolador.Celiminar());
        btn_CLimpiar.addActionListener(e -> Ccontrolador.Climpiar());

        // FACTURA
        btn_FGuardar.addActionListener(e -> Fcontrolador.Fguardar());
        btn_FActualizar.addActionListener(e -> Fcontrolador.Factualizar());
        btn_FEliminar.addActionListener(e -> Fcontrolador.Feliminar());
        btn_FLimpiar.addActionListener(e -> Fcontrolador.Flimpiar());

        // PROVEEDOR
        btn_PRGuardar.addActionListener(e -> PRcontrolador.PRguardar());
        btn_PRActualizar.addActionListener(e -> PRcontrolador.PRactualizar());
        btn_PREliminar.addActionListener(e -> PRcontrolador.PReliminar());
        btn_PRLimpiar.addActionListener(e -> PRcontrolador.PRlimpiar());

        // CLIENTE
        btn_CLGuardar.addActionListener(e -> CLcontrolador.CLguardar());
        btn_CLActualizar.addActionListener(e -> CLcontrolador.CLactualizar());
        btn_CLEliminar.addActionListener(e -> CLcontrolador.CLeliminar());
        btn_CLLimpiar.addActionListener(e -> CLcontrolador.CLlimpiar());

        // BUSCADORES
        txt_PBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });

        txt_FBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarFacturas(); }
            public void removeUpdate(DocumentEvent e) { filtrarFacturas(); }
            public void changedUpdate(DocumentEvent e) { filtrarFacturas(); }
        });

        txt_PRBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProveedores(); }
            public void removeUpdate(DocumentEvent e) { filtrarProveedores(); }
            public void changedUpdate(DocumentEvent e) { filtrarProveedores(); }
        });

        txt_CLBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });
    }

    private void filtrarProductos() {
        String texto = txt_PBuscador.getText().trim();
        if (texto.isEmpty()) {
            Pcontrolador.Plistar();
            return;
        }
        Pcontrolador.PbuscarYMostrar(texto);
    }

    private void filtrarFacturas() {
        String texto = txt_FBuscador.getText().trim();
        if (texto.isEmpty()) {
            Fcontrolador.Flistar();
            return;
        }
        Fcontrolador.FbuscarYMostrar(texto);
    }

    private void filtrarProveedores() {
        String texto = txt_PRBuscador.getText().trim();
        if (texto.isEmpty()) {
            PRcontrolador.PRlistar();
            return;
        }
        PRcontrolador.PRbuscarYMostrar(texto);
    }

    private void filtrarClientes() {
        String texto = txt_CLBuscador.getText().trim();
        if (texto.isEmpty()) {
            CLcontrolador.CLlistar();
            return;
        }
        CLcontrolador.CLbuscarYMostrar(texto);
    }

    private void configurarEventosTablas() {
        tbl_PProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_PProductos.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_PProductos.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    txt_PNombre.setText(tbl_PProductos.getValueAt(fila, 1).toString());
                    txt_PPrecio.setText(tbl_PProductos.getValueAt(fila, 2).toString());
                    txt_PCantidad.setText(tbl_PProductos.getValueAt(fila, 3).toString());
                }
            }
        });

        tbl_CCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CCategorias.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_CCategorias.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    boolean estado = Boolean.parseBoolean(tbl_CCategorias.getValueAt(fila, 3).toString());
                    cmb_CEstado.setSelectedIndex(estado ? 0 : 1);
                    cmb_CNombre.setSelectedItem(tbl_CCategorias.getValueAt(fila, 1).toString());
                    txt_CDescripcion.setText(tbl_CCategorias.getValueAt(fila, 2).toString());
                }
            }
        });

        tbl_PRProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_PRProveedores.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_PRProveedores.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    txt_PRNombre.setText(tbl_PRProveedores.getValueAt(fila, 1) != null ? tbl_PRProveedores.getValueAt(fila, 1).toString() : "");
                    txt_PREmpresa.setText(tbl_PRProveedores.getValueAt(fila, 2) != null ? tbl_PRProveedores.getValueAt(fila, 2).toString() : "");
                    txt_PRTelefono.setText(tbl_PRProveedores.getValueAt(fila, 3) != null ? tbl_PRProveedores.getValueAt(fila, 3).toString() : "");
                    txt_PRCorreo.setText(tbl_PRProveedores.getValueAt(fila, 4) != null ? tbl_PRProveedores.getValueAt(fila, 4).toString() : "");
                    txt_PRDireccion.setText(tbl_PRProveedores.getValueAt(fila, 5) != null ? tbl_PRProveedores.getValueAt(fila, 5).toString() : "");
                }
            }
        });

        tbl_CLClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CLClientes.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_CLClientes.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    txt_CLNombre.setText(tbl_CLClientes.getValueAt(fila, 1) != null ? tbl_CLClientes.getValueAt(fila, 1).toString() : "");
                    txt_CLApellido.setText(tbl_CLClientes.getValueAt(fila, 2) != null ? tbl_CLClientes.getValueAt(fila, 2).toString() : "");
                    txt_CLCedula.setText(tbl_CLClientes.getValueAt(fila, 3) != null ? tbl_CLClientes.getValueAt(fila, 3).toString() : "");
                    txt_CLTelefono.setText(tbl_CLClientes.getValueAt(fila, 4) != null ? tbl_CLClientes.getValueAt(fila, 4).toString() : "");
                    txt_CLCorreo.setText(tbl_CLClientes.getValueAt(fila, 5) != null ? tbl_CLClientes.getValueAt(fila, 5).toString() : "");
                }
            }
        });
    }
}