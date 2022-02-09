# Build internet.nl-python-scripts

FROM python:3.7

# Update and install
RUN apt update && \
apt full-upgrade -y && \
apt install -y nano && \
rm -r /var/lib/apt/lists/* && \
pip install pip-tools

# Clone python-scripts repository
WORKDIR /
RUN git clone "https://github.com/poorting/internet.nl_batch_scripts.git"

# Install dependencies
WORKDIR /internet.nl_batch_scripts
RUN pip-compile requirements.in && \
pip install -r requirements.txt
