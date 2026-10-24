package U1;

import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import javax.swing.SwingUtilities;

// Punto de entrada de la aplicación de gestión de productos.
public class Main {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionJPA::cerrar));
        // Swing crea la ventana en el hilo de eventos para mantener la interfaz estable.
        SwingUtilities.invokeLater(() -> new VentanaEmpresa().setVisible(true));
    }
}
