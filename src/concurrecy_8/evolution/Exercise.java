package concurrecy_8.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Exercise {

    public static void main(String[] args) throws Exception {
        generarPantallaPostPartida();
        generarPantallaPostPartidaMejorado();
    }

    public static void generarPantallaPostPartida() throws Exception {
        long inicio = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // Las 3 tareas a realizar
        Callable<Integer> calcularPL = () -> {
            Thread.sleep(2000);
            return 25;
        };
        Callable<Integer> calcularMaestria = () -> {
            Thread.sleep(2000);
            return 1200;
        };
        Callable<String> calcularCofre = () -> {
            Thread.sleep(2000);
            return "Cofre Hextech Obtenido";
        };

        System.out.println("Cargando estadísticas de la partida...");

        // Ejecución del Junior
        Integer puntosLiga = pool.submit(calcularPL).get();
        Integer maestria = pool.submit(calcularMaestria).get();
        String cofre = pool.submit(calcularCofre).get();

        System.out.println("Resultados: +" + puntosLiga + " PL, +" + maestria + " Puntos, " + cofre);
        System.out.println("Tiempo total: " + (System.currentTimeMillis() - inicio) / 1000 + " segundos.");
    }

    public static void generarPantallaPostPartidaMejorado() {
    long inicio = System.currentTimeMillis();
    ExecutorService pool = Executors.newFixedThreadPool(3);

    try {
        // 1. Las tareas (El manual de instrucciones)
        Callable<Integer> calcularPL = () -> { Thread.sleep(2000); return 25; };
        Callable<Integer> calcularMaestria = () -> { Thread.sleep(2000); return 1200; };
        Callable<String> calcularCofre = () -> { Thread.sleep(2000); return "Cofre Hextech Obtenido"; };

        System.out.println("Cargando estadísticas de la partida (Multihilo Real)...");

        // ==========================================
        // FASE 1: DESPLIEGUE (Enviar sin bloquear)
        // Guardamos los "radios de comunicación" (Futures)
        // ==========================================
        Future<Integer> futuroPL = pool.submit(calcularPL);
        Future<Integer> futuroMaestria = pool.submit(calcularMaestria);
        Future<String> futuroCofre = pool.submit(calcularCofre);

        // ==========================================
        // FASE 2: RECOLECCIÓN (La barrera)
        // Ahora sí llamamos al .get() y asignamos los valores a variables
        // ==========================================
        Integer puntosLiga = futuroPL.get();     // El main espera 2 segundos aquí
        Integer maestria   = futuroMaestria.get(); // Retorna al instante (ya pasaron los 2s)
        String cofre       = futuroCofre.get();    // Retorna al instante

        // 3. Mostramos la pantalla final
        System.out.println("✅ Resultados: +" + puntosLiga + " PL, +" + maestria + " Puntos, " + cofre);
        
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // ¡CRÍTICO! Siempre apagar la piscina en el finally para evitar fugas de RAM
        pool.shutdown(); 
    }

    System.out.println("🏁 Tiempo total: " + (System.currentTimeMillis() - inicio) / 1000 + " segundos.");
}
}
