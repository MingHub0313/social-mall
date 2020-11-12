#!/usr/bin/env bash
# ./start.sh mall-gateway
echo "脚本名$0"

SERVER_NAME=$1
#容器id
CID=$(docker ps | grep "$SERVER_NAME" | awk '{print $1}')
#镜像的ID
IID=$(docker images | grep "$SERVER_NAME" | awk '{print $3}')
function run(){
    transfer
    build
    if [-n "$CID"]; then
        echo "存在$ SERVER_NAME 容器 CID=$CID 重启docker容器..."
         docker restart $ SERVER_NAME
        echo "$SERVER_NAME 容器重启完成..."
   else

        echo "不存在$SERVER_NAME 容器 docker run创建容器..."

                docker run-d --name $ SERVER_NAME -p 88:88 --privileg=true $ SERVER_NAME

        echo "$SERVER_NAME 容器创建完成"
    fi
}

function transfer(){
        echo "----stop container-----"
        docker stop $ SERVER_NAME
        echo "----delete container-----"
        docker rm -f $ SERVER_NAME
        echo "------CID $IID ----"
        docker rmi -f $ IID
        echo "删除镜像"

}


# 构建docker 镜像
function build(){
    if [-n "$IID"]; then
        echo "存在$SERVER_NAME 镜像  IID=$IID"
        docker rmi $ IID
        docker build -t $ SERVER_NAME .
        echo "存在删除 再构建"
        docker run -d --name $ SERVER_NAME -p 88:88 --privileged=true $ SERVER_NAME
     else
        echo "不存在$SERVER_NAME 镜像 开始构建镜像"
        docker build -t $ SERVER_NAME .
  fi
}
