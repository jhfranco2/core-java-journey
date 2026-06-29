package concurrecy_8.evolution;

/**
 * NIVEL 1: EL FALSO MULTIHILO (El error clásico del Junior)
 * * Concepto: Intentamos delegar la tarea a un nuevo obrero (Thread), pero usamos
 * el comando equivocado para darle la orden.
 * Problema: Usar .run() no enciende el nuevo hilo. Simplemente hace que el hilo 
 * principal tome las instrucciones y las ejecute él mismo.
 * Tiempo esperado: 6 segundos (No hubo ninguna mejora).
 */
public class Evolucion1_FalsoMultihilo {

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();
        System.out.println("🚀 Iniciando SIAR (Nivel 1) en hilo: " + Thread.currentThread().getName());

        String[] clientes = {"Cliente A", "Cliente B", "Cliente C"};

        for (String cliente : clientes) {
            // 1. Definimos el manual de instrucciones (El Qué)
            Runnable tarea = () -> {
                try {
                    // Si te fijas en la consola, este nombre seguirá siendo "main"
                    System.out.println("⏱️ Calculando " + cliente + " en hilo: " + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    System.out.println("✅ Riesgo de " + cliente + " completado.");
                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt();
                }
            };

            // 2. Contratamos al obrero (El Quién)
            Thread trabajador = new Thread(tarea);
            trabajador.setName("Hilo-SIAR-" + cliente);
            
            // ❌ 3. EL ERROR CRÍTICO: 
            // .run() ignora al obrero y obliga al hilo main a hacer la tarea.
            trabajador.run(); 
        }

        long tiempoTotal = (System.currentTimeMillis() - inicio) / 1000;
        System.out.println("🏁 Tiempo total: " + tiempoTotal + " segundos. (Sigue siendo secuencial)");
    }
}