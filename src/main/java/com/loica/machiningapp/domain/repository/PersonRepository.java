package com.loica.machiningapp.domain.repository;

import com.loica.machiningapp.domain.model.Person;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {

  @Query("select u from Person u " +
      "where lower(u.name) like lower(concat('%', :searchTerm, '%')) " +
      "or lower(u.lastName) like lower(concat('%', :searchTerm, '%'))")
  List<Person> search(@Param("searchTerm") String searchTerm);

}
