package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 상품 등록
     */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }


    /**
     * 
     *상품수정
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId); //id를 기반으로 영속상태인 아이템을 찾아왔다

        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        
    }

    /*
    @Transactional
    void update(Long itemId, Item param) { //param: 파리미터로 넘어온 준영속 상태의 엔티티
        Item findItem = itemRepository.findOne(itemId); //id를 기반으로 영속상태인 아이템을 찾아왔다
        findItem.setPrice(param.getPrice()); //param에서 데이터 꺼내서 수정한다.
        //itemRepository.save(findItem); 할 필요 없다.

         /*
        findItem은 영속상태이다. param으로 값을 세팅한 다음
        스프링의 @Transactional에 의해서 커밋이 된다.
        변경된애를 다 찾아서 바뀐내용들 DB에 업데이트 쿼리 날아가서 처리된다.
        이게 변경감지이다.

        merge는 위에 작성한 이 코드를 한줄로 다 해준다.
        하지만 모든 값을 다 수정한다. 안건들인값은 null로 수정된다.
        그러니 쓰지말것.. 변경감지로 쓸것
        내가 변경하고자 하는 값만 set으로 수정할것..
        사실 이것도 지저분함 메서드 만들어서 수정할것..


    }*/



    /**
     * 상품 목록
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
     * 상품 수정할때 쓴다.
     * id받아서 수정하려고
     */
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}