@echo off
echo ===================================
echo Compilando DSA-HybridIndexer...
echo ===================================

REM Criar diretorio de saida se nao existir
if not exist out mkdir out

REM Compilar todos os arquivos Java
echo Compilando arquivos Java...
javac -d out -cp src src\main\java\br\com\hybridIndexer\model\*.java ^
              src\main\java\br\com\hybridIndexer\datastructures\*.java ^
              src\main\java\br\com\hybridIndexer\util\*.java ^
              src\main\java\br\com\hybridIndexer\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilacao concluida com sucesso!
    echo.
    echo Para executar o programa:
    echo   java -cp out main.java.br.com.hybridIndexer.Main
    echo.
    echo Para gerar datasets de teste:
    echo   java -cp out main.java.br.com.hybridIndexer.util.DatasetGenerator
) else (
    echo.
    echo Erro na compilacao!
    exit /b 1
)