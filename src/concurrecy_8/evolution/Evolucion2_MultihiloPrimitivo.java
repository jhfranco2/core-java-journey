package concurrecy_8.evolution;

/**
 * NIVEL 2: MULTIHILO PRIMITIVO (Fuego y Olvido)
 * * Concepto: Corregimos el comando usando .start(). Ahora los hilos sí se crean
 * y corren en paralelo al hilo principal.
 * Ventaja: El cálculo se hace verdaderamente rápido.
 * Problema Arquitectónico: El hilo principal (main) termina antes que los trabajadores.
 * Como usamos Runnable (que devuelve void), no tenemos forma
 * de recolectar los puntajes de riesgo para mostrarlos al usuario.
 * Tiempo del main esperado: 0 segundos (Termina al instante, pierde el control).
 */
public class Evolucion2_MultihiloPrimitivo {

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();
        System.out.println("🚀 Iniciando SIAR (Nivel 2) en hilo: " + Thread.currentThread().getName());

        String[] clientes = {"Cliente A", "Cliente B", "Cliente C"};

        for (String cliente : clientes) {
            Runnable tarea = () -> {
                try {
                    System.out.println("⏱️ Calculando " + cliente + " en hilo: " + Thread.currentThread().getName());
                    Thread.sleep(2000); // Trabajo en paralelo
                    System.out.println("✅ Riesgo de " + cliente + " completado.");
                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt();
                }
            };

            Thread trabajador = new Thread(tarea);
            trabajador.setName("Hilo-SIAR-" + cliente);
            
            // ✅ CORRECCIÓN: .start() lanza al obrero a trabajar en paralelo.
            trabajador.start(); 
        }

        long tiempoTotal = (System.currentTimeMillis() - inicio) / 1000;
        // El hilo principal llega aquí inmediatamente, ¡pero los resultados aún no existen!
        System.out.println("🏁 Tiempo total del MAIN: " + tiempoTotal + " segundos. (Perdimos control de la respuesta)");
    }
}