package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());//컨트롤러에서 뷰로 넘어갈때 데이터를 실어서 넘긴다.
        //form을 그냥 열고 빈화면을 가지고 간다.
        // (이름 내가 지어준것, 데이터)
        //memberForm 넘겼기 때문에 화면에서 new MemberForm()이라는 객체에 접근할 수 있다.
        return "members/createMemberForm"; //get방식으로 열린다
    }

    /*
    회원 등록 : 오류 처리
     */
    @PostMapping("/members/new") //실제로 등록
    //@Valid : MemberForm 어노테이션 적용되게
    //엔티티 그대로 안받고 이렇게 받는 이유 : 필요한 것만 뽑아 쓰는게 깔끔함
    public String create(@Valid MemberForm form , BindingResult result){

        //BindingResult result 오류가 여기 담겨서 실행이 된다
        if(result.hasErrors()){ //에러가 발생한 것을 인지하고
            return "members/createMemberForm"; //여기로 이동함
            //post로 보냈지만 다시 get으로 이동됐다
            //타임리프랑 스프링이 인티그레이션..되어잇어.. 끌고 갈 수 있음
        }

        Address address = new Address(form.getCity(),form.getStreet(),form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/"; //저장 후 재로딩되면 좋지 않으니까 redirect해서 index로 넘김

    }

    /*
    회원목록
     */
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers(); //ctrl +alt + v
        model.addAttribute("members", members); //(키,값)
        return "members/memberList";

    }


}
