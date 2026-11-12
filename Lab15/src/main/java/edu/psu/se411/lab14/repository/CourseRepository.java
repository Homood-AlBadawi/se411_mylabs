package edu.psu.se411.lab14.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import edu.psu.se411.lab14.model.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long>, PagingAndSortingRepository<Course, Long> {

}
