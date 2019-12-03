From confluentinc/cp-kafka

RUN mkdir -p /usr/local/floating_app/
RUN chmod 777 /usr/local/floating_app/

COPY ./cfg/ccloud-cfg.properties /usr/local/floating_app/ccloud-cfg.properties

WORKDIR /usr/local/floating_app

RUN ls

CMD [ "/usr/bin/kafka-producer-perf-test", "--topic", "test_flwl_01", "--num-records", "100", "--payload-delimiter", "|", "--record-size", "10000" , "--throughput", "100", "--print-metrics", "--producer.config", "/usr/local/floating_app/ccloud-cfg.properties" ]

