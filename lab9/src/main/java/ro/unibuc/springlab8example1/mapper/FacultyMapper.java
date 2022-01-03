package ro.unibuc.springlab8example1.mapper;

import org.mapstruct.Mapper;
import ro.unibuc.springlab8example1.domain.Faculty;
import ro.unibuc.springlab8example1.dto.FacultyDto;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    FacultyDto mapToDto(Faculty faculty);

    Faculty mapToEntity(FacultyDto facultyDto);
}
