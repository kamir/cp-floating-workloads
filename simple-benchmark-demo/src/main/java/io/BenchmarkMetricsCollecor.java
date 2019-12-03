package io;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.TopicExistsException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class BenchmarkMetricsCollecor {


    // Create topic in Confluent Cloud
    public static void createTopic(final String topic,
                                   final int partitions,
                                   final int replication,
                                   final Properties cloudConfig) {
        final NewTopic newTopic = new NewTopic(topic, partitions, (short) replication);
        try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
            else {
                System.out.println("The topic: " + topic + " is already there.");
            }
        }
    }

    public static void main(String[] args) throws Exception {

          if (args.length != 2) {
            System.out.println("Please provide command line arguments: configPath inputFile");
            System.exit(1);
          }



    // Load properties from a configuration file
    // The configuration properties defined in the configuration file are assumed to include:
    //   ssl.endpoint.identification.algorithm=https
    //   sasl.mechanism=PLAIN
    //   bootstrap.servers=<CLUSTER_BOOTSTRAP_SERVER>
    //   sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<CLUSTER_API_KEY>" password="<CLUSTER_API_SECRET>";
    //   security.protocol=SASL_SSL
//    final Properties props = loadConfig("cfg/ccloud-cfg.properties");
    final Properties props = loadConfig( args[0] );

          System.out.println( props );

    // Create topic if needed
    String topic = (String)props.get("topic.name");

    String key = args[1];

        System.out.println( "Topic : " + topic );
        System.out.println( "Key   : " + key );

        createTopic(topic, 1, 1, props);

    // Add additional properties.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<String, String>(props);

        BenchmarkResultRecord record = new BenchmarkResultRecord( args[1] );

        System.out.printf("Producing record: %s\n%s%n", key, record.asString());

        producer.send(new ProducerRecord<String, String>(topic, key, record.asString()), new Callback() {
            @Override
            public void onCompletion(RecordMetadata m, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                }
            }
        });


    producer.flush();

    System.out.printf( "1 message were produced to topic %s%n", topic);

    producer.close();

}

    public static Properties loadConfig(final String configFile) throws IOException {
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
