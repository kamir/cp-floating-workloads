#!/bin/sh

#  
# She Kafka distribution provides a benchmark tool to generate a sample data flow.
#
# Some build in metrics are used for simple analysis. Especially, when workload placement
# is based on latency, we would expect to see a difference in the benchmark result, once
# the workload placement is done in a different cluster, because a different latency is 
# expected.
#
#
/usr/bin/kafka-producer-perf-test --topic test_flwl_01 --num-records 100 --payload-delimiter "|" --record-size 10000 --throughput 100 --print-metrics --producer.config ./../simple-benchmark-demo/cfg/ccloud-cfg.properties
