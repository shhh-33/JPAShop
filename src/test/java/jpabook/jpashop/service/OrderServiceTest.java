package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService; //OrderService 받아옴
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

       Book book = createBook("시골 JPA", 10000, 10); //이름, 가격, 재고

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //assertEquals(x, y,z)  객체 x와 y가 일치함을 확인 z :실제값
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.",10000 * orderCount,getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",8, book.getStockQuantity());


    }



    @Test(expected = NotEnoughStockException.class) //재고수량 초과되면 예외가 터져야함
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member= createMember();
        Item item = createBook("시골",10000,10); //재고 10개인데

        int orderCount =11; //11개 주문하면

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount); //잘못된거니까 예외터져야

        //then
        fail("재고 수량 부족 예외가 발생해야 하는데 여기로 오면 잘못된 코드다");
    }

   @Test
    public void 주문취소() throws Exception {
       //given : 이런게 주어짐
       Member member= createMember();
       Book item = createBook("시골",10000,10); //10개에서
       int orderCount =2; //2개주문문
       Long orderId = orderService.order(member.getId(), item.getId(), orderCount);  //주문한것 까지 주어졌다

       //when : 실제 테스트 할것
       orderService.cancelOrder(orderId); //취소를했으니

        //then
       Order getOrder = orderRepository.findOne(orderId);

       assertEquals("주문 취소시 상태는 CANCEL 이다.",OrderStatus.CANCEL, getOrder.getStatus());
       assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity()); //다시 열개되야
    }



    private Book createBook(String name, int Price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(Price);
        book.setStockQuantity(stockQuantity); //ctrl alt p
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123"));
        em.persist(member);
        return member;
    }


}