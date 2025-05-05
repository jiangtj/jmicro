# .ide/Dockerfile
FROM timbru31/java-node:21-jdk-22

WORKDIR /root

# 安装 ssh 服务
RUN apt-get update && apt-get install -y wget unzip openssh-server

# 创建 /ide_cnb 目录，用于安装 IDE，注意安装路径必须是这个，便于自动识别环境中支持哪些 ide
RUN mkdir -p /ide_cnb

# 选择安装下方其中一个或多个 IDE

# 安装 IntelliJ IDEA
RUN wget https://download.jetbrains.com/idea/ideaIU-2025.1.tar.gz
RUN tar -zxvf ideaIU-2025.1.tar.gz -C /ide_cnb

# 安装 code-server(VSCode WebIDE 支持)
RUN curl -LSs "https://download.jetbrains.com/product?code=FLL&release.type=preview&release.type=eap&platform=linux_x64" --output fleet && chmod +x fleet

ENV LANG C.UTF-8
ENV JAVA_TOOL_OPTIONS -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8
