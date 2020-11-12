FROM java:latest
# 复制打包完成后的jar文件到/home目录下

# VOLUME 指定了临时文件目录为/tmp。
# 其效果是在主机 /var/lib/docker 目录下创建了一个临时文件，并链接到容器的/tmp
VOLUME /tmp

ADD mall-coupon.jar mall-coupon.jar

# RUN bash -c 'touch /app.jar'

# bash方式执行，使krt.jar可访问
# RUN新建立一层，在其基础上执行这些命令，执行结束后，commit这一层的修改，构成新的镜像

#RUN bash -c 'touch /app.jar'
# 设置时区
#RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 7000
ENTRYPOINT ["java", "-jar","mall-coupon.jar"]

