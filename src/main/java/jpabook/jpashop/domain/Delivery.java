package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Delivery { //배송엔티티

    @Id @GeneratedValue
    @Column(name ="delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy = "delivery" ,fetch = LAZY)
    private Order order;

    @Embedded
    private  Address address;

    @Enumerated(EnumType.STRING)
    //enum은 꼭 넣어야함
    // ordinal : 숫자로 들어감..1,2,3 이렇게 들어가서 중간에 뭐가 끼면 숫자가 밀려 꼭 String으로 써야해
    private DeliveryStatus status;  //READY ,COMP
}
