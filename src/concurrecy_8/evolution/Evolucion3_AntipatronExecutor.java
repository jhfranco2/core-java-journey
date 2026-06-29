package concurrecy_8.evolution;

import java.util.concurrent.*;

/**
 * NIVEL 3: EL ANTIPATRÓN DEL EXECUTOR (Cuello de botella por .get)
 * * Concepto: Usamos un Pool de hilos y Callable para poder recuperar el resultado.
 * Problema: Por mala lógica, exigimos el resultado (.get) en el mismo instante en 
 * que mandamos a hacer la tarea (.submit). Esto paraliza al hilo principal
 * y destruye completamente la ventaja de tener múltiples hilos.
 * Tiempo esperado: 6 segundos (Destruimos el multihilo).
 */
public class Evolucion3_AntipatronExecutor {

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();
        System.out.println("🚀 Iniciando SIAR (Nivel 3) en hilo: " + Thread.currentThread().getName());

        // Creamos nuestra cuadrilla fija de 3 obreros
        ExecutorService pool = Executors.newFixedThreadPool(3);
        String[] clientes = {"Cliente A", "Cliente B", "Cliente C"};

        try {
            for (String cliente : clientes) {
                // Usamos Callable porque necesitamos que retorne un String
                Callable<String> tarea = () -> {
                    System.out.println("⏱️ Calculando " + cliente + " en hilo: " + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    return "Riesgo_Alto_" + cliente;
                };

                // 1. Delegamos la tarea
                Future<String> promesa = pool.submit(tarea);
                
                // ❌ 2. EL ERROR CRÍTICO: 
                // Al llamar .get() de inmediato, el hilo main se congela aquí 2 segundos.
                // El ciclo for no avanza, por lo que las otras tareas no se envían al pool.
                String resultado = promesa.get(); 
                
                System.out.println("✅ Obtenido: " + resultado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Siempre debemos apagar el pool para no dejar hilos zombis consumiendo RAM
            pool.shutdown();
        }

        long tiempoTotal = (System.currentTimeMillis() - inicio) / 1000;
        System.out.println("🏁 Tiempo total: " + tiempoTotal + " segundos. (Lento e ineficiente)");
    }
}