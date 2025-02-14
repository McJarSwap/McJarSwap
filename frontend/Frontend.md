# 🏡 Minecraft Server Jar Swap  - Frontend
<img src="https://skillicons.dev/icons?i=js,css,react"><br>
## 1. 개요

이 프로젝트는 [Create React App](https://create-react-app.dev/)을 기반으로 한 **React 애플리케이션**입니다.  
주요 기능은 다음과 같습니다.

- **방 목록 관리**: 기존 방 리스트를 조회하고 관리할 수 있습니다.
- **방 생성(AddRoom)**: 새로운 방을 추가할 수 있습니다.
- **방 설정(SettingsScreen)**: 각 방의 설정을 수정할 수 있습니다.

각 기능은 **다양한 컴포넌트**와 **API 호출**을 통해 동작하며, 전체 애플리케이션은 React Router를 활용해 페이지를 구성합니다.

---

## 2. 애플리케이션 실행 흐름

### 🚀 1. HTML 템플릿 로드
- `index.html`이 브라우저에 로드되며, `<div id="root"></div>`가 애플리케이션의 **렌더링 시작점**이 됩니다.

### ⚛️ 2. React 애플리케이션 초기화
- `index.js`에서 `ReactDOM.createRoot`를 사용해 `<App />`을 `#root` 엘리먼트에 렌더링합니다.

### 🔀 3. 앱 컴포넌트 구성 및 라우팅
- `App.js`에서 `react-router-dom`을 사용해 **라우팅**을 설정합니다.
    - `/` → `<Main />` (메인 화면)
    - `/settings` → `<SettingsScreen />` (설정 화면)
- `useEffect`를 활용해 `fetchRooms()`를 호출하고, **백엔드에서 방 목록을 가져와 상태(`rooms`)에 저장**합니다.

### 🏠 4. 메인 화면 동작
- `Main.js`에서 방 목록을 표시하며, **"Create Room +" 버튼**을 통해 `<AddRoom />` 팝업을 띄울 수 있습니다.
- `RoomList.js`가 **방 리스트**를 관리하고, 각각의 방은 `RoomCard.js`를 통해 개별 렌더링됩니다.
- `<RoomCard />`에서는 **방 삭제, 설정 페이지 이동** 등의 기능을 제공합니다.

### ➕ 5. 방 생성 프로세스
- `<AddRoom />` (`src/components/AddRoom.js`)에서 입력 폼을 통해 방 정보를 입력합니다.
- **포트 번호 중복 확인**, **메모리 검증** 등의 **유효성 검사** 후, `FormData`를 사용해 **서버에 POST 요청**(`/addroom`)을 보냅니다.
- 요청이 성공하면 **새 방이 추가**되며, 팝업이 닫히고 **메인 화면이 업데이트**됩니다.

### ⚙️ 6. 설정 페이지 동작
- `<SettingsScreen />` (`src/components/SettingsScreen.js`)에서는 **URL 쿼리 및 상태(state)**를 활용해 해당 방의 설정을 수정할 수 있습니다.
- **포트 변경, 게임 모드 선택, 버전(JAR) 업로드** 등 설정 변경 사항은 **API 요청**(`/settings/save`)을 통해 반영됩니다.

### ⏳ 7. 로딩 화면 처리
- 데이터가 로딩 중일 때, `<LoadingScreen />` (`src/components/LoadingScreen.js`)이 표시됩니다.
- 애니메이션 효과는 `LoadingScreenStyle.css`에서 정의되어 있으며, **사용자에게 진행 상태를 시각적으로 알립니다**.

### 🔗 8. API 요청 관리
- `axios.js`에서 **Axios 인스턴스**를 설정하여 API 요청을 중앙 관리합니다.
- **기본 URL(`http://localhost:8080`)과 공통 헤더**를 적용하여 모든 API 호출이 일관되게 처리됩니다.

---

## 3. 구조 요약

이 애플리케이션은 다음과 같은 구조를 가집니다.

1. **HTML 템플릿(index.html)** → **React 초기화(index.js)**
2. **라우팅 및 전역 상태 관리(App.js)**
3. **개별 기능을 담당하는 컴포넌트**
4. **API 요청을 관리하는 axios.js**

이러한 구조를 통해 **모듈화된 컴포넌트 설계**와 **효율적인 상태 관리**를 실현하고 있으며, React Router 및 Axios를 활용하여 **클라이언트-서버 간 통신을 원활하게 처리**합니다.
