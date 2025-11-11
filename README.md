# 🏢 Victor Guerrero - Elevator System (Java OOP)

Este proyecto modela dos tipos de elevadores con principios de **Programación Orientada a Objetos (OOP)**:  
- **Public Elevator** (limitado por keycard y peso máximo de 1 tonelada)  
- **Freight Elevator** (acceso libre y límite de 3 toneladas)

Incluye manejo de acceso, control de peso, alarmas, *logging* con **SLF4J + Logback**, y pruebas unitarias con **JUnit 5**.

---

## 📦 Requisitos previos

Asegúrate de tener instalado:
- ☕ **Java 17** o superior  
- 🧰 **Maven 3.8+**  
- 🧠 **IntelliJ IDEA** (opcional, para desarrollo)

Verifica con:
```bash
java -version
mvn -version
```

---

## 🚀 Compilar el proyecto

Compila el código fuente y valida dependencias:

```bash
mvn clean compile
```

---

## ▶️ Ejecutar el programa

Ejecuta la clase principal `org.victor.Demo`:

```bash
 mvn clean compile exec:java
```

O limpia, compila y ejecuta todo en un solo paso:

```bash
mvn clean compile exec:java -Dexec.mainClass=org.victor.Demo
```

---

## 🧪 Ejecutar las pruebas unitarias

Ejecuta todas las pruebas JUnit:

```bash
mvn test
```

Para ver el reporte completo en consola:

```bash
mvn test -q
mvn surefire-report:report
```

Los resultados estarán en:
```
target/surefire-reports/
target/site/surefire-report.html
```

---

## 🧾 Estructura del proyecto

```
elevator-system/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/org/victor/
│   │   ├── Elevator.java
│   │   ├── PublicElevator.java
│   │   ├── FreightElevator.java
│   │   └── Demo.java
│   └── test/java/org/victor/
│       └── ElevatorTest.java
└── target/
```

---

## 🧰 Dependencias clave

- **SLF4J + Logback** → manejo de logs  
- **JUnit 5** → pruebas unitarias  
- **Lombok** → anotaciones para reducir boilerplate  

---

## 🌈 Ejemplo de salida esperada

```
INFO  PublicElevator - Public Elevator initialized successfully.
INFO  PublicElevator - Moving to floor 50
WARN  PublicElevator - Access denied — keycard required.
ERROR PublicElevator - Weight limit exceeded! Elevator shutting down.
```

---

## 💡 Notas

- Los logs `WARN` se muestran en **amarillo** si usas Logback configurado con colores.  
- Los límites de peso se pueden ajustar en el constructor.  
- Usa `mvn package` si quieres generar un `.jar` ejecutable.
