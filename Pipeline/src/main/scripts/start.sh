#!/bin/bash
if [ ! -d "data" ]; then
    echo "data and log directories of package are not pointing to golden source. running createLinksToDataFiles script"
  ./createLinksToDataFiles.sh $*
  if [ "$?" -ne 0 ]; then
  	echo "Failed to execute createLinksToDataFiles.sh!" 1>&2
  	exit 1
  fi
fi

PROCESS_NUM=$(ps -ef | grep "process.name=skroll" | grep -v "grep" | wc -l)
if [ $PROCESS_NUM -eq 0 ];
        then
                echo "starting skroll server..."
                sudo nohup java -Xms4g -Xmx4g -XX:MaxGCPauseMillis=500 -cp .:config/:Pipeline-all-1.0.jar -Dlog4j.configuration=config/log4j.xml -Dprocess.name=skroll com.skroll.rest.WebServer --port 8080 --baseuri webapp 3>&1 1>/dev/null 2>&3- | tee logs/skroll.log&

        else
                echo "Skroll Server is already running. Stop the server first and then run start.sh again"
                return 0
        fi

