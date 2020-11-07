#!/bin/bash

docker run --rm -it -d --name=scala-spark --mount type=bind,source=${PWD},destination=/root/git -p 4040:4040 johannesgraner/scala-with-spark:cached-jars 

