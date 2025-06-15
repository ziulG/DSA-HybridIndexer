#!/bin/bash

# Verificar se o programa foi compilado
if [ ! -d "out" ]; then
    echo "Projeto não compilado. Executando compilação..."
    ./compile.sh
    if [ $? -ne 0 ]; then
        echo "Erro na compilação. Abortando."
        exit 1
    fi
fi

# Executar o programa principal
echo "Iniciando DSA-HybridIndexer..."
echo ""
java -cp out main.java.br.com.hybridIndexer.Main