package at.undok.undok.client.model.form;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CloseCaseForm {

    /**
     * The day the counselling actually ended. Null means today — the frontend pre-fills the
     * picker with today, but a case may also be closed through a replayed or scripted call.
     */
    private LocalDate endDate;

    /**
     * Organisation the client was referred on to. Optional: a case can be closed without one.
     */
    private String referredTo;

}
