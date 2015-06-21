## How run Skroll locally


```
git clone https://github.com/sg1705/Skroll
gradlew compileJava
gradlew trainWithOverride
gradlew start
```

## How to train from command line

```
gradlew trainWithWeight
```

## How to run benchmark from command line

```
gradlew runBenchmark
```



## How run Skroll in AWS

Create an Ubuntu image

Edit Security Group to accept all traffic to start with
Setup java

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
java -version


Download git

sudo apt-get install git

git clone https://github.com/sg1705/Skroll
sudo apt-get install phantomjs
cd Skroll
./gradlew start