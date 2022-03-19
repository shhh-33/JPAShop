package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;


@Entity
@Table(name = "orders") //order가 sql 예약어라 s붙임
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //new Order()로 생성x , 생성메서드의 틀에서만 생성
public class Order { //주문엔티티

    @Id
    @GeneratedValue
    @Column(name = "order_id") //테이블 컬럼 이름
    private Long id;

    //테이블 컬림들

    @ManyToOne(fetch = LAZY) //order랑 member는 다대일 (서로반대) , 여러개 주문<-하나의 회원
    @JoinColumn //매핑을 어떻게 할건지 : 연관관계 주인으로 fk
    private Member member; //주문회원


    /*
     *cascade 안하면
     * persist(orderItemA)
     * persist(orderItemB)
     * persist(orderItemC) 이렇게 해야하함
     * 그래서
     * persist(order) 로 끝내게
     *
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //order에 의해서 매핑됐다 , 주인아님
    private List<OrderItem> orderItems = new ArrayList<>();

    //cascade : order를 저장할때 delivery도 자동으로 persist 해준다.
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) //하나의 배송정보는 하나의 주문정보만 가져야 하니까
    @JoinColumn(name = "delivery_id")  //연관관계 주인 fk
    private Delivery delivery; //배송정보


    private LocalDateTime orderDate; //주문시간 @Date 안써도 자바8이상에선 지원 //order_date로 바꿔짐 sql에서

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER ,CANCEL}


    /*
     * 회원엔티티
     * Member는 orders를 list로 가지고 있다
     * Order도 멤버를 가지고 있다 .양방향 참조중!
     * 근데 문제는
     * DB의 fk는 member_id 뿐
     * member_id의 관계를 바꾸고 싶으면 외래키 값을 변경해야하는데
     * Member에도 orders와 관련된 필드가 있고
     * Order에도 멤버필드가 있는데
     * 둘중에 jpa는 뭘 확인해서 fk를 바꿔야하냐?
     * fk값 있는 쪽을 연관관계 주인으로해서 업데이트
     * 그러면 fk없는 쪽은 읽기전용으로 된다.
     *
     */

    /*
    일대일 관계일시에는 많이 참조하는 테이블에 외래키 둘것 그래서 주문정보에 둘거임 배송정보 말고
     */

    /*
        원래라면..근데 놓칠 수 도 있으니까
        밑처럼 원자적으로 묶어버린다
      public static void main(String[] args){
        Member member = new Member();
        Order order = new Order();

        member.getOrders().add(order);
        order.setMember(member)
    }
     * */


    //==연관관계 메서드== 양방향 연관관계일때
    public void setMember(Member member) {
        this.member = member; //member setting할때 order 입장에서 이렇게 멤버를 넣는다
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성메서드==//
    //회원,배송,주문내역 넘기기 (...: 여러개)
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) { // 위에 선언 private List<OrderItem> orderItems = new ArrayList<>();
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //초기값을 order로 강제해둔다
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스로직==//

    /**주문 취소
     *
     */
    public void cancel() {
        //배송이 끝나면 취소 불가
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        //vaildation을 통과하면 주문 상태 바꿀 수 있다.
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); //

        }
    }
    
    //==조회 로직==//
    
    //전체 주문 가격 조회
    public int getTotalPrice(){
        int totalPrice =0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
