# McJarSwap

## 🚀 프로젝트 소개
Minecraft 게임 서버의 jar 파일을 손쉽게 관리하고, 서버의 정보를 웹 페이지를 통해 수정할 수 있게 하는 프로젝트입니다. 프론트엔드와 백엔드가 통합되어 있습니다.

---

### 🖥️ 프론트엔드 
<img src="https://skillicons.dev/icons?i=js,css,react"><br>
- **사용자 인터페이스**: 열려있는 서버 상태를 실시간으로 확인하고 관리, 수정할 수 있는 직관적인 UI
- **파일 업로드**: jar 파일 업로드 가능
- **새로운 서버 생성**: 서버 내용을 지정해 새로운 서버 생성

### ⚙️ 백엔드
<img src="https://skillicons.dev/icons?i=java,spring"><br>
- **Ubuntu 폴더 수정**: 프론트엔드에서 받은 요청을 Ubuntu 명령어를 통해 실행
- **마인크래프트 서버 직접 관리**: 프론트엔드에서 받은 요청을 통해 특정 서버를 찾아서 서버 종료 후 요청 실행. 서버 재시작

## 📋 주요 기능
1. 방 이름, 포트 번호, 게임 모드, 최소 최대 메모리 크기, JAR파일을 업로드 해 게임 방 새로 생성
2. 방의 포트 번호 수정
3. 게임 모드(서바이벌, 크리에이티브, 어드벤쳐) 변경 가능
4. 서버 JAR 업로드해서 버전 변환

## 📂 디렉토리 구조
```
project-root
├── 🖥️frontend
│   ├── public
│   ├── src
│   │   └── components
└── ⚙️backend
    └── (추후 수정 예정)
```

---

## 🚀 설치 및 실행 방법

1. McJarSwap/backend/src/main/McJarSwap/WebConfig.java 파일로 이동하여 배포할 프론트엔드 주소를 입력한다.
2. backend의 build.gradle을 활용해 FatJAR을 생성한다. 생성된 FatJAR을 우분투 서버에 업로드한다. 우분투 서버에서 git clone를 활용해 프론트엔드를 내려받는다.
3. [Ubuntu] {홈 디렉토리 경로}/McJarSwap/frontend 로 이동 후, npm start로 프론트엔드를 실행한다.
4. [Ubuntu] **2**에서 업로드한 FatJar을 java -jar McJarSwap.jar로 실행한다.
5. [Ubuntu] 서버ip:(프론트 서버 포트번호)로 접속한다.

---

## 📖 사용 방법

- addroom 버튼을 통해 서버를 생성 & 실행
- setting 버튼을 통해 서버 정보와 버전을 변경
- 휴지통 아이콘을 통해 서버를 정지하고 폴더를 제거


## 팀원
| | | |
|---|---|---|
| <img src="https://github.com/ChabinHwang.png" width="100px" alt="ChapinHwang"/> | <img src="https://github.com/bashdas.png" width="100px" alt="bashdas"/> | <img src="https://github.com/wvwwvv.png" width="100px" alt="wwwwv"/> |
| [ChabinHwang](https://github.com/ChabinHwang) | [bashdas](https://github.com/bashdas) | [wwwwv](https://github.com/wvwwvv) |
