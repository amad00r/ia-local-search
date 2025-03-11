@echo off
REM Eliminar los archivos .class en el directorio bin\redsensores
if exist bin\redsensores (
    del /Q bin\redsensores\*.class
    echo Archivos .class eliminados en bin\redsensores.
) else (
    echo El directorio bin\redsensores no existe.
)