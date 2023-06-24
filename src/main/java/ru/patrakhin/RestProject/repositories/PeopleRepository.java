package ru.patrakhin.RestProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.patrakhin.RestProject.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
