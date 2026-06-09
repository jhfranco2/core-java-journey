# 🧵 Capítulo 8: Concurrencia

## 8.0 Conceptos clave
* **Multitasking:** Es la capacidad del SO para ejecutar varios programas (procesos) funcionando al mismo tiempo.
* **Multithreaded:** Es llevar el concepto de multitasking a un nivel más bajo, dándole la capacidad al programa para ejecutar varias tareas al tiempo. Cada tarea se ejecuta en un hilo de control (*thread*). Los programas que pueden ejecutar más de un hilo a la vez se denominan multihilo.

### ⚙️ Procesos vs. Hilos (El manejo de datos)
* **Procesos:** Tienen sus propias variables y memoria aislada.
* **Hilos:** Comparten los mismos datos y memoria dentro del proceso.

> ⚠️ **El Peligro:** Como los hilos comparten la misma memoria, modificar los mismos datos al mismo tiempo es muy riesgoso y puede corromper la información. Controlar este riesgo es la clave de la concurrencia.

---

## 8.1 Ejecución de Hilos (Running Threads)

Para crear y ejecutar hilos en Java de forma moderna, separamos la tarea del hilo que la ejecuta.

**1. Crear la tarea (Implementando Runnable):**
```java
Runnable r = () -> { 
    try {
        // Código de la tarea
        Thread.sleep(1000); // Pausa el hilo por 1 segundo
    } catch (InterruptedException e) {
        // Obligatorio manejar esta excepción si usamos sleep()
    }
};
```

**2. Crear el hilo pasándole la tarea:**
```java
Thread t = new Thread(r);
```

**3. Ejecutar la tarea (¡Iniciar el hilo!):**
```java
t.start();
```

> ⚠️ **Precaución (El Gotcha de .run):**
> No llames al método `.run()` directamente. Si lo haces, el código se ejecutará de forma secuencial en el mismo hilo actual, perdiendo la capacidad multihilo. Siempre usa `.start()` para levantar un hilo nuevo.

> 💡 **Nota de Arquitectura:**
> Es posible crear hilos usando `class MiHilo extends Thread`. **Ya no se recomienda**. Java no permite herencia múltiple, y extender de `Thread` acopla la tarea al mecanismo de ejecución. Usar `Runnable` nos permite pasarle la tarea a frameworks o *Thread Pools* más adelante.

---

## 8.2 Estados de un Hilo (Thread States)

Un hilo en Java tiene un ciclo de vida compuesto por **6 estados** (obtenibles mediante `t.getState()`).

### 🔄 El Ciclo de Vida y sus Disparadores (El Cómo):

1. 🌱 **New (Nuevo):** El objeto fue creado (`new Thread(r)`), pero aún no se ha llamado a `.start()`.
2. 🏃 **Runnable (Ejecutable):** Se llamó a `.start()`. **Ojo:** No garantiza que el código se esté ejecutando en ese milisegundo; significa que está listo y esperando que el *Thread Scheduler* le asigne un fragmento de tiempo de CPU.
3. 🛑 **Blocked (Bloqueado):** El hilo intentó acceder a un recurso bloqueado por otro hilo (ej. un *lock*). Se queda inactivo hasta que el recurso se libere.
4. ⏳ **Waiting (Esperando):** El hilo se pausa indefinidamente esperando una señal de otro hilo. 
   * *Se activa con:* `Object.wait()` o `Thread.join()`.
5. ⏱️ **Timed Waiting (Espera temporizada):** Espera a otro hilo, pero con un límite de tiempo máximo. 
   * *Se activa con:* `Thread.sleep(1000)`, `Object.wait(1000)` o `Thread.join(1000)`.
6. 💀 **Terminated (Terminado):** El método `run()` terminó exitosamente o murió por una excepción no capturada.

---

### 🧠 Conceptos Avanzados de Planificación (Java 21+)
¿Cómo decide la máquina a qué hilo darle la CPU?
* **Hilos de Plataforma (Tradicionales):** Usan planificación *Preventiva* (*Preemptive*). El Sistema Operativo les asigna un tiempo máximo y los interrumpe forzosamente cuando se acaba.
* **Hilos Virtuales:** Usan planificación *Cooperativa*. El hilo no es interrumpido por el SO; él mismo cede el control cuando se bloquea o llama explícitamente a `Thread.yield()`.

> ⚠️ **El Peligro de "Matar" Hilos:**
> Nunca uses los métodos obsoletos `.stop()`, `.suspend()` o `.resume()`. Matar un hilo de forma abrupta corrompe los datos compartidos. La forma segura de detener un hilo es de manera cooperativa, pidiéndole que termine (usualmente mediante el manejo de `InterruptedException`).

## 8.3 Propiedades de los Hilos

### 8.3.1 Hilos de Plataforma vs. Virtuales (Java 21+)
* **Hilos de Plataforma:** Son los tradicionales del SO. Son muy **pesados** (consumen mucha RAM y CPU al crearse). Si una app web recibe miles de peticiones, el servidor colapsa si asigna un hilo por petición.
* **Hilos Virtuales:** Son gestionados por la JVM, no por el SO. Son extremadamente **ligeros**. Puedes tener millones de ellos montados sobre unos pocos hilos de plataforma (llamados *carrier threads*).
  * **El Por Qué:** Permiten escribir código bloqueante tradicional (fácil de leer) con el rendimiento de la programación asíncrona (evitando el infierno de los *callbacks*).
  * **El Cómo:** `Thread.startVirtualThread(r);`

### 8.3.2 Interrupción de Hilos (Pidiendo que se detengan)
En Java, **no puedes forzar o "matar" a un hilo**. Debes *solicitarle* amablemente que se detenga usando `t.interrupt()`.

**¿Qué hace internamente `interrupt()`?**
1. Si el hilo está ejecutando código normal, simplemente cambia una bandera booleana interna a `true`.
2. Si el hilo está bloqueado o durmiendo (ej. `Thread.sleep()`), lo despierta bruscamente y lanza una `InterruptedException` (limpiando la bandera booleana).

**El Cómo (Diseñando tareas interrumpibles):**
Si tu hilo hace un trabajo largo, debe estar diseñado para escuchar interrupciones:
```java
Runnable r = () -> {
    try {
        // Verificar constantemente si alguien pidió detenerse
        while (!Thread.currentThread().isInterrupted() && masTrabajoPorHacer) {
            hacerTrabajo(); // Lógica pesada
            Thread.sleep(1000); // Esto lanzará excepción si lo interrumpen
        }
    } catch (InterruptedException e) {
        // El hilo fue interrumpido mientras dormía. Terminar de forma segura.
    } finally {
        // Liberar recursos, cerrar bases de datos, etc.
    }
};
```

---

### 8.3.3 Hilos Demonio (Daemon Threads)

Un hilo demonio es un hilo de servicio en segundo plano que existe únicamente para asistir a otros hilos (ej. limpiar caché, recolector de basura, temporizadores). 

* **El Por Qué:** La Máquina Virtual de Java (JVM) se apaga automáticamente cuando los únicos hilos que quedan ejecutándose son demonios. Sirven para tareas infinitas que no deberían impedir que el programa termine.

* **El Cómo:**
```java
Thread t = new Thread(tarea);
t.setDaemon(true); // Lo convierte en Demonio
t.start();
```

> ⚠️ **Peligros y Gotchas:**
> 1. **Orden estricto:** El método `setDaemon(true)` **debe** llamarse antes de `start()`. Si lo llamas después, lanzará una `IllegalThreadStateException`.
> 2. **Peligro de datos:** Como la JVM mata a los demonios abruptamente al apagarse, **nunca** uses hilos demonio para operaciones de entrada/salida (I/O) como escribir en una base de datos o en un archivo. Podrían dejar la información corrupta.
> 3. **Hilos Virtuales:** En Java 21+, *todos* los hilos virtuales son demonios por naturaleza. Intentar hacer `setDaemon(false)` en ellos no tiene ningún efecto.

---

### 8.3.4 Nombres e IDs de Hilos

A los hilos se les puede asignar un nombre legible para facilitar la depuración (*debugging*) y leer mejor los registros (*logs*) o los *thread dumps* cuando la aplicación falla.

* **El Cómo:**
```java
Thread t = new Thread(tarea);
t.setName("Limpiador-Cache-1"); // Asigna un nombre humano
long id = t.threadId(); // Obtiene el ID numérico único del hilo
```