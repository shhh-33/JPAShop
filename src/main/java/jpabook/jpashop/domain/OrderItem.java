package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//주문상태 엔티티
@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //orderItem 입장에서 item
    @JoinColumn(name = "item_id") //fk
    private Item item; //주문상품

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") //fk
    //하나의 order(주문)이 여러개의 orderitem 가질 수 있다.
    private Order order; //주문

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //==주문상품 생성 메서드==//
    public static OrderItem createOrderItem(Item item ,int orderPrice , int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);

        return orderItem;
    }


    //==비즈니스로직==//
    public void cancel() {
        getItem().addStock(count); //취소 -> 주문수량만큼 재고 올리기
    }


    //==조회로직==//
    //주문상품전체가격조회
    public int getTotalPrice() {
         return getOrderPrice() * getCount(); //가격*수량
    }
}
