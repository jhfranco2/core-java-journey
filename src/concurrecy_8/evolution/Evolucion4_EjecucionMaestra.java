package concurrecy_8.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * NIVEL 4: EJECUCIÓN MAESTRA (Verdadero Paralelismo)
 * * Concepto: Separamos estratégicamente el envío de las tareas de la recolección
 * de los resultados.
 * Ventaja: Disparamos todas las consultas a la vez. Los 3 segundos que tarda
 * cada consulta corren en paralelo en los servidores externos.
 * Tiempo esperado: 2 segundos (Todos los cálculos se hicieron simultáneamente).
 */
public class Evolucion4_EjecucionMaestra {

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();
        System.out.println("🚀 Iniciando SIAR (Nivel 4) en hilo: " + Thread.currentThread().getName());

        ExecutorService pool = Executors.newFixedThreadPool(3);
        String[] clientes = {"Cliente A", "Cliente B", "Cliente C"};
        
        // Esta lista funcionará como nuestro archivador de "promesas de entrega"
        List<Future<String>> promesas = new ArrayList<>();

        try {
            // ==========================================
            // FASE 1: DESPLIEGUE MASIVO (Sin bloqueos)
            // ==========================================
            for (String cliente : clientes) {
                Callable<String> tarea = () -> {
                    System.out.println("⏱️ Calculando " + cliente + " en hilo: " + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    return "Riesgo_Alto_" + cliente;
                };
                
                // ✅ Enviamos al pool y guardamos el recibo, dejando al main continuar instantáneamente
                promesas.add(pool.submit(tarea)); 
            }

            System.out.println("☕ Main: Las 3 tareas fueron delegadas. Main sigue trabajando en otras cosas...");

            // ==========================================
            // FASE 2: RECOLECCIÓN (El cobro de los recibos)
            // ==========================================
            for (Future<String> promesa : promesas) {
                // Aquí el main se detiene solo si la tarea aún no termina.
                // Como las 3 arrancaron al tiempo, cuando el main termine de esperar 
                // los 2 segundos de la primera, las otras dos ya estarán listas.
                String resultado = promesa.get(); 
                System.out.println("✅ Consolidado: " + resultado);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

        long tiempoTotal = (System.currentTimeMillis() - inicio) / 1000;
        System.out.println("🏁 Tiempo total: " + tiempoTotal + " segundos. (¡Código de nivel Senior!)");
    }
}