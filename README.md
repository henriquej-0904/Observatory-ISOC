# Scripts Observatório ISOC PT

## Observatório de Tecnologias da Internet Portuguesa

Os scripts disponibilizados neste repositório são utilizados pelo **Observatório de Tecnologias da Internet Portuguesa** cujo objetivo consiste em aferir o grau de utilização de diferentes tecnologias pela Internet Portuguesa.

## Instruções de compilação do programa

O programa pode ser executado em dois modos: localmente ou utilizando a plataforma Docker.

## Instalação local

Para se proceder a uma instalação local, é necessário Java 11 (JDK) e Maven para compilar.

Na pasta [observatory](https://github.com/henriquej-0904/Scripts-ISOC/tree/main/observatory), deve-se executar o seguinte comando para compilar o programa:
```bash
mvn clean compile assembly:single
```
De seguida, o programa pode ser executado através do comando:
```bash
java -jar target/observatory-v1.2-jar-with-dependencies.jar
```

## Instalação docker

Em 1º lugar deve-se criar a imagem docker através do script: [buildDocker-sh](https://github.com/henriquej-0904/Scripts-ISOC/blob/main/observatory/buildDocker.sh).

De seguida, o programa pode ser executado através do script: [runDocker.sh](https://github.com/henriquej-0904/Scripts-ISOC/blob/main/observatory/runDocker.sh).


## Instruções de utilização

O programa suporta 2 comandos:
- test -> Execução dos testes a listas de domínios.
- report -> Criação de um relatório baseado em resultados obtidos.

Para mais informações sobre os comandos suportados e respectivas opções, deve-se consultar o ficheiro [programa-e-parametros.docx](https://github.com/henriquej-0904/Scripts-ISOC/blob/main/observatory/programa-e-parametros.docx).
