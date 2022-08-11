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
import java.util.List;
import java.util.stream.Collectors;


//@Controller @ResponseBody ->RestController
@RestController //데이터 자체를 json,xml로 보내려고
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원조회 api
     */

    /*
     * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     * - 기본적으로 엔티티의 모든 값이 노출된다.
     * - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
     * - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데,
     *   한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
     * <결론>
     * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
     */
    //V1: 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return  memberService.findMembers();
    }

    //V2: 응답 값으로 엔티티가 아닌 별도의 DTO 사용
    //리스트에서 오브젝트로 바꿈
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers =memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto((m.getName()))) //엔티티 수정하면 오류(스펙 안바뀜)
                .collect((Collectors.toList()));

        return new Result(collect.size(),collect);
    }

    //감싸서 반환..유연성 높임
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name; //api 스펙에 노출하고 싶은 것만
    }







    /**
     * 회원등록 api
     */
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
