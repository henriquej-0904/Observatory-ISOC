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
### Configuration

A configuração do sistema está dividida em 3 ficheiros: (`docker/defaults.env`, `docker/host.env` and `docker/local.env`).

O ficheiro (`docker/defaults.env`) não deve ser modificado, pois contém configurações padrão.
O ficheiro (`docker/host.env`) configura o domínio acessível por DNS e pode ser configurado através do seguinte comando: 
```bash
INTERNETNL_DOMAINNAME=example.com \
IPV4_IP_PUBLIC=127.0.0.1 \
IPV6_IP_PUBLIC=::1 \
envsubst < docker/host-dist.env > docker/host.env
```
Ao configurar o domínio torna possível a aquisição de um certificado Let's Encrypt que é obtido de forma automática. 

Por fim, o ficheiro (`docker/local.env`) permite configurar parâmetros locais necessários à máquina onde está instalado o software, tais como:
- `ENABLE_BATCH`: Definir com valor `True`, para ativar batch API
- `ENABLE_HOF`: Definir com valor `False`, para desativar Hall of Fame pois pode entrar em conflito com batch API
- `BATCH_AUTH`: Lista de pares `user:password` separada por vírgulas que define os utilizadores com acesso à funcionalidade Batch API

Opcionalmente podem ser definidos os seguintes parâmetros:

- `MONITORING_AUTH`: Lista de pares `user:password` separada por vírgulas que define os utilizadores com acesso à funcionalidade de métricas do software disponível em: `https://example.com/grafana/`.
- `BASIC_AUTH`, `BASIC_AUTH_RAW` e `ALLOW_LIST`: Restringe acesso à página de scan de um domínio (não disponível quando Batch API é ativado)

Exemplo de configuração:
```bash
cat >> docker/local.env <<EOF
ENABLE_BATCH=True
ENABLE_HOF=False
# user/password(s) for authenticating against Batch API
BATCH_AUTH=user:welkom01
# user/password(s) for access to /grafana monitoring
MONITORING_AUTH=user:welkom01
# user/password(s) for access to web interface
BASIC_AUTH=user:welkom01
# allowed IP's to visit web interface without password
ALLOW_LIST=198.51.100.1,2001:db8:2::1
EOF
```

## Iniciar/Parar serviço

Para facilidade, foram criados scripts que permitem iniciar (`start-internetnl.sh`) e parar o serviço (`stop-internetnl.sh`).

Depois de o serviço ser iniciado, o seguinte comando deve ser executado para criar os índices necessários na base de dados:
```bash
docker compose --project-name=internetnl-prod exec app ./manage.py api_create_db_indexes
```

