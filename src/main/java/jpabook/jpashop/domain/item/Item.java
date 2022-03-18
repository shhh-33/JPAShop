package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
//구현체를 갖기 때문에 추상클래스
//회원 엔티티
@Inheritance(strategy = InheritanceType.JOINED) //joined : 가장 정규화된 스타일..싱글테이블
//상속관계다.. Item -> Album,Book, Movie
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();


    //비즈니스 로직

    /*
    재고 증가
     */
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }

    /*
    재고 감소
     */
    public  void  removeStock(int quantity){ //주문하면 생성된다.
        int restStock = this.stockQuantity -quantity;
        if(restStock <0 ){
            throw  new NotEnoughStockException("need more stock");
        }
        this.stockQuantity =restStock;
    }

}
