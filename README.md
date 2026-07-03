# Soft-eComm Backend

Backend API para una plataforma de e-commerce desarrollada con **Spring Boot 3.5.x** y **Java 21**.

---

## Tabla de Contenidos

- [Stack Tecnológico](#-stack-tecnológico)
- [Arquitectura](#-arquitectura)
- [Base de Datos](#-base-de-datos)
- [Seguridad y Autenticación](#-seguridad-y-autenticación)
- [API Endpoints](#-api-endpoints)
- [Documentación API](#-documentación-api)
- [Configuración](#-configuración)
- [Principales Funcionalidades](#-principales-funcionalidades)

---

## Stack Tecnológico

### Core Framework
| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Spring Boot** | 3.5.13 | Framework principal para aplicaciones Spring |
| **Java** | 21 | Lenguaje de programación (LTS) |
| **Maven** | Wrapper | Gestión de dependencias y build |

### Spring Starters
| Dependencia | Propósito |
|-------------|-----------|
| `spring-boot-starter-web` | API REST, Tomcat embebido, Jackson |
| `spring-boot-starter-data-jpa` | Persistencia con Hibernate/JPA |
| `spring-boot-starter-security` | Autenticación y autorización |
| `spring-boot-starter-validation` | Validación Bean Validation (JSR-380) |
| `spring-boot-starter-test` | Testing (JUnit 5, Mockito, Spring Test) |

### Seguridad y JWT
| Librería | Versión | Uso |
|----------|---------|-----|
| `jjwt-api` | 0.12.5 | API para JWT |
| `jjwt-impl` | 0.12.5 | Implementación JWT (runtime) |
| `jjwt-jackson` | 0.12.5 | Serialización JSON para JWT |
| `BCryptPasswordEncoder` | - | Hashing de contraseñas |

### Base de Datos
| Tecnología | Versión | Configuración |
|------------|---------|---------------|
| **MySQL** | 8.x (via `mysql-connector-j`) | Driver JDBC |
| **Hibernate** | 6.x (via Spring Boot) | JPA Provider, DDL: `validate` |

### Utilidades y Mapeo
| Librería | Versión | Uso |
|----------|---------|-----|
| **Lombok** | Latest | Reduce boilerplate (@Data, @Builder, etc.) |
| **ModelMapper** | 3.2.4 | Mapeo Entity ↔ DTO |
| **Stripe Java** | 29.3.0 | Integración de pagos |

### Documentación API
| Herramienta | Versión | Descripción |
|-------------|---------|-------------|
| **SpringDoc OpenAPI** | 2.8.17 | Generación automática OpenAPI 3 / Swagger UI |

---

## Arquitectura

### Patrones Aplicados
- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separación entre entidades de persistencia y objetos de transferencia
- **Interface Segregation**: Interfaces de servicio (`*Service`) separadas de implementaciones (`*ServiceImpl`)
- **Builder Pattern**: Lombok `@Builder` en entidades y DTOs
- **Stateless JWT**: Autenticación sin sesión en servidor

### Capas

```
├── controller          # REST Controllers (endpoints HTTP)
├── service
│   ├── api             # Interfaces de servicio
│   └── impl            # Implementaciones de lógica de negocio
├── repository          # Spring Data JPA Repositories
├── model               # Entidades JPA (@Entity)
├── payload             # DTOs (Request/Response)
├── security
│   ├── config          # WebSecurityConfig, JWT Config
│   ├── jwt             # Filtros, utilidades, entry point
│   ├── request         # LoginRequest, SignUpRequest
│   ├── response        # UserInfoResponse, MessageResponse
│   └── services        # UserDetailsServiceImpl, UserDetailsImpl
├── exception           # GlobalExceptionHandler, excepciones custom
├── config              # Swagger, ModelMapper, AppConstants
└── utils               # Utilidades auxiliares
```

---

## Base de Datos

### Esquema Principal (Entidades JPA)

| Entidad | Tabla | Descripción |
|---------|-------|-------------|
| `User` | `users` | Usuarios del sistema |
| `Role` | `roles` | Roles: USER, SELLER, ADMIN |
| `Product` | `products` | Catálogo de productos |
| `Category` | `categories` | Categorías de productos |
| `Cart` | `carts` | Carrito de compras (1:1 User) |
| `CartItem` | `cart_items` | Items del carrito |
| `Order` | `orders` | Pedidos |
| `OrderItem` | `order_items` | Items de pedido |
| `Payment` | `payments` | Pagos (Stripe) |
| `Address` | `addresses` | Direcciones de envío |

### Relaciones Clave
- **User ↔ Role**: Many-to-Many (`user_role`)
- **User → Address**: One-to-Many
- **User → Cart**: One-to-One
- **User → Product**: One-to-Many (seller)
- **Cart → CartItem**: One-to-Many
- **Order → OrderItem**: One-to-Many
- **Order → Payment**: One-to-One
- **Category → Product**: One-to-Many

## Seguridad y Autenticación

### JWT (JSON Web Tokens)
- **Algoritmo**: HS256 (firma simétrica)
- **Expiración**: 300,000,000 ms (~3.5 días)
- **Transporte**: HttpOnly Cookie (`ecomm-cookie`) + Header Authorization Bearer
- **Secret**: Configurable via `spring.app.jwtSecret`

### Flujo de Autenticación
1. **Login** (`POST /ecomApi/auth/signin`) → Devuelve JWT en cookie HttpOnly
2. **Requests posteriores** → Cookie enviada automáticamente + Header `Authorization: Bearer <token>`
3. **Filtro JWT** (`AuthTokenFilter`) → Valida token, carga `UserDetails` en `SecurityContext`
4. **Logout** (`POST /ecomApi/auth/signout`) → Invalida cookie

### Roles y Autorización (RBAC)
| Rol | Permisos |
|-----|----------|
| `ROLE_USER` | Acceso a endpoints públicos, carrito, pedidos propios |
| `ROLE_SELLER` | Gestión de productos propios + permisos USER |
| `ROLE_ADMIN` | Acceso total: usuarios, categorías, analytics, etc |

## API Endpoints

### Base Path: `/ecomApi`

#### Autenticación (`/auth`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/signin` | Login usuario | No |
| POST | `/signup` | Registro usuario | No |
| POST | `/signout` | Logout | Sí |
| GET | `/username` | Username actual | Sí |
| GET | `/user` | Detalles usuario actual | Sí |
| GET | `/sellers` | Listar vendedores (paginado) | Admin |

#### Productos (`/public/products`, `/admin/products`, `/seller/products`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/public/products` | Listar productos (paginado, filtros) | No |
| GET | `/public/products/{id}` | Detalle producto | No |
| POST | `/seller/products` | Crear producto | Seller/Admin |
| PUT | `/seller/products/{id}` | Actualizar producto | Seller/Admin |
| DELETE | `/seller/products/{id}` | Eliminar producto | Seller/Admin |
| GET | `/admin/products` | Admin: todos productos | Admin |

#### Categorías (`/public/categories`, `/admin/categories`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/public/categories` | Listar categorías | No |
| GET | `/public/categories/{id}` | Detalle categoría | No |
| POST | `/admin/categories` | Crear categoría | Admin |
| PUT | `/admin/categories/{id}` | Actualizar categoría | Admin |
| DELETE | `/admin/categories/{id}` | Eliminar categoría | Admin |

#### Carrito (`/cart`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/cart` | Ver carrito | Usuario |
| POST | `/cart/add` | Agregar item | Usuario |
| PUT | `/cart/item/{id}` | Actualizar cantidad | Usuario |
| DELETE | `/cart/item/{id}` | Eliminar item | Usuario |
| DELETE | `/cart/clear` | Vaciar carrito | Usuario |

#### Pedidos (`/orders`, `/admin/orders`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/orders` | Crear pedido desde carrito | Usuario |
| GET | `/orders` | Mis pedidos (paginado) | Usuario |
| GET | `/orders/{id}` | Detalle pedido | Usuario |
| PUT | `/admin/orders/{id}/status` | Actualizar estado | Admin |
| GET | `/admin/orders` | Todos pedidos | Admin |

#### Direcciones (`/addresses`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/addresses` | Mis direcciones | Usuario |
| POST | `/addresses` | Agregar dirección | Usuario |
| PUT | `/addresses/{id}` | Actualizar dirección | Usuario |
| DELETE | `/addresses/{id}` | Eliminar dirección | Usuario |

#### Pagos Stripe (`/payments`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/payments/create-payment-intent` | Crear PaymentIntent | Usuario |
| POST | `/payments/webhook` | Webhook Stripe | No (Stripe) |

#### Analytics (`/admin/analytics`)
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/admin/analytics/summary` | Resumen ventas/usuarios | Admin |
| GET | `/admin/analytics/top-products` | Productos top | Admin |
| GET | `/admin/analytics/sales-by-period` | Ventas por período | Admin |

## Documentación API

### Swagger UI / OpenAPI 3
- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`
- **Configuración**: `SwaggerConfig.java` con Bearer Auth JWT

### Características
- Documentación automática de todos los endpoints
- Esquemas de request/response generados desde DTOs
- Autorización JWT integrada en UI (botón "Authorize")
- Agrupación por tags (Auth, Products, Categories, Cart, Orders, etc.)

---

## Configuración

### Variables Principales (`application.properties`)

```properties
# Aplicación
spring.application.name=backend
spring.web.locale=en_US

# JWT
spring.app.jwtSecret=<secret-key-256-bits>
spring.app.jwtExpirationMs=300000000
spring.ecom.app.jwtCookieName=ecomm-cookie

# Base de Datos MySQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/ecomm?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=0000

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate

# Imágenes
image.base.url=http://localhost:8080/images
project.image=images/

# Stripe
stripe.secret.key=sk_test_...
```

## Principales Funcionalidades

###  Catálogo y Productos
- CRUD completo de productos (Seller/Admin)
- Búsqueda con filtros: categoría, precio, nombre, paginación
- Gestión de categorías (Admin)
- Imágenes de productos (servidas estáticamente)

###  Carrito de Compras
- Un carrito por usuario (1:1)
- Agregar/actualizar/eliminar items
- Cálculo de totales automático

###  Pedidos y Checkout
- Crear pedido desde carrito
- Historial de pedidos por usuario
- Estados: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
- Gestión de estados (Admin)

###  Pagos con Stripe
- PaymentIntent creation
- Webhook para confirmación asíncrona
- Registro de pagos en BD

### Gestión de Usuarios y Roles
- Registro con validación (email único, username único)
- Login con JWT en cookie HttpOnly
- Roles: USER, SELLER, ADMIN (inicializados en arranque)
- Perfil de usuario y direcciones múltiples

### Archivos Estáticos
- Servicio de imágenes en `/images/**`
- Configuración CORS habilitada

### Seguridad
- JWT Stateless con expiración configurable
- BCrypt para passwords
- Validación Bean Validation en DTOs
- Manejo global de excepciones (`GlobalExceptionHandler`)
- CORS configurado
---

## Autor

**Giovani Saavedra**  
giovani.saavedra@pucp.edu.pe

---
