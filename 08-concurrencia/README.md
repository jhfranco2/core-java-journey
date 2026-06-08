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

## 8.2 Thread States

1. New
2. Runnable
3. Blocked
4. Waiting
5. Timed waiting