# ✅ SOLUCIÓN - Error de Conexión Dispositivo Físico

## 🔴 Problema Encontrado

```
Failed to connect to /10.0.2.2 (port 5192) from /192.168.1.100 (port 37858) after 10000ms
```

### Causas:
1. ❌ **IP incorrecta**: `10.0.2.2` solo funciona para **emuladores**, no para dispositivos físicos
2. ❌ **Backend escuchando solo en localhost**: No permitía conexiones desde la red local
3. ❌ **Firewall bloqueando**: Puerto 5192 no tenía regla de entrada

---

## ✅ SOLUCIÓN APLICADA

### 1. **Actualizar IP en ApiClient.kt**

**Antes:**
```kotlin
private const val BASE_URL = "http://10.0.2.2:5192/"  // ❌ Solo emuladores
```

**Después:**
```kotlin
private const val BASE_URL = "http://192.168.10.246:5192/"  // ✅ Tu IP local
```

### 2. **Configurar Backend para Escuchar en Red Local**

**Archivo:** `launchSettings.json`

**Antes:**
```json
"applicationUrl": "http://localhost:5192"  // ❌ Solo localhost
```

**Después:**
```json
"applicationUrl": "http://0.0.0.0:5192"  // ✅ Todas las interfaces
```

### 3. **Crear Regla de Firewall**

```powershell
New-NetFirewallRule -DisplayName "ASP.NET Backend - Port 5192" -Direction Inbound -Protocol TCP -LocalPort 5192 -Action Allow
```

### 4. **Reiniciar Backend**

```powershell
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run --launch-profile http
```

**Verificar que muestre:**
```
Now listening on: http://0.0.0.0:5192  ✅
```

---

## 🧪 Verificación

### 1. Backend escuchando correctamente:
```powershell
netstat -ano | findstr :5192
```
**Debe mostrar:**
```
TCP    0.0.0.0:5192           0.0.0.0:0              LISTENING       18212
```

### 2. Probar endpoint desde PC:
```powershell
curl http://192.168.10.246:5192/api/ConfigSla
# Debe responder (401 es normal sin token)
```

---

## 📱 Ahora en tu Tablet Android

### 1. **Rebuild la App**
- Android Studio → Build → Clean Project
- Build → Rebuild Project

### 2. **Run en Tablet**
- Conecta tablet por USB
- Run (Shift + F10)

### 3. **Probar Login**
- Usuario: tu usuario de BD
- Contraseña: tu contraseña de BD
- Debe conectar correctamente a **192.168.10.246:5192**

---

## 🔍 Si Aún No Funciona

### Verificar que PC y Tablet estén en la misma red WiFi

**En tu PC:**
```powershell
ipconfig
# Busca "Dirección IPv4" en "Adaptador de LAN inalámbrica Wi-Fi"
# Tu IP: 192.168.10.246
```

**En tu Tablet:**
- Configuración → WiFi → Ver red conectada
- Debe estar en la red **192.168.10.x**

### Probar ping desde Tablet a PC

Instala una app de terminal en tu tablet (ej: Termux) y ejecuta:
```bash
ping 192.168.10.246
```

Si no responde, el problema es de red (no firewall/backend).

### Verificar Firewall de Windows

```powershell
Get-NetFirewallRule -DisplayName "ASP.NET Backend - Port 5192"
```

Si no aparece, crear manualmente en Windows Defender Firewall:
1. Firewall de Windows Defender → Configuración avanzada
2. Reglas de entrada → Nueva regla
3. Puerto → TCP → 5192 → Permitir conexión

---

## 📊 Configuración Final

### Para EMULADOR:
```kotlin
private const val BASE_URL = "http://10.0.2.2:5192/"
```

### Para DISPOSITIVO FÍSICO:
```kotlin
private const val BASE_URL = "http://192.168.10.246:5192/"
```

### Backend siempre debe escuchar en:
```json
"applicationUrl": "http://0.0.0.0:5192"
```

---

## ✅ Estado Actual

- ✅ Backend escuchando en `0.0.0.0:5192`
- ✅ ApiClient configurado con IP `192.168.10.246`
- ✅ Regla de firewall creada
- ✅ Backend corriendo en nueva ventana de PowerShell

**¡Ahora puedes probar en tu tablet!** 🎉

---

## 🔄 Para Futuras Sesiones

Cada vez que trabajes con dispositivo físico:

1. **Iniciar backend:**
```powershell
cd "c:\DESARROLLO DE APLICACIONES WEB\PROYECTO FINAL\API.backend.singula\API.backend.singula"
dotnet run --launch-profile http
```

2. **Verificar tu IP actual** (puede cambiar):
```powershell
ipconfig
```

3. **Si la IP cambió**, actualizar `ApiClient.kt`

4. **Run app en tablet**
