FROM codenvy/ubuntu_jdk8

# NGINX SETUP TAKEN FROM THE https://github.com/nginxinc/docker-nginx.git OFFICIAL IMAGE FOR CLIENT SIDE
ENV NGINX_VERSION 1.9.9-1~jessie

USER root 
# GET entry point script running server and client binaries
COPY ./atm-startup.sh /tmp/
RUN whoami && ls -l /tmp/atm-startup.sh && chmod +x /tmp/atm-startup.sh

RUN apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62
RUN echo "deb http://nginx.org/packages/mainline/debian/ jessie nginx" >> /etc/apt/sources.list

# server ports (8080 for server and 80/443 for nginx)
EXPOSE 8080 80 443

RUN apt-get update && \
    apt-get install -y ca-certificates nginx=${NGINX_VERSION} && \
    rm -rf /var/lib/apt/lists/*

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log
RUN ln -sf /dev/stderr /var/log/nginx/error.log

VOLUME ["/var/cache/nginx"]

# NGINX STARTUP IN THE ENTRYPOINT CMD ["nginx", "-g", "daemon off;"]

# GET ATM server/client code
COPY ./bluebank-atm-ATMSERVER.zip /tmp/

# extract the war to run server/UI in standalone
RUN rm -rf /tmp/ATM-SERVER && mkdir -p /tmp/ATM-SERVER && cd /tmp/ATM-SERVER && unzip /tmp/bluebank-atm-ATMSERVER.zip

ENTRYPOINT [ "/tmp/atm-startup.sh" ]
