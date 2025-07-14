package gift.validator;

import gift.dto.UpdateMemberRequest;
import gift.repository.MemberRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UpdateMemberRequestValidator implements ConstraintValidator<ValidUpdateMemberRequest, UpdateMemberRequest> {

    private final MemberRepository memberRepository;

    public UpdateMemberRequestValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean isValid(UpdateMemberRequest updateMemberRequest, ConstraintValidatorContext context) {
        if (updateMemberRequest == null) {
            return true;
        }

        Long identifyNumber = updateMemberRequest.identifyNumber();
        String email = updateMemberRequest.email();
        if (email != null && !memberRepository.findById(identifyNumber).orElseThrow().getEmail().equals(email) && memberRepository.checkEmailExists(email)) {
            addConstraintViolation(context, "email", "이미 존재하는 이메일입니다.");
            return false;
        }

        String password = updateMemberRequest.password();
        if (password != null && !password.isEmpty() && (password.length() < 15 || password.length() > 64)) {
            addConstraintViolation(context, "password", "비밀번호는 15자 이상 64자 이내여야 합니다.");
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
