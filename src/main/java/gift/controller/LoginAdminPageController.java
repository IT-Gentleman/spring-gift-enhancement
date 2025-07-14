package gift.controller;

import gift.dto.LoginMemberRequest;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class LoginAdminPageController {

    private final MemberService memberService;

    public LoginAdminPageController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginAdminPage(Model model) {
        model.addAttribute("member", LoginMemberRequest.empty());
        return "admin/login-form";
    }

    @PostMapping("/login")
    public String loginAdminPage(
            @Valid @ModelAttribute LoginMemberRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", request);
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> "- " + error.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            model.addAttribute("message", "Invalid input. Check again.\n" + errorMessages);
            return "admin/login-form";
        }

        try {
            String token = memberService.login(request.email(), request.password());
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            redirectAttributes.addFlashAttribute("message", "Login successful.");
            return "redirect:/admin";

        } catch (ResponseStatusException e) {
            model.addAttribute("member", request);
            model.addAttribute("message", "Login failed: Invalid credentials.");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "admin/login-form";
        } catch (Exception e) {
            model.addAttribute("member", request);
            model.addAttribute("message", "An unexpected error occurred.");
            return "admin/login-form";
        }
    }

    @PostMapping("/logout")
    public String logoutAdminPage(
            RedirectAttributes redirectAttributes,
            HttpServletResponse response
    ) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0); // 쿠키 삭제
        cookie.setPath("/");
        response.addCookie(cookie);
        redirectAttributes.addFlashAttribute("message", "Logout successful.");
        return "redirect:/admin/login";
    }
}

