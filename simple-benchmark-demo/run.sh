mvn clean compile package

docker login

docker build . -t flwl_demo_01

docker run -t flwl_demo_01 > flwl_demo_01.dat

java -cp target/simple-demo-1.0-SNAPSHOT.jar io.BenchmarkMetricsCollecor cfg/ccloud-cfg.properties flwl_demo_01

java -cp target/simple-demo-1.0-SNAPSHOT.jar io.BenchmarkMetricsConsumer cfg/ccloud-cfg.properties
