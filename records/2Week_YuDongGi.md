# Title: [2Week] 유동기

## 미션 요구사항 분석 & 체크리스트

---

- ## **미션 요구사항**
    - 필수 과제
      <br/>케이스 4 : 한명의 인스타회원이 다른 인스타회원에게 중복으로 호감표시를 할 수 없습니다.
      <br/>-> 내 인스타 아이디가 aaaa이면 같은 사람에게 같은 사유(예시:외모)로 호감표시를 할 수 없다.
      <br/>케이스 5 : 한명의 인스타회원이 11명 이상의 호감상대를 등록 할 수 없습니다.
      <br/>케이스 6 : 케이스 4 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.
      <br/>-> 내 인스타 아이디가 aaaa이고 기존에 외모로 표시를 해놓았으면 같은 사람에게 다른 사유(예시:성격)로 호감표시를 변경할 수 있다.
    - 선택 과제
      <br/>네이버 로그인으로 가입 및 로그인 처리가 가능하도록 한다.
      <br/>스프링 oauth2 클라이언트로 구현한다.
      <br/><br/>
- ## **체크리스트**
    - [x] 이미 어떤 회원에게 호감표시를 하였을 경우 중복호감표시를 막도록 한다.
      -[x] testcase 작성
    - [x] 한명의 인스타회원이 11명 이상의 호감상대를 등록 할 수 없다. 
      -[x] testcase 작성
    - [x] 기존에 표시한 사유와 다른 사유로 호감을 표시하는 경우는 가능하도록 한다. + 추가되는 것이 아니라 기존의 것을 변경한다.
      -[x] testcase 작성

<br/>

### 2주차 미션 요약

---

**[접근 방법]**
- 케이스 4번: LikeablePersonController의 add메서드를 통해 호감 표시를 진행하면 service에서 중복호감표시를 막도록 하였다.
<br/>-> 존재 여부만 확인하면 되니 레포지토리에 existsBy를 활용하여 이미 호감표시를 하였는지 체크하였다.
<br/>->  boolean existsByFromInstaMemberIdAndToInstaMemberUsername(Long id, String username); 추가 
<br/><br/>

- 케이스 5번: 11명 이상의 호감상대를 등록 못하도록 하는 방법은 fromLikeablePeople의 사이즈를 체크하여 11이상일 경우 못하도록 하였다.
<br/>-> 다음과 같이 서비스에서 걸러주었다. member.getInstaMember().getFromLikeablePeople().size() >= 10 
<br/><br/>
- 케이스 6번
  - 처음에는 호감 사유만 다르면 변경이 아니라 추가로 잘못 생각하여 boolean existsByFromInstaMemberAndToInstaMemberUsernameAndAttractiveTypeCode(InstaMember fromInstaMember, String username, int attractiveTypeCode) 
    이 코드로 있는지 체크 후에 없으면 추가하도록 하였다.
  - 하지만 추가가 아닌 변경으로 깨닫고 나서는 findBy로 찾아서 .isPresent로 있을 경우 호감사유가 같으면 historyback, 
    다르면 update를 하도록 하였다.

**[특이사항]**

- 

> 참고 문서 : 


[추가사항]
+
+




