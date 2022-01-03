package ro.unibuc.springlab8example1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.springlab8example1.dto.FacultyDto;
import ro.unibuc.springlab8example1.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculties")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @PostMapping
    public ResponseEntity<FacultyDto> create(@RequestBody FacultyDto facultyDto) {
        return ResponseEntity.ok(facultyService.create(facultyDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(facultyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<FacultyDto>> getAll() {
        return ResponseEntity.ok(facultyService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        facultyService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
