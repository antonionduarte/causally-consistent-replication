FROM openjdk:17

COPY target/peersim.jar peersim.jar
COPY config/c3/* config/c3/
COPY config/saturn/* config/saturn/
COPY config/graphs/* config/graphs/
COPY config/latencies/* config/latencies/
COPY config/partitions/* config/partitions/

COPY entrypoint.sh entrypoint.sh

RUN ["chmod", "+x", "entrypoint.sh"]
ENTRYPOINT ["./entrypoint.sh"]
