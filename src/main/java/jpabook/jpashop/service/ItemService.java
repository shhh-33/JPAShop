package jpabook.jpashop.service;

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
     * 상품 목록
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    /**
    상품 수정할때 쓴다.
     id받아서 수정하려고
     */
    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}