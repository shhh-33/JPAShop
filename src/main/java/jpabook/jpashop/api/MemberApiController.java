package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;


/**
 * 회원등록 api
 */
//@Controller @ResponseBody ->RestController
@RestController //데이터 자체를 json,xml로 보내려고
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //회원등록
    //문제발생 : 엔티티를 수정하면 api 스펙이 변해버릴 수 있다. 그렇기 때문에 api 스펙을 위한 별도의 dto 만들어야한다.
    @PostMapping("/api/v1/members")       //body에서 온 json 데이터를 매핑해서 member에 다 넣는다
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //Valid : Member 검증 (ex: NotEmpty)
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    /*
    2번 장점
    Member엔티티에서 이름명을 바꾼다.. 그럼 오류난다 -> api스펙에서만 수정하면 된다.
     */

    //이렇게 dto따로 만들어 버릴것
    //api 스펙 자체가 name만 받게 정의되어있구나를 알 수 있다..
   @Data
    static class CreateMemberRequest {
       @NotEmpty //필요한 vaildation넣기
        private String name;
    }


    @Data
    static class CreateMemberResponse {
        private Long id;

        //생성자만들기..(generate)
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
