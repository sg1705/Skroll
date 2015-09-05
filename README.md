## How run Skroll locally


```
git clone https://github.com/sg1705/Skroll
gradlew compileJava
gradlew trainWithOverride
gradlew start
```

For following four commands below, the default folder is  "build/resources/main/preEvaluated/".
you don't have to provide the -f option, if you are using the default folder.

## To annotate Doc type of files of a folder specified by -f, from command line

```
./gradlew trainer -Dexec.args="-c annotateDocType -f build/resources/main/preEvaluated/ -t 101 -w 1f"
```

## To train Doc type Model from a folder containing training files

```
./gradlew trainer -Dexec.args="-c trainDocTypeModel -f build/resources/main/preEvaluated/"
```

## How to train from command line

```
./gradlew trainer -Dexec.args="-c trainWithWeight -f build/resources/main/preEvaluated/"

```

## How to classify doctype from command line

```
./gradlew trainer -Dexec.args="-c classifyDocType -f build/resources/main/preEvaluated/"

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