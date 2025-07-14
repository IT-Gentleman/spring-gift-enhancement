package gift.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UpdateMemberRequestValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUpdateMemberRequest {
    String message() default "Invalid member request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
