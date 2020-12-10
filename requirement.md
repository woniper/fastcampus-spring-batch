## 회원 등급 적용
1. 회원 등급은 총 4가지로 구분 된다.
    * 일반(normal), 실버(silver), 골드(gold), VIP
1. 최근 3개월 간 총 주문 금액이
    * 200,000원 이상인 경우 실버
    * 300,000원 이상인 경우 골드
    * 500,000원 이상인 경우 VIP
2. 통계 데이터 생성


## 개발 순서
1. User(not @Entity) 생성하고 reader, processor, writer 만들어 로그 찍기
2. processor에서 availableLevelUp 메소드로 writer 대상 filtering
3. writer에서 levelup 실행하고, levelup 전/후 데이터 로그 찍기
    * User.levelup 메소드 테스트 코드 작성
4. reader에서 User를 @Entity로 만들고 H2 DB에 저장하기
5. Orders @Entity를 만들어 User와 @OneToMany 관계로 설정, totalAmount를 List<Orders>로 계산하기
5. LevelUpConfiguration 테스트 코드 작성
    * User.updatedDate 필드 추가
6. LevelUpJobExecutionListener 만들기
    * levelup 완료된 User에게 이메일 전송 (updatedDate = now() 인 데이터)
    * 실행 시간 측정
    * 실행 결과 로그 찍기
7. mysql profile 추가
    * batch-schema-.sql을 찾아 local mysql에 생성
    * User <-> Orders 양방향 매핑으로 변경
8. OrderStatisticsStep 개발
9. 성능 개선 (성능 표 채우기)
    * 400건 -> 4000건으로 늘림
    * batch_size 0, 1000건 비교
    * jdbc batch insert로 비교
    * TaskExecutor 사용해서 Multi Task Step 실행 (batch_size 비교)
        * saveUserStep을 multi thread 처리하면 나타나는 문제와 해결 방법
    * userLevelUpStep와 orderStatisticsStep을 Flow로 병렬 처리
