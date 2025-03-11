@echo off
REM Crear el directorio de salida si no existe
if not exist bin (
    mkdir bin
)

REM Compilar los archivos Java
javac -d bin -cp lib\* src\*.java

echo Compilacion completada.