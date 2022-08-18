# 평촌경영고등학교 아우토크립트 실습생 심혁
[프로젝트 깃 링크](https://github.com/hyuck0221/project.git)  
[공부내용 정리 노션 링크](https://subdued-draw-211.notion.site/MAIN-PAGE-a72ffdd2ebf744278995667f1df0b3cd)

### 서비스 소개
로그인 후 게시판을 이용할 수 있는 서비스

### 구현 내용
* 로그인
* 회원가입
* 회원가입 시 중복체크
* 로그아웃
* 로그인 회원만 게시판 접근
* 게시판 CRUD
* 로그인된 정보와 글 작성자의 정보가 같을 시 수정 및 삭제 가능
* 글 수정 시 본인만 볼 수 있도록 잠금설정

### 실행 시나리오
    /sign 주소에서 아이디, 패스워드, 닉네임 입력  
      이미 있는 아이디가 감지되면 오류  
      중복체크에 오류가 없다면 user 데이터베이스에 정보 입력 (INSERT)
    /login 에서 회원가입한 아이디, 패드워드 입력
      없는 아이디면 오류
      로그인 성공 시 login_user 변수에 로그인한 유저 정보 입력
    로그인 후 게시판 접속 (자동 /board_list 주소로 이동)

    /board_list가 메인페이지로 설정
    CREATE 클릭 시 /create_board 이동
    제목과 내용 입력 후 ENTER
      게시판 내용이 입력되면 board 데이터베이스에 정보 입력 (INSERT)
    게시판 정보가 입력되며 부여받은 crud id를 이용해 /board_list 페이지에서 글 확인 가능
      확인 시 본인 글이면 EDIT 버튼 활성화
      EDIT 클릭 시 /edit_board 이동하여 수정 가능 (본인만 볼 수 있게 설정 가능)
    DELETE 클릭 시 /delete_board 이동
    게시판 글 crud id를 이용해 본인이 작성한 글이 확인되면 해당 글 삭제
    logout 버튼 클릭 시 /logout이 입력되며 index 페이지로 이동
    로그아웃 상태에선 /board_list, /create_board 등 접근 불가
### 배운 내용
* kotlin 문법 이해
* intellij 사용법
* mariaDB 사용법
* mysql 문법 이해
* 데이터베이스에 대한 개념 이해
* 데이터베이스 연동 방법 이해
* spring boot 작동방식 이해
* 공부 하면서 "화면 없이"는 만들 진 못했지만 개념 이해

### 공부 하면서 느낀점
과제를 진행 해 보면서 많은 것들을 배우고 느낄 수 있었습니다  
초반에는 어디부터 접근 해야 할 지 너무 막막해서 걱정됐지만  
제가 이렇게 한가지 일에 집중할 수 있다는 것을 보고 해낼 수 있다고 믿습니다  
이번 과제는 요구사항을 모두 완벽하게 해내진 못했는데  
특히 가장 아쉬운 점이 요구사항에서 "화면 없이"를 해내지 못한 게 가장 걸렸습니다  
처음부터 API라는 개념을 잘 이해했었더라면 지금보다 좀 더 완벽했지 않았을까 싶습니다  
이 점으로 이 분야에서의 공부 필요성을 느낀 만큼 더욱 열심히 할 수 있을 것 같습니다