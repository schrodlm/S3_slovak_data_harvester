package cz.trixi.schrodlm.slovakcompany.model;

import java.nio.file.Path;
import java.time.LocalDate;

public record BatchModel(String batchName, LocalDate exportDate, Path pathToFile) {

}
