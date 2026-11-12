package psu.se411.lab16.controller;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import psu.se411.lab16.model.Course;
import psu.se411.lab16.repository.CourseRepository;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

	private CourseRepository courseRepository;

	private CourseController(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@GetMapping()
	private ResponseEntity<Iterable<Course>> findAll() {
		try {
			return ResponseEntity.ok(courseRepository.findAll());
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping()
	private ResponseEntity<Void> createCourse(@RequestBody Course newCourse, UriComponentsBuilder ucb) {
		try {
			Course course = courseRepository.save(newCourse);
			URI newCourseLocation = ucb.path("/api/v1/courses/{id}")
					.buildAndExpand(newCourse.id())
					.toUri();
			return ResponseEntity.created(newCourseLocation).build();
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/{courseId}")
	private ResponseEntity<Course> findById(@PathVariable Integer courseId) {
		try {
			Optional<Course> course = courseRepository.findById(courseId);
			if (course.isPresent()) {
				return ResponseEntity.ok(course.get());
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PutMapping("/{courseId}")
	private ResponseEntity<Void> updateCourse(@PathVariable Integer courseId, @RequestBody Course update) {
		try {
			Optional<Course> course = courseRepository.findById(courseId);
			if (course.isPresent()) {
				Course updatedCourse = new Course(courseId, update.code(), update.name(), update.description(), update.credits());
				courseRepository.save(updatedCourse);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/{courseId}")
	private ResponseEntity<Void> deleteCourse(@PathVariable Integer courseId) {
		try {
			Optional<Course> course = courseRepository.findById(courseId);
			if (course.isPresent()) {
				courseRepository.deleteById(courseId);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception ex) {
			return ResponseEntity.internalServerError().build();
		}
	}

}
