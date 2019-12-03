mvn clean compile package

# docker login

sudo docker build . -t flwl_demo_01

sudo docker run -t flwl_demo_01 > flwl_demo_01.dat

