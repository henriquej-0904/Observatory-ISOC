# Scripts Observatório ISOC PT

## Observatório de Tecnologias da Internet Portuguesa

Os scripts disponibilizados neste repositório são utilizados pelo **Observatório de Tecnologias da Internet Portuguesa** cujo objetivo consiste em aferir o grau de utilização de diferentes tecnologias pela Internet Portuguesa.

## Instruções de compilação do programa

O programa pode ser executado em dois modos: localmente ou utilizando a plataforma Docker.

## Instalação local

Para se proceder a uma instalação local, é necessário Java 19 (JDK) e Maven para compilar.

Na pasta [observatory](./observatory), deve-se executar o seguinte comando para compilar o programa:
```bash
mvn clean compile assembly:single
```
De seguida, o programa pode ser executado através do comando:
```bash
java -jar target/observatory-jar-with-dependencies.jar
```

## Instalação docker

O script [runDocker.sh](./observatory/runDocker.sh) permite executar a imagem mais recente (latest) disponível no [Docker Hub](https://hub.docker.com/r/henriquej0904/observatory-isoc).


## Instruções de utilização

O programa suporta 2 comandos:
- test -> Execução dos testes a listas de domínios.
- report -> Criação de um relatório baseado em resultados obtidos.

Para mais informações sobre os comandos suportados e respectivas opções, deve-se consultar o ficheiro [especificação.pdf](./observatory/documentation/especificação.pdf).
