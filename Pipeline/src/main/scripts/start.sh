#!/bin/bash

PROCESS_NUM=$(ps -ef | grep "process.name=skroll" | grep -v "grep" | wc -l)
if [ $PROCESS_NUM -eq 0 ];
        then
                echo "starting skroll server..."
                nohup java -cp .:config/:Pipeline-all-1.0.jar -Dlog4j.configuration=config/log4j.xml -Dprocess.name=skroll com.skroll.rest.WebServer --port 8088 --baseuri webapp >/dev/null&

        else
                echo "Skroll Server is already running. Stop the server first and then run start.sh again"
                return 0
        fi

