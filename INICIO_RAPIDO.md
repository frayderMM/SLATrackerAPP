# 🚀 INICIO RÁPIDO - Módulo de Estadísticas

## ⚡ Configuración en 3 Pasos

### 1️⃣ Iniciar Backend
```powershell
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run
```
✅ Debe mostrar: `Now listening on: http://localhost:5192`

---

### 2️⃣ Configurar IP en Android

**Abrir:** `ApiClient.kt` (línea 12)

**Para EMULADOR:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:5192/"  // ✅ Ya configurado
```

**Para DISPOSITIVO FÍSICO:**
```kotlin
private const val BASE_URL = "http://TU_IP_LOCAL:5192/"  // Ej: 192.168.1.100
```

Para obtener tu IP local:
```powershell
ipconfig
# Busca "IPv4" en "Adaptador de LAN inalámbrica Wi-Fi"
```

---

### 3️⃣ Ejecutar App en Android Studio

1. **Sync Gradle** (si es necesario)
2. **Run** (Shift + F10)
3. **Login** con tus credenciales de BD
4. **Navegar** a Estadísticas
5. **Aplicar filtros** y ver datos reales

---

## 📊 Funcionalidades Implementadas

### ✅ Login
- Autenticación con backend
- Token JWT guardado automáticamente
- Navegación después de login exitoso

### ✅ Estadísticas
- **Filtros:**
  - Tipo de SLA (SLA1, SLA2, Todos)
  - Fecha Inicio / Fecha Fin
  - Bloque Tech (múltiple selección)
  
- **KPIs Calculados Automáticamente:**
  - Cumplimiento % (verde/amarillo/rojo)
  - Total Solicitudes
  - Tiempo Promedio (días)
  - En Alerta (70-79%)
  - % Incumplidas
  - Período aplicado

- **Detalle por Rol:**
  - Tabla con solicitudes por Bloque Tech
  - SLA % por bloque
  - Tiempo promedio por bloque
  - Indicador visual (●) de cumplimiento

- **Análisis de Incumplimientos:**
  - Total de incumplimientos
  - Retraso promedio sobre umbral
  - Retraso máximo
  - Detalle por Bloque Tech

- **Configuración de Reporte:**
  - Nombre personalizable
  - Sugerencia automática con timestamp

---

## 🎯 Endpoints Utilizados

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/Usuarios/authenticate` | POST | Login y obtención de token |
| `/api/Dashboard/sla/data` | GET | Datos de dashboard con filtros |
| `/api/Dashboard/sla/statistics` | GET | Estadísticas generales |
| `/api/ConfigSla` | GET | Configuración de SLAs |

---

## 🧪 Probar el Flujo Completo

1. **Iniciar backend** (paso 1)
2. **Ejecutar app** en emulador o dispositivo
3. **Login** con credenciales válidas de tu BD
4. **Ir a Estadísticas** desde el menú
5. **Seleccionar filtros:**
   - Tipo SLA: `SLA1`
   - Fecha Inicio: (clic en campo → usar fecha actual)
   - Fecha Fin: (clic en campo → usar fecha actual)
   - Bloque Tech: Seleccionar uno o varios
6. **Presionar "Aplicar Filtros"**
7. **Verificar que se muestren datos reales** del backend

---

## ⚠️ Si algo no funciona

### Backend no responde
```powershell
# Verificar que esté corriendo
netstat -ano | findstr :5192

# Si no aparece, iniciar backend nuevamente
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run
```

### Error de conexión en app
1. Verifica la IP en `ApiClient.kt`
2. Si usas dispositivo físico, asegúrate de estar en la misma red WiFi
3. Revisa Logcat en Android Studio para ver errores detallados

### Credenciales incorrectas
- Verifica usuario/contraseña en tu base de datos PostgreSQL
- El endpoint debe retornar un token JWT válido

---

## 📁 Archivos Principales Creados

```
✅ TokenManager.kt             - Gestión de token JWT
✅ ApiClient.kt                - Cliente HTTP con interceptor
✅ StatisticsRepository.kt     - Llamadas al backend
✅ StatisticsViewModel.kt      - Lógica de negocio y KPIs
✅ LoginViewModel.kt           - Login con guardado de token
✅ LoginViewModelFactory.kt    - Factory para ViewModel
✅ StatisticsScreen.kt         - UI conectada con backend
✅ DashboardDto.kt             - DTOs para dashboard
✅ CONFIGURACION_ESTADISTICAS.md - Documentación completa
```

---

## 🎉 ¡Listo para Usar!

Todo el flujo desde **login hasta estadísticas** está completamente funcional e integrado con el backend.

**Siguiente paso:** Probar con tus credenciales reales de la base de datos.
