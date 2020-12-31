### Reader
* 100개의 person data를 csv 파일에서 읽는다.

### Processor
* person data를 jpa를 이용해 H2DB 에 저장한다.
* allow_duplicate 파라미터로 이름(person.name) 중복 저장 여부를 설정한다.
* `allow_duplicate=true` 인 경우 이름(person.name) 중복 상관 없이 모두 저장한다.
* `allow_duplicate=false 또는 null` 인 경우 이름(person.name)이 중복된 데이터는 저장하지 않는다.
* 힌트 : 중복 체크는 `java.util.Map` 사용

### Writer
* 몇 건 저장됐는 지 log를 찍는다.
* 힌트 : `CompositeItemWriter` 사용
