# 🔧 Comandos Útiles para Debugging

## Backend (ASP.NET)

### Iniciar Backend
```powershell
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run
```

### Verificar que el puerto esté escuchando
```powershell
netstat -ano | findstr :5192
```

### Ver logs del backend en tiempo real
```powershell
# El output de 'dotnet run' muestra los logs automáticamente
# Observa las peticiones HTTP que llegan
```

### Probar endpoints con curl (desde PowerShell)

**Login:**
```powershell
$body = @{
    username = "tu_usuario"
    password = "tu_password"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:5192/api/Usuarios/authenticate" -Method POST -Body $body -ContentType "application/json"
```

**Dashboard Data (con token):**
```powershell
$headers = @{
    Authorization = "Bearer TU_TOKEN_AQUI"
}

Invoke-RestMethod -Uri "http://localhost:5192/api/Dashboard/sla/data?slaCode=SLA1" -Method GET -Headers $headers
```

**Config SLA:**
```powershell
$headers = @{
    Authorization = "Bearer TU_TOKEN_AQUI"
}

Invoke-RestMethod -Uri "http://localhost:5192/api/ConfigSla" -Method GET -Headers $headers
```

---

## Android App

### Ver logs en Logcat (Android Studio)

**Filtrar por tag:**
```
Tag: System.out
Tag: okhttp.OkHttpClient  (para ver peticiones HTTP)
```

**Filtrar por texto:**
```
Search: "Authorization"  (para ver headers)
Search: "Response"       (para ver respuestas del backend)
Search: "Error"          (para ver errores)
```

### Ver peticiones HTTP en Logcat

Gracias al `HttpLoggingInterceptor` en `ApiClient.kt`, verás:
```
--> POST /api/Usuarios/authenticate
Content-Type: application/json
{"username":"...","password":"..."}
--> END POST

<-- 200 OK
{"token":"...","usuario":{...}}
<-- END HTTP
```

### Debug de SharedPreferences

Para verificar que el token se guardó:
```kotlin
// Agregar temporalmente en StatisticsViewModel init:
Log.d("TOKEN_DEBUG", "Token: ${ApiClient.getTokenManager()?.getToken()}")
Log.d("TOKEN_DEBUG", "UserId: ${ApiClient.getTokenManager()?.getUserId()}")
```

---

## Verificar Conectividad

### Desde Emulador a tu PC

**Ping a tu backend:**
```powershell
# En tu PC, verifica que el backend esté escuchando en todas las interfaces
# (no solo localhost)
```

**Si el emulador no puede conectar a 10.0.2.2:5192:**
1. Verifica que el backend esté corriendo
2. Verifica que no haya firewall bloqueando el puerto 5192
3. Usa `localhost` en vez de `0.0.0.0` en el backend

### Desde Dispositivo Físico a tu PC

**Obtener tu IP local:**
```powershell
ipconfig
# Busca "IPv4" en "Adaptador de LAN inalámbrica Wi-Fi"
# Ejemplo: 192.168.1.100
```

**Verificar que el dispositivo puede hacer ping:**
```powershell
# En tu PC
ping TU_IP_LOCAL
```

**Verificar que el puerto esté abierto:**
```powershell
# En tu PC
Test-NetConnection -ComputerName TU_IP_LOCAL -Port 5192
```

**Si no conecta:**
1. Verifica que PC y dispositivo estén en la misma red WiFi
2. Desactiva temporalmente el firewall de Windows
3. Verifica que el backend esté escuchando en `0.0.0.0:5192` (no solo `localhost`)

---

## Troubleshooting Común

### Error: "Unable to resolve host"

**Causa:** El dispositivo no puede llegar al backend

**Solución:**
```powershell
# 1. Verifica que el backend esté corriendo
dotnet run

# 2. Verifica la IP en ApiClient.kt
# Emulador: http://10.0.2.2:5192/
# Dispositivo: http://TU_IP_LOCAL:5192/

# 3. Verifica firewall
New-NetFirewallRule -DisplayName "ASP.NET Backend" -Direction Inbound -Protocol TCP -LocalPort 5192 -Action Allow
```

---

### Error: "401 Unauthorized"

**Causa:** El token no está siendo enviado o es inválido

**Debug:**
```kotlin
// En StatisticsRepository, agregar logs temporalmente:
Log.d("API_DEBUG", "Token: ${ApiClient.getTokenManager()?.getToken()}")

// En ApiClient, verificar que el interceptor esté agregando el header
```

**Solución:**
1. Verifica que el login haya sido exitoso
2. Verifica que `TokenManager.saveToken()` se esté llamando
3. Verifica que `SharedPreferences` tenga el token guardado

---

### Error: "Credenciales incorrectas"

**Causa:** Usuario/contraseña no existen en la BD o endpoint no funciona

**Debug:**
```powershell
# Probar endpoint directamente con curl
$body = @{
    username = "tu_usuario"
    password = "tu_password"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:5192/api/Usuarios/authenticate" -Method POST -Body $body -ContentType "application/json"
```

**Solución:**
1. Verifica las credenciales en PostgreSQL
2. Verifica que el endpoint `/api/Usuarios/authenticate` esté funcionando
3. Revisa los logs del backend para ver errores

---

### No se muestran datos en Estadísticas

**Causa:** No hay datos en la BD para los filtros aplicados

**Debug:**
```sql
-- Conectar a PostgreSQL y verificar datos
SELECT * FROM "Solicitud" LIMIT 10;
SELECT * FROM "ConfigSla";
```

**Solución:**
1. Cambia los filtros (fecha más amplia, SLA = "Todos")
2. Verifica que haya solicitudes en la base de datos
3. Revisa Logcat para ver la respuesta del backend

---

## Logs Útiles para Debugging

### En LoginViewModel
```kotlin
Log.d("LOGIN", "Attempting login for user: $username")
Log.d("LOGIN", "Login response: ${res.token}")
Log.d("LOGIN", "Token saved: ${ApiClient.getTokenManager()?.getToken()}")
```

### En StatisticsViewModel
```kotlin
Log.d("STATS", "Loading dashboard data with filters: sla=$slaCode, dates=$startDate to $endDate")
Log.d("STATS", "Dashboard data size: ${data.size}")
Log.d("STATS", "Calculated KPIs - Cumplimiento: $cumplimiento%, Total: $total")
```

### En ApiClient
```kotlin
// Ya configurado con HttpLoggingInterceptor.Level.BODY
// Muestra automáticamente todas las peticiones y respuestas en Logcat
```

---

## Resetear Estado de la App

### Limpiar token guardado
```kotlin
// En LoginViewModel o donde sea necesario
ApiClient.getTokenManager()?.clearToken()
```

### Limpiar SharedPreferences completo
```powershell
# En Android Studio Terminal:
adb shell
run-as dev.esandamzapp.slatrackerapp
cd shared_prefs
rm sla_tracker_prefs.xml
exit
exit
```

### Reinstalar app completa
```powershell
# Desinstalar
adb uninstall dev.esandamzapp.slatrackerapp

# Instalar de nuevo desde Android Studio
# Run → Clean → Rebuild → Run
```

---

## Monitoreo en Tiempo Real

### Ver todas las peticiones HTTP
En Logcat, filtrar por: `okhttp.OkHttpClient`

### Ver respuestas del servidor
Buscar en Logcat: `<-- 200 OK` o `<-- 401 Unauthorized`

### Ver errores de red
Buscar en Logcat: `IOException` o `SocketTimeoutException`

---

## 🎯 Checklist de Debugging

Cuando algo no funcione, verificar en orden:

- [ ] Backend corriendo (`dotnet run`)
- [ ] Puerto 5192 escuchando (`netstat -ano | findstr :5192`)
- [ ] IP correcta en `ApiClient.kt` (10.0.2.2 para emulador, IP local para dispositivo)
- [ ] Login exitoso (token guardado en SharedPreferences)
- [ ] Headers con Authorization en peticiones (ver Logcat)
- [ ] Respuesta del backend exitosa (código 200)
- [ ] Datos en la base de datos que coincidan con filtros

---

¡Con estos comandos puedes debugear cualquier problema que surja!
