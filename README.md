# CentOS에서 실행하기

### 1. CentOS 서버 접속 (7.9)

### 2. yum 최신화, update
```bash
yum install epel-release -y
yum update -y
```

### 3. Docker 설치
```bash
yum remove docker \
    docker-client \
    docker-client-latest \
    docker-common \
    docker-latest \
    docker-latest-logrotate \
    docker-logrotate \
    docker-engine

# Set up the repository
yum install -y yum-utils

yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
    
# Install Docker Engine
yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# 도커 시작
systemctl start docker

# 도커 활성화
systemctl enable docker
```

### 4. DB 설치 , gram__prod database 생성
```bash
# MariaDB 설치
docker run \
  --name mariadb_1 \
  -d \
  --restart unless-stopped \
  -e MARIADB_ROOT_PASSWORD=lldj123414 \
  -e TZ=Asia/Seoul \
  -p 3306:3306 \
  -v /docker_projects/mariadb_1/conf.d:/etc/mysql/conf.d \
  -v /docker_projects/mariadb_1/mysql:/var/lib/mysql \
  -v /docker_projects/mariadb_1/run/mysqld:/run/mysqld/ \
  mariadb:latest
```
```bash
#보안설정
docker exec -it mariadb_1 /usr/bin/mariadb-secure-installation
# Switch to unix_socket authentication [Y/n] n
```
```bash
# DB 접속 하고
CREATE DATABASE gram__prod;
```

### 5. git 설치, java 설치

### 6. git clone으로 소스코드 다운로드, gitignore된 파일 복원
```bash
mkdir -p /docker_projects/gram/project

cd /docker_projects/gram/project

git clone https://github.com/udonggi/Mission_YuDongGi .
```

```bash
# gitignore된 파일 복원 application-oauth.yml.default -> application-oauth.yml
vim src/main/resources/application-oauth.yml
```

### 7. 빌드
```bash
cd /docker_projects/gram/project

chmod 744 gradlew

./gradlew clean build
```

### 8. Docker 이미지 생성, 실행
```bash
docker build -t gram .

docker run \
  --name=gram_1 \
  --restart unless-stopped \
  -p 80:8080 \
  -e TZ=Asia/Seoul \
  -d \
  gram
```