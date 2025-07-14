package gift.handler;

import gift.controller.ProductAdminPageController;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice(assignableTypes = ProductAdminPageController.class)
public class ProductAdminPageControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleWebException(ResponseStatusException ex, Model model) {
        String errorMessage;
        switch (ex.getStatusCode()) {
            case HttpStatus.NOT_FOUND -> errorMessage = "Product not found : 유효하지 않은 상품ID로 접근하였습니다.\n";
            case HttpStatus.INTERNAL_SERVER_ERROR -> errorMessage = "Internal Server Error\n";
            default -> errorMessage = "Error Occurred\n";
        }
        model.addAttribute("errorMessage", errorMessage + ex.getMessage());
        return "error/custom-error";
    }
}