# Internet.nl Software

O Observatório utiliza o software [Internet.nl](https://github.com/internetstandards/Internet.nl) para a execução de testes que visam identificar os domínios da internet que estão de acordo (em compliance) com os standards da Internet.
O software pode ser utilizado/configurado de 2 formas diferentes:
 - Disponibilização de página Web que permiite qualquer utilizador efetuar os referidos testes para um domínio especificado;
 - Configuração de um serviço interno (não disponível ao público) que permite um utilizador com autorização submeter um conjunto de domínios (funcionalidade de Batching) para serem testados assíncronamente.

Neste caso, é utilizada a 2ª opção, o qual é explicada a seguir.

## Internet.nl Software Setup for Batch Requests

A versão Batch do software pode ser instalada através das plataformas Docker e Docker Compose.
Para informações detalhadas sobre o deployment do software, consultar página oficial [aqui](https://github.com/internetstandards/Internet.nl/blob/main/documentation/Docker-deployment-batch.md).

A instalação de Docker pode ser consultada [aqui](https://docs.docker.com/desktop/install/linux-install/).
Um dos testes efetuados pelo software é a disponibilização de IPv6 por parte do domínio a ser testado. Desta forma, é necessária a ativação de IPv6 no Docker, que pode ser configurado através do comando:
```bash
echo '{"experimental": true, "ip6tables": true, "live-restore": true}' > /etc/docker/daemon.json
systemctl stop docker
systemctl start docker
```

De seguida, fazer clone do repositório:
```bash
RELEASE=main && \
mkdir -p /opt/Internet.nl/docker && \
cd /opt/Internet.nl/ && \
curl -sSfO --output-dir docker https://raw.githubusercontent.com/internetstandards/Internet.nl/${RELEASE}/docker/defaults.env && \
curl -sSfO --output-dir docker https://raw.githubusercontent.com/internetstandards/Internet.nl/${RELEASE}/docker/host-dist.env && \
curl -sSfO --output-dir docker https://raw.githubusercontent.com/internetstandards/Internet.nl/${RELEASE}/docker/docker-compose.yml && \
touch docker/local.env
```

