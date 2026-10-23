package U1;

import U1.gui.VentanaProductos;
import javax.swing.SwingUtilities;

// Punto de entrada de la aplicación de gestión de productos.
public class Main {
    public static void main(String[] args) {
        // Swing crea la ventana en el hilo de eventos para mantener la interfaz estable.
        SwingUtilities.invokeLater(() -> new VentanaProductos().setVisible(true));
    }
}