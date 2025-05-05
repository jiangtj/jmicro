# .ide/Dockerfile
FROM openjdk:21

WORKDIR /root

# 安装 ssh 服务
RUN apt-get update && apt-get install -y wget unzip openssh-server

# 创建 /ide_cnb 目录，用于安装 IDE，注意安装路径必须是这个，便于自动识别环境中支持哪些 ide
RUN mkdir -p /ide_cnb

# 选择安装下方其中一个或多个 IDE

# 安装 IntelliJ IDEA
RUN wget https://download.jetbrains.com/idea/ideaIU-2024.3.5.tar.gz
RUN tar -zxvf ideaIU-2024.3.5.tar.gz -C /ide_cnb

# 安装 code-server(VSCode WebIDE 支持)
RUN curl -fsSL https://code-server.dev/install.sh | sh \
  && code-server --install-extension redhat.vscode-yaml \
  && code-server --install-extension orta.vscode-jest \
  && code-server --install-extension dbaeumer.vscode-eslint \
  && code-server --install-extension waderyan.gitblame \
  && code-server --install-extension mhutchie.git-graph \
  && code-server --install-extension donjayamanne.githistory

ENV LANG C.UTF-8
ENV JAVA_TOOL_OPTIONS -Duser.timezone=GMT+8 -Dfile.encoding=UTF-8
