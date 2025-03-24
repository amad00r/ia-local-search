@echo off
REM Ejecutar los experimentos Java con argumentos
java -cp "bin;lib\*" redsensores.ExperimentsMain %*
echo Programa ejecutado.