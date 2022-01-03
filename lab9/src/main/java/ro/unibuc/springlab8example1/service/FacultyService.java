package ro.unibuc.springlab8example1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.springlab8example1.domain.Faculty;
import ro.unibuc.springlab8example1.dto.FacultyDto;
import ro.unibuc.springlab8example1.exception.FacultyNotFoundException;
import ro.unibuc.springlab8example1.mapper.FacultyMapper;
import ro.unibuc.springlab8example1.repository.FacultyRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private FacultyMapper facultyMapper;

    public FacultyDto create(FacultyDto facultyDto) {
        Faculty faculty = facultyRepository.save(facultyMapper.mapToEntity(facultyDto));

        return facultyMapper.mapToDto(faculty);
    }

    public FacultyDto getById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException("Faculty not found"));

        return facultyMapper.mapToDto(faculty);
    }

    public List<FacultyDto> getAll() {
        return facultyRepository.findAll().stream().map(facultyMapper::mapToDto).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        Faculty faculty = facultyMapper.mapToEntity(getById(id));
        facultyRepository.delete(faculty);
    }
}
