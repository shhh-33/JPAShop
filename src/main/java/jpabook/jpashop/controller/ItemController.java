package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * 상품 등록 클릭시
     */
    @GetMapping(value = "/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());  //이렇게 넘겨줘서 html에서 추적가능
        return "items/createItemForm";
    }


    /**
     * 상품 등록 (submit)
     */
    @PostMapping(value = "/items/new")
    public String create(BookForm form) {
        Book book = new Book();

        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);

        return "redirect:/items"; //저장된 책 목록으로
    }

    /**
     * 상품 목록
     */
    @GetMapping(value = "/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /**
     * 상품 수정 폼
     * 폼에서 수정데이터 입력 후 ->수정되어야하니 form 있어야함
     */
    @GetMapping("items/{itemID}/edit")
    public String updateItemForm(@PathVariable("itemID") Long itemId, Model model) {

        Book item = (Book) itemService.findOne(itemId); //item Id 받아서 수정

        BookForm form = new BookForm(); //form을 수정할때 book엔티티가 아니라 form을 보낼거임

        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);  //updateItemForm으로 보낸다.

        return "items/updateItemForm";
    }

    /**
     * 상품 수정
     */
    @PostMapping(value = "/items/{itemId}/edit") //post로 받았다.
    public String updateItem(@ModelAttribute("form") BookForm form) { //th:object="${form}" 넘어오게 (이름은 상관없)

        itemService.updateItem(form.getId(),form.getName(),form.getPrice() ,form.getStockQuantity());

        return "redirect:/items";

        /*
        bookform을 통해 데이터가 날아온다.
        새로운 book이 아니다 id(식별자)가 세팅되어있다 -> jpa(db)에 다녀온 애 이런것을 준영속객체라고 한다.
        JPA가 관리를 안한다..그래서 book.set(~) 해도 반영이 안돼
        그럼 도대체 어떻게 데이터를 변경할 수 있을까
         */

        //form을 받아와서 book으로 바꿨다 ->form은 웹계층에만 쓸려고

        /*
        복잡해서 이렇게 안쓴다.
        Book book = new Book();

        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book); //merge로 동작중
        */

    }


}