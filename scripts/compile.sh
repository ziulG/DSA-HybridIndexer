#!/bin/bash

echo "==================================="
echo "Compilando DSA-HybridIndexer..."
echo "==================================="

# Criar diretório de saída se não existir
mkdir -p out

# Compilar todos os arquivos Java
echo "Compilando arquivos Java..."
javac -d out -cp src src/main/java/br/com/hybridIndexer/model/*.java \
              src/main/java/br/com/hybridIndexer/datastructures/*.java \
              src/main/java/br/com/hybridIndexer/util/*.java \
              src/main/java/br/com/hybridIndexer/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilação concluída com sucesso!"
    echo ""
    echo "Para executar o programa:"
    echo "  java -cp out main.java.br.com.hybridIndexer.Main"
    echo ""
    echo "Para gerar datasets de teste:"
    echo "  java -cp out main.java.br.com.hybridIndexer.util.DatasetGenerator"
else
    echo "✗ Erro na compilação!"
    exit 1
fi