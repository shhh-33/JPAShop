package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

//카테고리 엔티티
@Entity
@Getter @Setter
public class Category {

    //pk
    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    
    @ManyToMany  // Category엔티티도 List로 items을 가지고, item엔티티도 List로 Category를 가진다.
    //일대다 다대일로 풀어내는 중간테이블이 존재하기 때문에 테이블로 매핑해줘야한다
    @JoinTable(name = "category_item", //중간테이블 이름 정하기
            //매핑
            joinColumns = @JoinColumn(name = "category_id"),//중간테이블과 category테이블 join (fk)
            inverseJoinColumns = @JoinColumn(name = "item_id") //중간테이블에서 item테이블 join (fk)
              )
    private List<Item> items = new ArrayList<>();


    //밑의 둘은 같은 엔티티를 셀프로 양방향 연관관계 매핑
    @ManyToOne (fetch = LAZY)
    @JoinColumn(name ="parent_id") //fk
    private Category parent;

    //자식 여러명 가능
    @OneToMany(mappedBy = "parent") //  private Category parent; 여기로 이동
    private  List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child); //
        child.setParent(this); //자식에서도 부모가 누구인지를
    }
}

