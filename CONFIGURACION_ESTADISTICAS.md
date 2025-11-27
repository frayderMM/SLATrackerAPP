# Guía de Configuración - Módulo de Estadísticas

## ✅ Implementación Completada

### 1. **Estructura de Archivos Creados/Actualizados**

#### DTOs (Data Transfer Objects)
- ✅ `DashboardDto.kt` - DTOs para dashboard (DashboardSlaDto, DashboardStatsDto, ConfigSla)
- ✅ `LoginRequest.kt` - Request para login (ya existía)
- ✅ `LoginResponse.kt` - Response de login (ya existía)

#### Capa de Datos
- ✅ `TokenManager.kt` - Gestión de token JWT y userId en SharedPreferences
- ✅ `ApiClient.kt` - Cliente Retrofit con AuthInterceptor para token automático
- ✅ `ApiService.kt` - Definición de endpoints (ya existía, actualizado)
- ✅ `StatisticsRepository.kt` - Repository para llamadas al dashboard con filtros

#### ViewModels
- ✅ `LoginViewModel.kt` - ViewModel de login (actualizado con guardado de token)
- ✅ `LoginViewModelFactory.kt` - Factory para LoginViewModel con Application context
- ✅ `StatisticsViewModel.kt` - ViewModel de estadísticas con lógica de KPIs

#### UI
- ✅ `LoginScreen.kt` - Pantalla de login (actualizada con ViewModelFactory)
- ✅ `StatisticsScreen.kt` - Pantalla de estadísticas conectada con backend

---

## 🔧 Configuración Necesaria

### 1. **Actualizar IP del Backend**

En `ApiClient.kt`, cambia la IP según tu configuración:

```kotlin
private const val BASE_URL = "http://10.0.2.2:5192/"  // Para emulador Android
// O usa tu IP local: "http://192.168.X.X:5192/"       // Para dispositivo físico
```

**Opciones de IP:**
- **Emulador Android**: `10.0.2.2` apunta a `localhost` de tu PC
- **Dispositivo físico**: Usa la IP de tu red local (ej: `192.168.1.100`)
- **Para encontrar tu IP**: En Windows PowerShell ejecuta `ipconfig` y busca tu IPv4

### 2. **Iniciar Backend ASP.NET**

```powershell
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run
```

El backend debe estar corriendo en `http://localhost:5192` o el puerto configurado.

### 3. **Credenciales de Prueba**

Para probar el login, usa tus credenciales de la base de datos PostgreSQL.

**Ejemplo:**
- Usuario: `tu_usuario`
- Contraseña: `tu_contraseña`

---

## 📋 Flujo de Funcionamiento

### 1. **Login**
1. Usuario ingresa credenciales en `LoginScreen`
2. `LoginViewModel` llama a `AuthRepository.login()`
3. Se obtiene el token JWT del backend
4. Token se guarda en `SharedPreferences` vía `TokenManager`
5. Navegación a pantalla principal con userId

### 2. **Estadísticas**
1. `StatisticsViewModel` carga configuración SLA al iniciar
2. Usuario aplica filtros (Tipo SLA, Fechas, Bloques Tech)
3. Al presionar "Aplicar Filtros", se llama al backend con parámetros
4. Backend retorna lista de `DashboardSlaDto`
5. ViewModel calcula KPIs automáticamente:
   - Cumplimiento %
   - Total Solicitudes
   - Tiempo Promedio
   - En Alerta
   - % Incumplidas
   - Detalle por Rol (Bloque Tech)
   - Análisis de Incumplimientos

### 3. **AuthInterceptor**
Todas las peticiones (excepto login) llevan automáticamente el header:
```
Authorization: Bearer <token>
```

---

## 🎯 Endpoints del Backend Utilizados

### Login
```
POST /api/Usuarios/authenticate
Body: { "username": "...", "password": "..." }
Response: { "token": "...", "usuario": {...} }
```

### Dashboard Data (con filtros)
```
GET /api/Dashboard/sla/data?slaCode=SLA1&startDate=2024-01-01&endDate=2024-12-31&bloqueTech=Backend
Response: [ { "idSolicitud": ..., "bloqueTech": ..., "cumpleSla1": ... }, ... ]
```

### Dashboard Statistics
```
GET /api/Dashboard/sla/statistics
Response: { "totalSolicitudes": ..., "cumplimientoSla1": ..., "cumplimientoSla2": ..., "tiempoPromedio": ... }
```

### Config SLA
```
GET /api/ConfigSla
Response: [ { "idConfigSla": 1, "codigoSla": "SLA1", "diasUmbral": 30 }, ... ]
```

---

## 🧪 Pasos para Probar

### 1. **Verificar Backend**
```powershell
# En carpeta del backend
dotnet run

# Debe mostrar: "Now listening on: http://localhost:5192"
```

### 2. **Actualizar IP en ApiClient.kt**
Si usas dispositivo físico, cambia a tu IP local.

### 3. **Compilar y Ejecutar App**
```
1. Abrir proyecto en Android Studio
2. Sync Gradle
3. Run app en emulador o dispositivo
```

### 4. **Flujo de Prueba**
1. **Login**: Ingresa credenciales de BD
2. Si login exitoso → navega a home
3. Navega a **Estadísticas**
4. Selecciona filtros (SLA, fechas, bloques)
5. Presiona **Aplicar Filtros**
6. Verifica que se muestren datos reales del backend

---

## 🔍 Cálculo de KPIs

### Cumplimiento SLA
```kotlin
cumplimiento = (solicitudes que cumplen SLA / total solicitudes) * 100
```

### Tiempo Promedio
```kotlin
tiempoPromedio = suma(diasTranscurridos) / total solicitudes
```

### En Alerta
```kotlin
enAlerta = solicitudes con porcentaje entre 70% y 79.9%
```

### Incumplidas
```kotlin
incumplidas = solicitudes con cumpleSla = false
porcentajeIncumplidas = (incumplidas / total) * 100
```

### Detalle por Rol
Agrupa solicitudes por `bloqueTech` y calcula métricas por grupo.

### Incumplimientos
Solo para solicitudes con `cumpleSla = false`:
- **Retraso Promedio**: `(diasTranscurridos - diasUmbral) promedio`
- **Retraso Máximo**: `(diasTranscurridos - diasUmbral) máximo`

---

## ⚠️ Troubleshooting

### Error: "Unable to resolve host"
- Verifica que el backend esté corriendo
- Verifica la IP en `ApiClient.BASE_URL`
- Si usas emulador, usa `10.0.2.2`
- Si usas dispositivo, verifica que estén en la misma red WiFi

### Error: "401 Unauthorized"
- El token no está siendo enviado correctamente
- Verifica que `TokenManager.getToken()` retorne el token
- Verifica que el login haya sido exitoso

### Error: "Credenciales incorrectas"
- Verifica usuario/contraseña en la base de datos
- Verifica que el endpoint `/api/Usuarios/authenticate` esté funcionando

### No se muestran datos
- Verifica que haya datos en la BD para los filtros aplicados
- Revisa los logs en Logcat para ver la respuesta del backend
- Verifica que los endpoints del backend retornen datos válidos

---

## 📱 Próximos Pasos

1. ✅ Login funcional con token guardado
2. ✅ Estadísticas con filtros y datos reales
3. 🔄 Implementar generación de PDF
4. 🔄 Implementar historial de reportes
5. 🔄 Agregar DatePicker real (actualmente usa fecha actual como mock)

---

## 📂 Estructura de Carpetas

```
app/src/main/java/dev/esandamzapp/slatrackerapp/
├── data/
│   ├── local/
│   │   └── TokenManager.kt ✅
│   ├── remote/
│   │   ├── ApiClient.kt ✅
│   │   ├── ApiService.kt ✅
│   │   └── dto/
│   │       ├── DashboardDto.kt ✅
│   │       ├── LoginRequest.kt
│   │       └── LoginResponse.kt
│   └── repository/
│       ├── AuthRepository.kt
│       └── StatisticsRepository.kt ✅
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt ✅
│   │   ├── LoginViewModel.kt ✅
│   │   ├── LoginViewModelFactory.kt ✅
│   │   └── LoginState.kt
│   └── statistics/
│       ├── StatisticsScreen.kt ✅
│       └── StatisticsViewModel.kt ✅
```

---

## 🎉 ¡Todo Listo!

El módulo de estadísticas está completamente integrado con el backend. Puedes probar el flujo completo desde login hasta visualización de datos reales.
