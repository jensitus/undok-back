package at.undok.auth.api;

import at.undok.auth.model.form.SecondFactorForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/service/second-factor-auth")
@PreAuthorize("hasRole('SECOND_FACTOR')")
public interface SecondFactorApi {

    @PostMapping("/second-factor")
    public ResponseEntity<?> secondFactor(@RequestBody SecondFactorForm secondFactorForm);

}
