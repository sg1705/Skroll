# To run the command line utility.
java -cp .:config/:Pipeline-all-1.0.jar -Dlog4j.configuration=config/log4j.xml -Dprocess.name=skrollTrainer com.skroll.trainer.Trainer $*