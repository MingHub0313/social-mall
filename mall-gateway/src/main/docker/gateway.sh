#!/bin/bash

SERVER_NAME=mall-gateway
#容器id
CID=$(docker ps | grep "$SERVER_NAME" | awk '{print $1}')
#镜像的ID
IID=$(docker images | grep "$SERVER_NAME" | awk '{print $3}')
echo "----stop container-----"
docker stop $SERVER_NAME
echo "----delete container-----"
docker rm -f $SERVER_NAME
echo "------CID $IID ----"
docker rm -f $IID
echo "删除镜像"

#if [-n "$CID"]; then
#        echo '存在$SERVER_NAME容器 CID=$CID 重启docker容器...'
#        docker restart $SERVER_NAME
#        echo '$SERVER_NAME 容器重启完成...'
#else
#        echo '不存在$SERVER_NAME容器 docker run创建容器...'
#        docker run -d --name $SERVER_NAME -p 88:88 --privileged=true $SERVER_NAME
#        echo '$SERVER_NAME容器创建完成'
#fi
