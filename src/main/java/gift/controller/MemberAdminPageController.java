package gift.controller;

import gift.dto.CreateMemberRequest;
import gift.dto.MemberResponse;
import gift.dto.UpdateMemberRequest;
import gift.entity.Member;
import gift.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/members")
public class MemberAdminPageController {

    private final MemberService memberService;

    public MemberAdminPageController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public String getMembers(Model model) {
        List<Member> memberList = memberService.getMemberList();
        List<MemberResponse> response = memberList.stream()
                .map(member -> MemberResponse.from(member))
                .toList();
        model.addAttribute("members", response);
        return "admin/member-list";
    }

    @GetMapping("/new")
    public String createMember(Model model) {
        model.addAttribute("member", CreateMemberRequest.empty());
        model.addAttribute("memberId", null);
        return "admin/member-form";
    }

    @PostMapping
    public String createMember(
            @Valid @ModelAttribute CreateMemberRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberId", null);
            model.addAttribute("member", request);
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> "- " + error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            model.addAttribute("message", "Invalid input. Check again.\n" + errorMessages);
            return "admin/member-form";
        }
        Member createdMember = memberService.createMember(request.email(), request.password());
        model.addAttribute("member", UpdateMemberRequest.from(createdMember));
        redirectAttributes.addFlashAttribute("message", "Member created successfully.");
        return "redirect:/admin/members/" + createdMember.getIdentifyNumber();
    }

    @GetMapping("/{id}")
    public String getMember(
            @PathVariable("id") Long identifyNumber,
            Model model
    ) {
        Member member = memberService.getMemberById(identifyNumber);
        model.addAttribute("member", UpdateMemberRequest.from(member));
        model.addAttribute("memberId", identifyNumber);
        return "admin/member-form";
    }

    @PutMapping("/{id}")
    public String updateMember(
            @PathVariable("id") Long identifyNumber,
            @Valid @ModelAttribute UpdateMemberRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("memberId", identifyNumber);
            model.addAttribute("member", request);
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> "- " + error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            model.addAttribute("message", "Invalid input. Check again.\n" + errorMessages);
            return "admin/member-form";
        }
        Member updatedMember = memberService.updateSelectivelyMember(
                identifyNumber,
                request.email(),
                request.password(),
                request.authority()
        );
        model.addAttribute("member", UpdateMemberRequest.from(updatedMember));
        redirectAttributes.addFlashAttribute("message", "Member updated successfully.");
        return "redirect:/admin/members/" + identifyNumber;
    }

    @DeleteMapping("/{id}")
    public String deleteMember(
            @PathVariable("id") Long identifyNumber,
            RedirectAttributes redirectAttributes
    ) {
        memberService.deleteMember(identifyNumber);
        redirectAttributes.addFlashAttribute("message", "Member deleted successfully.");
        return "redirect:/admin/members";
    }
}
