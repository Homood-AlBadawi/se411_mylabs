package psu.se411.lab16.repository;

import org.springframework.data.repository.CrudRepository;

import psu.se411.lab16.model.Course;

public interface CourseRepository extends CrudRepository<Course, Integer> {

}
