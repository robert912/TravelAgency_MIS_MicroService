## Parte 1: Instalar kubectl (El cliente de Kubernetes)
kubectl es la herramienta de comandos que necesitas para controlar Kubernetes (la usarás en el Paso 4 para aplicar los archivos .yaml).

1. Abre PowerShell como Administrador (Clic derecho en el menú de inicio > Terminal Administrador o PowerShell Administrador).

2. Descarga la última versión estable ejecutando este comando:

    ```
    curl.exe -LO "https://dl.k8s.io/release/v1.30.0/bin/windows/amd64/kubectl.exe"
    ```
3. Crea una carpeta en tu disco `C:` para guardar tus herramientas de sistema:
   ```
   mkdir C:\k8s
   ```
4. Mueve el archivo kubectl.exe a esa carpeta:
    ```
    move kubectl.exe C:\k8s\
    ```

---

## Parte 2: Instalar Minikube

¡Ojo! La guía pide la versión **1.32 o superior**. Vamos a descargar el instalador oficial.

1. En la misma terminal de Administrador, descarga el instalador de Minikube:
    ```
    curl.exe -LO https://storage.googleapis.com/minikube/releases/latest/minikube-installer.exe
    ```
2. Ejecuta el instalador que acabas de descargar:
    ```
    .\minikube-installer.exe
    ```
3. Sigue el asistente de instalación (es el típico *Siguiente, Siguiente, Instalar*). Esto guardará automáticamente `minikube.exe` en tu sistema.

---

## Parte 3: Configurar las Variables de Entorno (El "Path")

Para que puedas escribir `minikube` o `kubectl` desde cualquier carpeta, debemos avisarle a Windows dónde están guardados.

1. Abre el menú de inicio, escribe **Variables de entorno** y selecciona **Editar las variables de entorno del sistema**.
2. Haz clic en el botón **Variables de entorno...** (abajo a la derecha).
3. En la sección inferior ("Variables del sistema"), busca la variable llamada **Path** y hazle doble clic.
4. Haz clic en **Nuevo** y añade la ruta de kubectl:
   > `C:\k8s`
5. *(Opcional)* Revisa si la instalación de Minikube ya añadió su propia ruta (suele ser `C:\Program Files\minikube`). Si no la ves, dale a **Nuevo** y añádela manualmente.
6. Haz clic en **Aceptar** en las tres ventanas abiertas para guardar los cambios.

---

## Parte 4: ¡Verificación Final!

**Cierra por completo tu PowerShell actual y abre uno nuevo** (para que cargue las nuevas variables de entorno). Ejecuta estos comandos para comprobar que todo está listo:

```
kubectl version --client
```
```
minikube version
```