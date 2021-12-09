package ro.unibuc.springlab8example1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Null
    private Long id;

    @Size(min = 1, max = 50)
    private String username;

    @Size(min = 1, max = 100)
    private String fullName;

    // it is not a proper validation, added just to be there
    @Pattern(regexp = "[0-9]{13}")
    private String cnp;

    @Min(1)
    private Integer age;

    @Size(max=150)
    private String otherInformation;
}
