package ru.patrakhin.RestProject.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.patrakhin.RestProject.PeopleService;
import ru.patrakhin.RestProject.dto.PersonDTO;
import ru.patrakhin.RestProject.models.Person;
import ru.patrakhin.RestProject.util.PersonErrorResponse;
import ru.patrakhin.RestProject.util.PersonNotCreatedException;
import ru.patrakhin.RestProject.util.PersonNotFoundException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService,
                            ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getPeople(){
        return peopleService.findAll().stream().map(this::converToPersonDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id){
        return converToPersonDTO(peopleService.findOne(id));
    }


    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = (List<FieldError>) bindingResult.getFieldError();
            for (FieldError error: errors){
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        peopleService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO converToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }

}
