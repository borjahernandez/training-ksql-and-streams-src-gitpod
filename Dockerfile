FROM gitpod/workspace-full:latest

# Install Confluent Platform 6.2.1
RUN sudo wget --quiet -P /home/gitpod/ http://packages.confluent.io/archive/6.2/confluent-community-6.2.1.tar.gz \
        && sudo tar xzf /home/gitpod/confluent-community-6.2.1.tar.gz \
        && sudo rm /home/gitpod/confluent-community-6.2.1.tar.gz

# Setup PATH env variable
ENV PATH="/home/gitpod/confluent-6.2.1/bin:$PATH"

# # Install Tailscale
# USER root
# RUN curl -fsSL https://pkgs.tailscale.com/stable/ubuntu/focal.gpg | sudo apt-key add - \
#      && curl -fsSL https://pkgs.tailscale.com/stable/ubuntu/focal.list | sudo tee /etc/apt/sources.list.d/tailscale.list \
#      && apt-get update \
#      && apt-get install -y tailscale
