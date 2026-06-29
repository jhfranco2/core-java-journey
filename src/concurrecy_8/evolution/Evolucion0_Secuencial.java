package concurrecy_8.evolution;

/**
 * NIVEL 0: EJECUCIÓN SECUENCIAL
 * * Concepto: El hilo principal (main) hace todo el trabajo pesado, uno por uno.
 * Problema: Bloquea el sistema. Si hay 100 clientes, la aplicación web se congela
 * y da un "TimeOut" al usuario.
 * Tiempo esperado: 6 segundos (2s + 2s + 2s).
 */
public class Evolucion0_Secuencial {
    
    public static void main(String[] args) throws InterruptedException {
        long inicio = System.currentTimeMillis();
        System.out.println("🚀 Iniciando SIAR (Nivel 0) en hilo: " + Thread.currentThread().getName());

        String[] clientes = {"Cliente A", "Cliente B", "Cliente C"};

        // El hilo principal procesa iterativamente a cada cliente
        for (String cliente : clientes) {
            System.out.println("⏱️ Calculando riesgo para " + cliente + "...");
            
            // Simulamos la latencia de una consulta compleja a base de datos
            Thread.sleep(2000); 
            
            System.out.println("✅ Riesgo de " + cliente + " completado.");
        }

        long tiempoTotal = (System.currentTimeMillis() - inicio) / 1000;
        System.out.println("🏁 Tiempo total: " + tiempoTotal + " segundos. (Cuello de botella puro)");
    }
}