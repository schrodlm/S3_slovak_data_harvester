package cz.trixi.schrodlm.slovakcompany.model;

import java.time.LocalDateTime;

public record BatchModel(String batchName, LocalDateTime exportDate, String pathToFile) {

}
