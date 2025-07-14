package gift.validator;

import gift.dto.NewMemberRequest;
import gift.repository.MemberRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class NewMemberRequestValidator implements ConstraintValidator<ValidNewMemberRequest, NewMemberRequest> {

    private final MemberRepository memberRepository;

    public NewMemberRequestValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean isValid(NewMemberRequest memberRequest, ConstraintValidatorContext context) {
        if (memberRequest == null) {
            return true;
        }

        String email = memberRequest.email();
        if (email != null && memberRepository.checkEmailExists(email)) {
            addConstraintViolation(context, "email", "이미 존재하는 이메일입니다.");
            return false;
        }

        String password = memberRequest.password();
        if (password != null && (password.length() < 15 || password.length() > 64)) {
            addConstraintViolation(context, "password", "비밀번호는 15자 이상 64자 이내여야 합니다."); // This validation is for both register and update
            return false;
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String fieldName, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
