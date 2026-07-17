# Control de Gastos App

Aplicación de control de gastos personal desarrollada con Kotlin Multiplatform y Compose Multiplatform.

## Funcionalidades

- **Dashboard**: Resumen mensual con ingresos, gastos, ahorro y saldo
- **Movimientos**: Registro de gastos variables y efectivo con categorías
- **Cuentas**: Posición financiera con cuentas bancarias y otros activos
- **Presupuesto**: Límites mensuales por categoría con seguimiento
- **Conexión Bancaria**: Sincronización automática con cuentas bancarias via Nordigen (GoCardless)

## Categorías

- Alimentación
- Bares
- Coche
- Gata
- Gym
- Viajes
- Cumple
- Transporte
- Cofradía
- Farmacia
- Libros
- Impuestos
- Otros

## Tecnología

- **Kotlin Multiplatform** - Código compartido para todas las plataformas
- **Compose Multiplatform** - UI nativa para cada plataforma
- **Room** - Base de datos local
- **Ktor** - Cliente HTTP para APIs
- **Nordigen (GoCardless)** - Conexión bancaria Open Banking
- **Koin** - Inyección de dependencias
- **Voyager** - Navegación

## Plataformas

- Android
- Windows / macOS / Linux (Desktop)
- iOS

## Configuración

### Nordigen API

1. Regístrate en [GoCardless](https://gocardless.com)
2. Obtén tu `secret_id` y `secret_key`
3. Configura en la app: Ajustes > Conexión Bancaria

### Compilación

```bash
# Desktop
./gradlew :composeApp:run

# Android
./gradlew :composeApp:installDebug
```

## Licencia

MIT License - © 2026 Rarcega
