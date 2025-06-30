# image-saver


<h1>필수 설정</h1>

- local.properties 파일에 KAKAO_API_KEY 를 추가해야합니다.

<h1>설치 환경</h1>

- JDK 11 이상

- Android Studio Arctic Fox 이상 (권장: 최신 Stable 버전)

- Gradle-8.11.1 (프로젝트 gradle-wrapper.properties에 명시된 버전 사용)

- Android SDK: minSdk 26 / targetSdk 35


<h1>주요 기능</h1>

<h2>BookmarkScreen</h2>

- 로컬 DB에 저장된 Bookmark 이미지 리스트 조회 및 삭제

- 이미지 / 영상 Type 별 필터 기능

- Bookmark 아이템 선택 시 큰 이미지 보기 가능 (핀치 줌 지원)

<h2>SearchScreen</h2>

- 검색어 입력 시 자동 검색 기능

- 5분간 검색 결과 캐싱 (동일 키워드 5분 이내 재검색 시 네트워크 요청 없이 결과 제공)

- 캐싱된 검색어 조회 기능

- 스크롤 시 다음 페이지 불러오기 지원

- Kakao API를 통한 이미지 + 동영상 검색 결과 조회 (datetime 필드 기준 정렬)

- Bookmark에 보관된 이미지에는 마킹 표시 출력