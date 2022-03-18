package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    //item은 jpa에 저장하기 전까지 id값이 없다(=완전히 새로 생성하는 객체)
    public void save(Item item) {
        if ((item.getId() == null)) {
            em.persist(item); //상품 신규 등록
        } else { //만약에 db에 이미 있는 값이라면 수정
            em.merge(item);
        }
    }



    public List<Item> findAll() {
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

}
