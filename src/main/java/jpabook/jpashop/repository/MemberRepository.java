package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository //Component scan해서 spring bean 자동등록
@RequiredArgsConstructor
public class MemberRepository {
     /*
       @PersistenceContext //spring이 jpa가 Entity Manager를 만들어서 EntityManager를 여기에 주입
       private EntityManager em;
      */

    private final EntityManager em;

    //jpa가 member를 저장하는 로직
    //EntitlyManager가 persist하면 영속성 컨택스트에 Member Entity를 넣고 트랜잭션이 커밋되는 시점에 DB에 insert쿼리 날라가서 저장
    public void save(Member member) {
        em.persist(member);
    }


    //조회 로직 -- 회원 id로 단건 조회
    public Member fineOne(Long id) {
        //find : JPA가 제공하는 메서드
        return em.find(Member.class, id); //(타입, pk) id값을 넘긴 후  member를 찾아서 반환
    }

    //List 조회된 결과 반환해서 회원목록 뿌림
    public List<Member> findAll() {
        //jpql 쿼리, 반환타입
        //sql이랑 기능적이랑 문법은 거의 동일 - > sql로 변환되기 때문
        //sql은 테이블을 대상으로 변환되지만 이거는 Entitly객체를 대상으로 변환
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    //특정 이름으로 조회
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",
                        Member.class)
                .setParameter("name", name)
                .getResultList();
    }


}
