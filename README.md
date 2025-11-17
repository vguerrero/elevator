# Victor Guerrero - Advanced Elevator System (Java)

## 📖 Descripción

Sistema avanzado de gestión de elevadores con **seguridad integrada**, **concurrencia thread-safe**, **auditoría de eventos** y **monitoreo en tiempo real**. Implementa dos tipos de elevadores con control de acceso, gestión de peso, alarmas inteligentes y manejo robusto de errores.

### Tipos de Elevadores:
- **Public Elevator** → Limitado por keycard, peso máximo: 1 tonelada
- **Freight Elevator** → Acceso libre, peso máximo: 3 toneladas

---

## Características Principales

### **Seguridad Avanzada**
- ✅ Autenticación de usuarios con contraseñas encriptadas (BCrypt)
- ✅ Sistema de roles (ADMIN, EMPLOYEE, MAINTENANCE)
- ✅ Gestión de keycards con validación de acceso
- ✅ Bloqueo de cuenta automático después de 5 intentos fallidos
- ✅ Control de acceso a pisos restringidos

###  **Concurrencia Thread-Safe**
- ✅ Locks de lectura/escritura (ReentrantReadWriteLock)
- ✅ Sistema de despacho thread-safe para múltiples elevadores
- ✅ Operaciones atómicas sin deadlocks
- ✅ Soporte para operaciones concurrentes

###  **Auditoría y Monitoreo**
- ✅ Registro detallado de eventos de seguridad
- ✅ Seguimiento de métricas (movimientos, peso, sobrecargas)
- ✅ Alertas de salud del sistema en tiempo real
- ✅ Sistema de listeners para eventos de auditoría

###  **Manejo Robusto de Errores**
- ✅ Excepciones personalizadas específicas
- ✅ Validación completa de entrada
- ✅ Shutdown automático en situaciones peligrosas
- ✅ Recuperación ante condiciones de error

###  **Arquitectura Profesional**
- ✅ Inyección de dependencias
- ✅ Pattern Builder para eventos
- ✅ Separación clara de responsabilidades
- ✅ Código limpio y mantenible

---

##  Requisitos

-  **Java 17** o superior
-  **Maven 3.8+**
-  **Ejecutado en windows 11 64 b**



##  Compilar el proyecto

```bash
mvn clean compile
```

---

##  Ejecutar el Demo

Ejecuta la demostración del sistema:

```bash
mvn clean compile exec:java -D exec.mainClass=org.victor.Demo
```

O en un solo comando: compilar, test unitarios y demo

```bash
mvn clean compile test exec:java -D exec.mainClass=org.victor.Demo
```

La demo incluye:
-  Registro y autenticación de usuarios
-  Gestión de múltiples elevadores
-  Operaciones seguras con manejo de errores
-  Detección de sobrecarga y shutdown de emergencia
-  Control de acceso a pisos restringidos
-  Operaciones de elevador de carga
-  Operaciones concurrentes (multithreading)
-  Auditoría y logging
-  Métricas del sistema

---

## Ejecutar Pruebas

Ejecuta todas las pruebas unitarias (16 tests):

```bash
mvn test
```

## Estructura

```
elevators/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/org/victor/
│   │   ├── Elevator.java                          # Clase base abstracta
│   │   ├── PublicElevator.java                    # Elevador público
│   │   ├── FreightElevator.java                   # Elevador de carga
│   │   ├── Demo.java                              # Demostración
│   │   ├── exception/                             # Excepciones personalizadas
│   │   │   ├── ElevatorException.java
│   │   │   ├── ElevatorOverloadException.java
│   │   │   ├── AccessDeniedException.java
│   │   │   ├── ElevatorNotOperationalException.java
│   │   │   └── InvalidFloorException.java
│   │   ├── security/                              # Seguridad
│   │   │   ├── User.java
│   │   │   └── UserManager.java
│   │   ├── audit/                                 # Auditoría
│   │   │   ├── AuditEvent.java
│   │   │   └── AuditLogger.java
│   │   ├── monitoring/                            # Monitoreo
│   │   │   ├── ElevatorMetrics.java
│   │   │   └── SystemMonitor.java
│   │   └── dispatch/                              # Despacho
│   │       ├── ElevatorDispatcher.java
│   │       └── ElevatorRequest.java
│   └── test/java/org/victor/
│       └── ElevatorTest.java                      # 16 pruebas unitarias
└── target/
```




## Notas Importantes

- **Seguridad**: Las contraseñas se encriptan con BCrypt
- **Concurrencia**: Todas las operaciones críticas son thread-safe
- **Logging**: Usa SLF4J + Logback para registro detallado
- **Testing**: 16 pruebas unitarias automatizadas
- **Auditoría**: Todos los eventos de seguridad se registran
- **Monitoreo**: Sistema de alertas en tiempo real

---

## Autor

**Victor Guerrero**



