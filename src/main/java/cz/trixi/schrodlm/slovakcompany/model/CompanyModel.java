package cz.trixi.schrodlm.slovakcompany.model;

import java.time.LocalDateTime;

public record CompanyModel(String ico, LocalDateTime dbModification, LocalDateTime termination) {


}
