# Build internet.nl-python-scripts and java environment

FROM python:3.7

# Update and install
RUN apt update && \
apt full-upgrade -y && \
apt install -y nano openjdk-11-jre && \
rm -r /var/lib/apt/lists/* && \
pip install pip-tools

WORKDIR /app
COPY internet.nl-python-scripts/ /app/internet.nl-python-scripts

# Install python dependencies
WORKDIR /app/internet.nl-python-scripts
RUN pip-compile requirements.in && \
pip install -r requirements.txt

# COPY observatory java script
WORKDIR /app
COPY target/observatory-1.0-jar-with-dependencies.jar observatory.jar