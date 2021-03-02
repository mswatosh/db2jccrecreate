FROM ibmjava:8

USER root

RUN apt-get update
RUN apt-get install -y krb5-user libpam-krb5 libpam-ccreds auth-client-config
RUN chmod 777 /etc
RUN mkdir /etc/krb5
RUN printf "add_entry -password -p dbuser@EXAMPLE.COM -k 1 -e aes256-cts\npassword\nwkt /etc/krb5.keytab" | ktutil
ADD docker-entrypoint.sh /
RUN chmod a+x /docker-entrypoint.sh

COPY /build/libs/app.jar /app.jar
COPY /build/dependencies/db2jcc.jar /db2jcc.jar
COPY /src/main/resources/jcc.properties /jcc.properties

ENTRYPOINT ["/docker-entrypoint.sh"]