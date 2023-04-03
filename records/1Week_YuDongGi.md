# Title: [1Week] 유동기

## 미션 요구사항 분석 & 체크리스트

---

- ## **미션 요구사항**
    - 필수 과제
      <br/>-> 호감 목록 페이지에서 특정 항목에 삭제 버튼을 누르면 삭제되는데
      <br/>삭제하기 전에 해당 항목에 대한 소유권이 로그인한 사람에게 있는지 체크한다.
    - 선택 과제
      <br/>-> 구글 로그인으로 가입 및 로그인 처리가 가능하도록 한다.
      <br/>스프링 oauth2 클라이언트로 구현한다.
      <br/><br/>
- ## **체크리스트**
    - [x] 금방 할 수 있는 구글 로그인 + 네이버 로그인 구현
        - oauth2-client 라이브러리 사용하여 구현하였다. 구글의 경우 id와 secret만 입력해주면 되었다. yml파일을 따로 분리해 inclue = oauth을 적어주고 gitignore에
          추가하여 보안을 유지하였다.
    - [x] 호감상대 삭제 Test case 만들기
        - 테스트케이스는 이미 등록되어 있는 것을 활용하였다. user3으로 로그인하여 미리 user4에게 호감표시 해 놓은 것을 삭제하면 LikeablePersonController에서 delete 메서드를
          통해 삭제하고 삭제가 완료되면 호감목록으로 돌아가므로 3xx로 리다이렉트 된다.
    - [x] 호감상대 삭제 구현
        - LikeablePersonService에서 id을 통해 LikeablePerson을 찾아서 삭제하였다. RsData를 활용하여 실패하면 실패 메세지를 보여주고 삭제에 성공하면 삭제가 완료되었다는
          메세지를 보여주었다.
    - [x] 호감상대 삭제 권한 확인 구현
        - principal을 활용하였다. 현재 로그인 한 사람의 인스타정보에서 id와 LikeablePerson의 id를 비교하여 같으면 삭제하고 다르면 삭제할 수 없다는 메세지를 출력하였다.
    - [x] UI 개선
        - [x] 내비 바 추가 -> 데이지UI 사용
        - [x] 로그인, 회원가입 폼 가운데로 이동 -> tailwind 사용

<br/>

>  호감 목록에 있는 것을 삭제하는 기능 구현은 그동안 배운 것을 활용해 구현하는 것이라 어렵지 않았다.
> 하지만 제일 쉬울 줄 알았던 구글 로그인이 가장 문제였다. 계속 에러가 나서 찾아보던 중, openid라는 scope가 있을 경우 Open Id Provider로 인식한다고 한다. 그래서 scope=profile, email
> 을 추가해주었다. 구글은 OpenId Provider인 서비스이고 카카오, 네이버는 그렇지 않다고 한다. 추후에 UI를 좀 더 깔끔하게 꾸며볼 계획이다.
> 
> 참고 문서 : https://daisyui.com/ , https://tailwindcss.com/docs/, https://developers.google.com/identity/protocols/oauth2/scopes?hl=ko


