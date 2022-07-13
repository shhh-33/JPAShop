package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    /*
     등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     문제발생
      - 엔티티를 수정하면 api 스펙이 변해버릴 수 있다. 그렇기 때문에  API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다

      - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
      - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
      - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다
     */
    @PostMapping("/api/v1/members")       //body에서 온 json 데이터를 매핑해서 member에 다 넣는다
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { //Valid : Member 검증 (ex: NotEmpty)
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*

     등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
      -> CreateMemberRequest 를 Member 엔티티 대신에 RequestBody와 매핑한다.
     <장점>
    Member엔티티에서 이름명을 바꾼다.. 그럼 오류난다 -> api스펙에서만 수정하면 된다.

    - 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
    - 엔티티와 API 스펙을 명확하게 분리할 수 있다.
    - 엔티티가 변해도 API 스펙이 변하지 않는다.
    */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}") //put은 전체업데이트, post는 부분 업데이트
    public UpdateMemberResponse updateMemberV2( //업데이트용 응답 dto
                                                @PathVariable("id") Long id,
                                                @RequestBody @Valid UpdateMemberRequest request) { //업데이트용 요청 dto 별도로 만듬

        memberService.update(id, request.getName()); //수정을 여기서 끝내고(수정, 조회 분리해서 유지보수성 증대)

        Member findMember =memberService.findOne(id);  //수정한거 조회

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }


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
