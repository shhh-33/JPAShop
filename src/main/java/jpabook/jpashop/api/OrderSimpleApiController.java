package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 성능 최적화
 * xToOne(ManyToOne, OneToOne)
 * <Order>
 * Order -> Member - ManyToOne
 * Order -> Delivery - OneToOne
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    /*V1. 엔티티 직접 노출
    - 양방향연관관계 참조하면 무한루프 -> @JsonIgnore
    order -> member , member -> orders 양방향 연관관계를 계속 로딩하게 된다.
    따라서 @JsonIgnore 옵션을 한곳에 주어야 한다

    - Hibernate5Module 모듈 등록, LAZY=null 처리

     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

}
