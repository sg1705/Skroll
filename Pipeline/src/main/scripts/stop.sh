#!/bin/bash

PROCESS_NAME="process.name=skroll"
PROCESS_NUM=$(ps -ef | grep $PROCESS_NAME | grep -v "grep" | wc -l)
PROCESS_ID=$(pgrep -f process.name=skroll)
if [ $PROCESS_NUM -eq 0 ];
        then
                echo "No Skroll Server is running..."
        else
                echo "stopping Skroll Server"
                ps -ef | grep $PROCESS_NAME |grep -v "grep" | awk '{print $2}' | xargs sudo kill
                echo "Skroll Server stopped"
        fi