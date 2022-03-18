package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //스프링이 제공함
@Transactional(readOnly = true) //JPA의 데이터 변경시에 꼭 있어야 함 (트랜잭션 안에서 변경할 것) 그래야 lazy로딩같은것도 된다.
// (readOnly = true) : jpa 성능 좋게 해줌 (읽기전용 -> 부하를 많이 주지 않음)

//@AllArgsConstructor //필드의 모든 생성자 만들어줌
@RequiredArgsConstructor //final이 있는 필드만 생성자 만들어줌
public class MemberService {


    private final MemberRepository memberRepository; //생성자


    /*
     1.field injection (스프링이 스프링빈에 등록되어있는 MemberRepository를 injection 해준다)
      @Autowired
      private MemberRepository memberRepository;
      단점이 많다. 테스트할때 못바꾼다. 엑세스 할 수 있는 방법이 없다. field + private라

     2.setter injection
      @Autowired
      public void setMemberRepository(MemberRepository memberRepository){
        this.memberRepository =memberRepository;
      }
        //장점 : 주입하기 편리함
        //단점 : 한번뭔가 런타임을 하는 시점에 누군가가 바꾸면 ..?

     3.생성자 injection : 한번 생성할때 끝나버림 , 테스트 케이스 작성할때 멤버서비스를
       @Autowired
       public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository; //값 setting
      }
     */


    //회원가입       //쓰기에는 true 넣으면 데이터 변경안되니까 넣지 말것
    @Transactional //이렇게 따로 설정하면 이게 우선순위 (기본값 readOnly=false)
    public Long join(Member member) {
        validateDuplicateMember(member); //중복회원 검증 로직
        memberRepository.save(member); //save : MemberRepository 함수
        return member.getId(); //값이 있다는게 보장이 된다. persist 하는 순간
    }

    //중복체크
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /*
    회원 전체 조회
    */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    //단건 조회 (id로)
    public Member findOne(Long memberId) {
        return memberRepository.fineOne(memberId);
    }

}
