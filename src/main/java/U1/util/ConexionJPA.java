package U1.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class ConexionJPA {
    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("postgres");

    private ConexionJPA() {
    }

    public static EntityManager crearEntityManager() {
        return FACTORY.createEntityManager();
    }

    public static void cerrar() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
