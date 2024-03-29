/**
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import org.apache.kafka.clients.consumer.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

public class BenchmarkMetricsConsumer {

  public static void main(final String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Please provide command line arguments: configPath");
      System.exit(1);
    }

    // Load properties from a configuration file
    // The configuration properties defined in the configuration file are assumed to include:
    //   ssl.endpoint.identification.algorithm=https
    //   sasl.mechanism=PLAIN
    //   bootstrap.servers=<CLUSTER_BOOTSTRAP_SERVER>
    //   sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<CLUSTER_API_KEY>" password="<CLUSTER_API_SECRET>";
    //   security.protocol=SASL_SSL
    final Properties props = loadConfig(args[0]);

    String topic = (String)props.get("topic.name");

    // Add additional properties.
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-" + System.currentTimeMillis());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
    consumer.subscribe(Arrays.asList(topic));

    Long total_count = 0L;

    try {
      while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
          String key = record.key();
          String value = record.value();

          System.out.printf("Consumed record with key: %s. \n", key);
        }
      }
    } finally {
      consumer.close();
    }
  }


  public static Properties loadConfig(String configFile) throws IOException {
    if (!Files.exists(Paths.get(configFile))) {
      throw new IOException(configFile + " not found.");
    }
    final Properties cfg = new Properties();
    try (InputStream inputStream = new FileInputStream(configFile)) {
      cfg.load(inputStream);
    }
    return cfg;
  }

}
