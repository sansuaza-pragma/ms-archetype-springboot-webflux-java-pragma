# Arquetipo Spring Boot WebFlux - Capa de Integración

## Descripción

✨ Arquetipo empresarial especializado para el desarrollo de capas de integración reactivas. Este proyecto proporciona una base sólida y reutilizable implementando arquitectura hexagonal, programación reactiva con Spring WebFlux, y las mejores prácticas para resolver problemas comunes en proyectos de integración entre sistemas.

El arquetipo fue diseñado específicamente para abordar los desafíos de las capas de integración, incluyendo: consumo de APIs externas con resiliencia, transformación de datos entre sistemas, manejo robusto de errores, trazabilidad de transacciones, y alta disponibilidad. Incluye componentes transversales pre-configurados como cliente REST genérico con Circuit Breaker, manejo centralizado de excepciones, validación de seguridad, integración con Azure Key Vault, y observabilidad con Application Insights.

---

## Estructura de Directorios

```
📂 ms-archetype-springboot-webflux-java
 ┣ 📂 deployment/                          # Configuración de despliegue Kubernetes/Helm
 ┃ ┣ 📂 values/                            # Valores por ambiente (ConfigMap, Deployment, HPA, Ingress, Service)
 ┃ ┣ 📜 Chart.yaml                         # Definición del Helm Chart
 ┃ ┗ 📜 values.yaml                        # Valores por defecto
 ┣ 📂 gradle/wrapper/                      # Gradle Wrapper
 ┣ 📂 src/
 ┃ ┣ 📂 main/
 ┃ ┃ ┣ 📂 java/com/company/integration/
 ┃ ┃ ┃ ┣ 📂 crosscutting/                 # Funcionalidades transversales
 ┃ ┃ ┃ ┃ ┣ 📂 constants/                  # Constantes globales y por dominio
 ┃ ┃ ┃ ┃ ┣ 📂 documentation/              # Configuración Swagger/OpenAPI
 ┃ ┃ ┃ ┃ ┣ 📂 entrypointfilter/           # Filtros de contexto y seguridad
 ┃ ┃ ┃ ┃ ┣ 📂 exceptions/                 # Manejo global de excepciones
 ┃ ┃ ┃ ┃ ┣ 📂 infraestructure/            # Configuraciones de infraestructura
 ┃ ┃ ┃ ┃ ┣ 📂 logging/                    # Configuración de logging
 ┃ ┃ ┃ ┃ ┣ 📂 messages/                   # Mensajes y plantillas
 ┃ ┃ ┃ ┃ ┣ 📂 restclients/                # Cliente REST genérico con Circuit Breaker
 ┃ ┃ ┃ ┃ ┗ 📂 utility/                    # Utilidades comunes
 ┃ ┃ ┃ ┣ 📂 [domain-module]/              # Módulo de dominio (ejemplo: loansanddeposits)
 ┃ ┃ ┃ ┃ ┣ 📂 application/                # Casos de uso
 ┃ ┃ ┃ ┃ ┣ 📂 domain/                     # Lógica de negocio
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 models/                   # Entidades de dominio
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 ports/                    # Puertos (in/out)
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 services/                 # Servicios de dominio
 ┃ ┃ ┃ ┃ ┗ 📂 infraestructure/            # Adaptadores
 ┃ ┃ ┃ ┃   ┣ 📂 controller/               # Controladores REST tradicionales
 ┃ ┃ ┃ ┃   ┣ 📂 dataproviders/            # Adaptadores de persistencia
 ┃ ┃ ┃ ┃   ┗ 📂 entrypoints/              # Puntos de entrada (REST routers)
 ┃ ┃ ┃ ┗ 📜 IntegrationApplication.java   # Clase principal
 ┃ ┃ ┗ 📂 resources/
 ┃ ┃   ┣ 📜 application-local.yaml        # Configuración ambiente local
 ┃ ┃   ┗ 📜 logback-spring.xml            # Configuración de logging
 ┃ ┗ 📂 test/                              # Tests unitarios e integración
 ┣ 📜 build.gradle                         # Configuración de dependencias
 ┣ 📜 settings.gradle                      # Configuración del proyecto Gradle
 ┣ 📜 azure-pipelines.yml                  # Pipeline CI/CD Azure DevOps
 ┣ 📜 .gitignore                           # Archivos ignorados por Git
 ┗ 📜 README.md                            # Este archivo
```

---

## Características

### Arquitectura y Diseño
- ✅ **Arquitectura Hexagonal (Puertos y Adaptadores)** - Separación clara entre dominio, aplicación e infraestructura
- ✅ **Programación Reactiva** - Spring WebFlux con Project Reactor para alta concurrencia y manejo eficiente de I/O
- ✅ **Domain-Driven Design (DDD)** - Modelado orientado al dominio de integración
- ✅ **Functional Endpoints** - Routers funcionales para endpoints REST
- ✅ **Especializado para Capas de Integración** - Resuelve problemas comunes: transformación de datos, orquestación de servicios, manejo de timeouts y reintentos

### Resiliencia y Seguridad
- ✅ **Circuit Breaker** - Resilience4j con recarga dinámica de configuración
- ✅ **Filtro de Hosts Permitidos** - Validación de seguridad para llamadas HTTP externas
- ✅ **Manejo Global de Excepciones** - Respuestas estandarizadas de error
- ✅ **Validación de Headers y Body** - Validadores reactivos personalizados

### Persistencia Reactiva
- ✅ **R2DBC** - Acceso reactivo a SQL Server
- ✅ **MongoDB Reactivo** - Soporte para bases de datos NoSQL
- ✅ **Redis Reactivo** - Caché distribuido

### Observabilidad
- ✅ **Application Insights** - Telemetría y monitoreo en Azure
- ✅ **Logging Estructurado** - Logback con contexto de transacción
- ✅ **Health Checks** - Endpoints de liveness y readiness
- ✅ **Swagger/OpenAPI** - Documentación automática de APIs

### Integración (Especialización del Arquetipo)
- ✅ **Cliente REST Genérico** - Abstracción reutilizable para consumo de APIs externas con manejo de errores
- ✅ **Transformación de Datos** - Mapeo entre diferentes formatos y contratos de APIs
- ✅ **Orquestación de Servicios** - Composición reactiva de múltiples llamadas a sistemas externos
- ✅ **Azure Key Vault** - Gestión segura de secretos y configuraciones de endpoints
- ✅ **Trazabilidad End-to-End** - Transaction ID propagado en todas las operaciones de integración
- ✅ **Manejo de Timeouts** - Configuración granular de timeouts por servicio externo
- ✅ **Estrategias de Reintento** - Políticas de retry configurables para servicios no disponibles

### DevOps
- ✅ **Helm Charts** - Despliegue en Kubernetes
- ✅ **Azure Pipelines** - CI/CD automatizado
- ✅ **Multi-ambiente** - Configuraciones para local, dev, qa, prod
- ✅ **HPA (Horizontal Pod Autoscaler)** - Escalado automático

---

## Tecnologías Utilizadas

### Backend
- **Plataforma**: Spring Boot 3.5.9
- **Lenguaje**: Java 21 (LTS)
- **Framework Reactivo**: Spring WebFlux (Project Reactor)
- **Build Tool**: Gradle 8.x

### Persistencia
- **SQL Reactivo**: R2DBC con SQL Server
- **NoSQL**: MongoDB Reactivo
- **Caché**: Redis Reactivo (Lettuce)

### Resiliencia y Seguridad
- **Circuit Breaker**: Resilience4j 2.3.0
- **Validación**: Jakarta Validation (Bean Validation)
- **Gestión de Secretos**: Azure Key Vault

### Observabilidad y Documentación
- **Telemetría**: Azure Application Insights
- **Logging**: Logback + SLF4J
- **Documentación API**: Springdoc OpenAPI 2.8.15
- **Monitoreo**: Spring Boot Actuator

### Testing
- **Framework**: JUnit 5 (Jupiter)
- **Reactivo**: Reactor Test
- **Mocking**: Mockito 5.2.0
- **HTTP Mock**: OkHttp MockWebServer 5.3.2

### Cloud y DevOps
- **Cloud Provider**: Microsoft Azure
- **Orquestación**: Kubernetes + Helm
- **CI/CD**: Azure DevOps Pipelines
- **Contenedores**: Docker

---

## Guía de Inicio Rápido

### 🚀 Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

- **Java 21** - [Descargar OpenJDK](https://adoptium.net/)
- **Gradle 8.x** - (incluido via Gradle Wrapper)
- **Docker** - [Descargar Docker](https://www.docker.com/products/docker-desktop)
- **Git** - [Descargar Git](https://git-scm.com/)

Opcional para desarrollo local:
- **SQL Server** - Para persistencia relacional
- **MongoDB** - Para persistencia NoSQL
- **Redis** - Para caché

### 🚀 Configurar

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/your-org/ms-archetype-springboot-webflux-java.git
cd ms-archetype-springboot-webflux-java
```

#### 2. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto (o configura las variables en tu IDE):

```bash
# Azure Key Vault
AZURE_KEYVAULT_URI=https://your-keyvault.vault.azure.net/
AZURE_CLIENT_ID=your-client-id
AZURE_CLIENT_SECRET=your-client-secret
AZURE_TENANT_ID=your-tenant-id

# Application Insights
APPINSIGHTS_INSTRUMENTATIONKEY=your-instrumentation-key

# Base de Datos (Opcional para local)
DB_HOST=localhost
DB_PORT=1433
DB_USER=SA
DB_PASSWORD=yourStrongPassword123
```

#### 3. Configurar Bases de Datos Locales (Opcional)

Si deseas ejecutar con bases de datos locales, puedes usar Docker Compose:

```bash
# SQL Server
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=yourStrongPassword123" \
   -p 1433:1433 --name sqlserver -d mcr.microsoft.com/mssql/server:2022-latest

# MongoDB
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Redis
docker run -d -p 6379:6379 --name redis redis:latest
```

#### 4. Ajustar Configuración Local

Edita `src/main/resources/application-local.yaml` según tu entorno:

```yaml
spring:
  application:
    name: ms-archetype-springboot-webflux-java

bd:
  sqlserver:
    host: localhost
    port: 1433
    user: SA
    password: yourStrongPassword123

# Configura los hosts permitidos para el cliente REST
rest:
  allowed-host:
    host-name: "api.example.com,another-service.com"
```

### 🚀 Instalación

#### Compilar el Proyecto

```bash
# Usando Gradle Wrapper (recomendado)
./gradlew clean build

# O si tienes Gradle instalado globalmente
gradle clean build
```

#### Ejecutar Tests

```bash
./gradlew test
```

#### Ejecutar la Aplicación

```bash
# Modo desarrollo con perfil local
./gradlew bootRun --args='--spring.profiles.active=local'

# O usando el JAR compilado
java -jar build/libs/ms-archetype-springboot-webflux-java-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=local
```

La aplicación estará disponible en: `http://localhost:8080`

#### Acceder a la Documentación API

Una vez iniciada la aplicación, accede a Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## 🛠 Despliegue, Compilación y Pruebas

### Compilación para Producción

```bash
# Compilar sin ejecutar tests
./gradlew clean build -x test

# Compilar con tests y análisis de código
./gradlew clean build check
```

### Crear Imagen Docker

```bash
# Construir imagen
docker build -t mercantil/ms-archetype-webflux:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  mercantil/ms-archetype-webflux:latest
```

### Despliegue en Kubernetes con Helm

#### 1. Configurar valores por ambiente

Edita los archivos en `deployment/values/` según tu ambiente:

```bash
# Desarrollo
helm install integration-layer ./deployment \
  -f deployment/values/values-dev.yaml \
  --namespace integration-dev

# Producción
helm install integration-layer ./deployment \
  -f deployment/values/values-prod.yaml \
  --namespace integration-prod
```

#### 2. Verificar despliegue

```bash
# Ver pods
kubectl get pods -n integration-dev

# Ver logs
kubectl logs -f <pod-name> -n integration-dev

# Ver servicios
kubectl get svc -n integration-dev
```

### 🧪 Ejecución de Pruebas

#### Tests Unitarios

```bash
./gradlew test
```

#### Tests de Integración

```bash
./gradlew integrationTest
```

#### Reporte de Cobertura

```bash
./gradlew jacocoTestReport

# Ver reporte en: build/reports/jacoco/test/html/index.html
```

### 🧪 Validación de Recursos

Una vez desplegados los recursos en Kubernetes, valida que estén correctamente configurados:

```bash
# Health checks
curl http://your-service/actuator/health
curl http://your-service/actuator/health/liveness
curl http://your-service/actuator/health/readiness

# Métricas
curl http://your-service/actuator/metrics

# Verificar Circuit Breaker
curl http://your-service/actuator/circuitbreakers
```

También puedes usar herramientas como:
- **Azure Monitor** - Para visualizar métricas y logs
- **Kubernetes Dashboard** - Para monitorear pods y servicios
- **Grafana/Prometheus** - Para métricas personalizadas

---

## 📖 Uso del Arquetipo

### Crear una Nueva Capa de Integración

1. **Clonar el arquetipo**:
```bash
git clone https://github.com/your-org/ms-archetype-springboot-webflux-java.git my-integration-layer
cd my-integration-layer
```

2. **Renombrar el paquete base**:
   - Cambiar `com.mercantil.operationsandexecution` por tu paquete (ej: `com.company.integration`)
   - Actualizar `CommonConstants.PACKAGE_PROJECT_ROOT`
   - Renombrar `BankingApplication` a `IntegrationApplication` y actualizar `@SpringBootApplication`

3. **Definir tu dominio de integración**:
   - Duplicar la estructura de `loansanddeposits/` con el nombre de tu módulo
   - Implementar modelos de entrada/salida para transformación de datos
   - Definir puertos para servicios externos (ports/out)
   - Crear servicios de orquestación en `domain/services/`

4. **Configurar adaptadores externos**:
   - Implementar adaptadores en `infraestructure/dataproviders/` para cada sistema externo
   - Configurar URLs y credenciales en `application.yaml`
   - Usar el cliente REST genérico para consumir APIs

5. **Configurar endpoints de integración**:
   - Crear routers en `infraestructure/entrypoints/rest/router/`
   - Implementar handlers con validación de headers
   - Documentar contratos con anotaciones OpenAPI

### Usar el Cliente REST Genérico

```java
@Service
@RequiredArgsConstructor
public class ExternalApiAdapter implements GetSomeByRest {
    
    private final IGenericRestClient genericRestClient;
    
    @Value("${adapter.restconsumer.baseurl}")
    private String baseUrl;
    
    @Override
    public Mono<SomeByRestModel> getSomeByRest() {
        var config = new HttpRequestConfiguration<>(
            HttpMethod.GET,
            Map.of("Content-Type", "application/json"),
            null,
            new HttpResponsePayload<>(SomeByRestModel.class, ErrorResponse.class)
        );
        
        return genericRestClient
            .sendRequestAndReceiveResponse(
                config,
                UUID.randomUUID().toString(),
                URI.create(baseUrl + "/api/resource")
            )
            .map(ResponseEntity::getBody);
    }
}
```

### Configurar Circuit Breaker

Crea un archivo `circuit-breaker-profiles.json` en `resources/`:

```json
{
  "profiles": {
    "default": {
      "slidingWindowSize": 10,
      "failureRateThreshold": 50,
      "waitDurationInOpenState": 10000,
      "permittedNumberOfCallsInHalfOpenState": 3,
      "slowCallDurationThreshold": 2000,
      "slowCallRateThreshold": 50
    }
  }
}
```

---

## Autores y Agradecimientos

### Equipo de Desarrollo

- **Arquitecto**: Cesar Herrera - Diseño de arquitectura hexagonal y patrones reactivos
- **Backend Lead**: David Buitrago - Implementación de componentes transversales y especialización para capas de integración
- **DevSecOps**: Jhon Quevedo - Pipeline CI/CD y configuración Kubernetes

### Agradecimientos

- Comunidad Spring Framework
- Proyecto Reactor Team
- Resilience4j Contributors

---

## Licencia

| Licencia | Uso Comercial | Modificación | Distribución | Sublicencia | Descripción breve |
|----------|---------------|--------------|--------------|-------------|-------------------|
| **Apache 2.0** | ✅ Sí | ✅ Sí | ✅ Sí | ✅ Sí | Licencia permisiva con protección de patentes. Permite uso comercial con atribución. |

Este proyecto está licenciado bajo la Licencia Apache 2.0. Consulta el archivo `LICENSE` para más detalles.

```
Copyright 2024 [Your Organization]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## FAQs

### ❓ Preguntas Frecuentes

#### ¿Por qué usar programación reactiva?

La programación reactiva con Spring WebFlux permite:
- **Mayor throughput**: Manejo eficiente de miles de conexiones concurrentes con pocos threads
- **Mejor uso de recursos**: Non-blocking I/O reduce el consumo de memoria
- **Backpressure**: Control de flujo automático para evitar sobrecarga
- **Ideal para microservicios**: Composición de llamadas asíncronas sin bloqueo

#### ¿Cómo agregar un nuevo módulo de integración?

1. Crea la estructura en `src/main/java/com/company/integration/[tu-modulo]/`
2. Sigue la estructura hexagonal: `application/`, `domain/`, `infraestructure/`
3. Define tus puertos (interfaces) en `domain/ports/in` (casos de uso) y `domain/ports/out` (servicios externos)
4. Implementa servicios de orquestación en `domain/services/`
5. Crea adaptadores para sistemas externos en `infraestructure/dataproviders/`
6. Configura endpoints de entrada en `infraestructure/entrypoints/`

#### ¿Cómo funciona el Circuit Breaker?

El Circuit Breaker protege tu aplicación de fallos en cascada:
- **Cerrado (Closed)**: Peticiones fluyen normalmente
- **Abierto (Open)**: Si falla el % configurado, se abre y rechaza peticiones
- **Semi-abierto (Half-Open)**: Después del tiempo de espera, permite algunas peticiones de prueba

Configuración dinámica: El arquetipo recarga la configuración automáticamente cada día a las 4 AM.

#### ¿Cómo manejar transacciones en entornos reactivos?

R2DBC soporta transacciones reactivas:

```java
@Transactional
public Mono<Void> saveWithTransaction(Entity entity) {
    return repository.save(entity).then();
}
```

Para transacciones distribuidas, considera patrones como Saga.

#### ¿Cómo configurar diferentes ambientes?

Usa perfiles de Spring:
- `application.yaml` - Configuración base
- `application-local.yaml` - Desarrollo local
- `application-dev.yaml` - Desarrollo
- `application-qa.yaml` - QA
- `application-prod.yaml` - Producción

Activa con: `--spring.profiles.active=dev`

#### ¿Cómo agregar autenticación y autorización?

El arquetipo incluye filtros de seguridad. Para JWT:

1. Agrega Spring Security Reactive:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
```

2. Configura `SecurityWebFilterChain`
3. Implementa validación de tokens en filtros

#### ¿Qué hacer si el Circuit Breaker está siempre abierto?

Verifica:
- Logs de la aplicación para ver errores de los servicios externos
- Configuración de umbrales (`failureRateThreshold`, `slowCallDurationThreshold`)
- Conectividad con el servicio externo (red, firewall, DNS)
- Timeouts configurados (pueden ser muy agresivos para servicios lentos)
- Latencia real del servicio externo vs. configuración de `slowCallDurationThreshold`

#### ¿Cómo manejar transformaciones complejas de datos entre sistemas?

Para transformaciones complejas en capas de integración:

1. **Usa mappers dedicados**: Crea clases `Mapper` en el paquete `infraestructure/dataproviders/mappers/`
2. **Valida datos de entrada**: Usa Bean Validation en los DTOs
3. **Maneja valores opcionales**: Usa `Optional` o valores por defecto
4. **Documenta el mapeo**: Comenta las reglas de transformación no obvias
5. **Prueba exhaustivamente**: Crea tests unitarios para cada escenario de mapeo

```java
@Component
public class ExternalApiMapper {
    public InternalModel toInternal(ExternalApiResponse external) {
        return InternalModel.builder()
            .id(external.getExternalId())
            .name(Optional.ofNullable(external.getName()).orElse("Unknown"))
            .amount(convertCurrency(external.getAmount(), external.getCurrency()))
            .build();
    }
}
```

#### ¿Cómo propagar el Transaction ID en llamadas a servicios externos?

El arquetipo incluye propagación automática de Transaction ID:

1. El filtro `ContextWebFilter` extrae el header `transactionId`
2. Se almacena en el contexto reactivo
3. El `GenericRestClient` lo incluye automáticamente en headers de salida
4. Para propagación manual:

```java
return Mono.deferContextual(ctx -> {
    String txId = ctx.get("transactionId");
    // Usar txId en logs o headers personalizados
    return externalService.call(txId);
});
```

#### ¿Cómo depurar flujos reactivos?

Usa operadores de debugging:

```java
return service.getData()
    .doOnNext(data -> log.info("Received: {}", data))
    .doOnError(error -> log.error("Error: ", error))
    .log(); // Logging detallado de señales
```

#### ¿Cuáles son los errores comunes al migrar de Spring MVC?

- **Bloquear el thread**: Evita `.block()` en código de producción
- **No retornar Mono/Flux**: Los controladores deben retornar tipos reactivos
- **Usar APIs bloqueantes**: Reemplaza JDBC por R2DBC, RestTemplate por WebClient
- **No manejar backpressure**: Usa operadores como `buffer()`, `window()` apropiadamente

---

## Contribuciones e Historial de Versiones

### Cómo Contribuir

1. **Fork** el repositorio
2. Crea una **rama** para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** tus cambios (`git commit -m 'feat: agregar nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un **Pull Request**

### Convención de Commits

Seguimos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Formato, punto y coma faltantes, etc.
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Tareas de mantenimiento

### Historial de Versiones

#### v0.0.1-SNAPSHOT (Actual)
- ✨ Versión inicial del arquetipo
- ✅ Arquitectura hexagonal con Spring WebFlux
- ✅ Cliente REST genérico con Circuit Breaker
- ✅ Integración con Azure Key Vault y Application Insights
- ✅ Soporte para R2DBC, MongoDB y Redis reactivos
- ✅ Helm charts para despliegue en Kubernetes
- ✅ Pipeline CI/CD con Azure DevOps
- ✅ Documentación Swagger/OpenAPI
- ✅ Manejo global de excepciones
- ✅ Filtros de seguridad y validación

#### Roadmap

- [ ] v0.1.0 - Agregar soporte para Spring Security Reactive
- [ ] v0.2.0 - Implementar patrón Saga para transacciones distribuidas
- [ ] v0.3.0 - Agregar soporte para eventos con Kafka Reactive
- [ ] v0.4.0 - Implementar rate limiting y throttling
- [ ] v1.0.0 - Release estable para producción

---

## Recursos Adicionales

### Documentación Oficial

- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/)
- [R2DBC](https://r2dbc.io/)
- [Resilience4j](https://resilience4j.readme.io/)
- [Azure Spring Cloud](https://learn.microsoft.com/en-us/azure/developer/java/spring/)

### Guías y Tutoriales

- [Reactive Programming with Spring](https://spring.io/reactive)
- [Building Reactive REST APIs](https://spring.io/guides/gs/reactive-rest-service/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)

### Soporte

Para preguntas o soporte:
- **Issues**: [GitHub Issues](https://github.com/your-org/ms-archetype-springboot-webflux-java/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/ms-archetype-springboot-webflux-java/discussions)
- **Wiki**: [Documentación del Proyecto](https://github.com/your-org/ms-archetype-springboot-webflux-java/wiki)

---

**Desarrollado con ❤️ por el equipo de Arquitectura - Especializado para Capas de Integración**
