package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //이건 롤백(=DB에 있는거 다버린다)
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;


    /*
    그런데 실행하면 콘솔창에 insert문이 뜨지 않는다..
    왜냐면
    JPA는 DB트랜잭션이 커밋될때 그때 영속성 컨택스트에 있는 jpa객체인 member가 만들어 지면서 insert쿼리가 나가는 것
    그런데 스프링에서의 @Transactional은 기본값이 롤백
    그래서 @Rollback(false)로 설정하면 롤백안하고 커밋해버림
    이거 설정하면 insert쿼리 나온다
     */
    @Test
    @Rollback(false)
    public void 회원가입() throws Exception {

        //given : 이런게 주어졌을때
        Member member = new Member(); //member 만들고
        member.setName("길");

        //when : 이렇게 하면
        Long saveId = memberService.join(member);

        //then : 이렇게 된다.
        assertEquals(member, memberRepository.fineOne(saveId)); //멤버가 멤버리파지토리에서 멤버를 찾는다 정상적으로 가입됐는지 확인
        //트랜젝션 안에서 fk값 같으면 같은 영속성 컨택스를 가져서 하나로 관리된다.

    }

    @Test(expected = IllegalStateException.class)
    public void 회원중복() throws Exception {

        //given
        Member member1 = new Member();
        member1.setName("김");

        Member member2 = new Member();
        member2.setName("김");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다.

        //then
        fail("예외가 발생해야한다."); //이거 출력되면 잘못작성된 코드
    }

}