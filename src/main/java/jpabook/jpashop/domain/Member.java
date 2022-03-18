package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity //회원 엔티티
@Getter
@Setter
public class Member { //엔티티타입

    @Id //key값 ,db저장되기 전에도 로직 잡혀있음
    @GeneratedValue //em.persist(member);할때 @~이거하면 어쨌든 id값이 항상 생성되는게 보장이 된다.
    @Column(name = "member_id") //pk 컬럼명 //엔티티의 식별자
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //하나의 회원, 여러개 상품 주문 : 일대다
    //mappedBy : 연관관계 주인이 아니다. order 테이블에 있는 member필드에 의해 매핑된것
    //읽기전용 : 여기에 값을 넣는다해서 외래키 값이 변경되지 않음
    private List<Order> orders = new ArrayList<>(); //null pointer exception 안나게



}
