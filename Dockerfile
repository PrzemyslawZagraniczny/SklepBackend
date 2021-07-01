FROM ubuntu:18.04

ENV TZ=Europe/Warsaw

RUN apt-get update && apt-get install -y openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
ENV PATH $JAVA_HOME/bin:$PATH

#WGET CURL GIT GNUPG
RUN apt-get update && apt-get install -y wget curl git gnupg

#SCALA
RUN wget https://www.scala-lang.org/files/archive/scala-2.12.12.deb &&\
	dpkg -i scala-2.12.12.deb &&\
	rm scala-2.12.12.deb &&\
	apt-get update &&\
	apt-get install -y scala

EXPOSE 3000
EXPOSE 9000

#SBT

#RUN wget https://scala.jfrog.io/artifactory/debian/sbt-1.4.9.deb &&\
#	dpkg -i sbt-1.5.9.deb && apt-get update &&\
#	rm sbt-1.4.9.deb &&\
#	apt-get install -y sbt

RUN	curl -sL -o sbt.deb https://scala.jfrog.io/artifactory/debian/sbt-1.4.9.deb &&\
    dpkg -i sbt.deb &&\
    apt-get update &&\
    apt-get install -y sbt

#NODE
RUN apt-get update &&\
	curl -sL https://deb.nodesource.com/setup_14.x  | bash - &&\
	apt-get install -y nodejs && npm install

