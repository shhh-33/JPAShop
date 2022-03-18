package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    //상품 공통 속성
    private Long id; //상품 수정해야해서

    private String name;
    private int price; //가격
    private int stockQuantity; //재고

    //책 속성
    private String author;
    private String isbn;
}