<<<<<<< HEAD
## Git 협업 방식
Git Flow

* main 브랜치
    * 가장 최근 배포 버전 현재 운영중인 프로덕션 서비스
* release 브랜치
    * 네이밍 규칙 : release/버전명
    * 프로덕션에 배포할 서비스를 버전별로 관리
* hotfix 브랜치
    * 네이밍 규칙: hotfix/버전명
    * 이미 배포된 브랜치 (release 브랜치) 에서 긴급 수정이 필요할경우
        * 버그 수정후 머지하면 해당 브랜치 삭제
* develop 브랜치
    * 네이밍 규칙 : dev
    * 개발중인 브랜치, feature 브랜치를 합쳐서 최종적으로 release 브랜치로 머지
* feature 브랜치
    * 네이밍 규칙 : feature
    * 새로운 기능을 개발하기 위한 브랜치 develop 브랜치를 pull 해와서 개발진행

Git Convention

* feat : 새로운 기능 추가
* fix : 버그 수정
* docs : 문서 수정
* style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
* refactor : 코드 리펙토링
* test : 테스트 코드, 리펙토링 테스트 코드 추가
* chore : 빌드 업무 수정, 패키지 매니저 수정

처음 import시   
cd frontend   
npm install


=======
# Hotel
>>>>>>> 6245cdd (Initial commit)
