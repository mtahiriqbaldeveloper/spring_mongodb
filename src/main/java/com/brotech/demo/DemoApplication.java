package com.brotech.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository studentRepository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"Germany",
					"koblenz",
					"56070"
			);
			String mail = "tahir@yahoo.com";
			Student student = new Student(
					"tahir",
					"iqbal",
					mail,
					Gender.MALE,
					address,
					List.of("maths", "computer science", "web science"),
					BigDecimal.TEN,
					LocalDateTime.now()
			);

			//usingMongoTemplateAndQuery(studentRepository, mongoTemplate, mail, student);

			studentRepository.findStudentByEmail(mail)
					.ifPresentOrElse(s -> {
						System.out.println(student + "already exists");
					}, () -> {
						System.out.println("inserting student " + student);
						studentRepository.insert(student);
					});
		};
	}
// mongo template
private static void usingMongoTemplateAndQuery(StudentRepository studentRepository, MongoTemplate mongoTemplate, String mail, Student student) {
	Query query = new Query();
	query.addCriteria(Criteria.where("email").is(mail));
	List<Student> students = mongoTemplate.find(query, Student.class);


	if (students.size() > 1) {
		throw new IllegalStateException("found many students with email" + mail);
	}
	if (students.isEmpty()) {
		System.out.println("inserting student " + student);
		studentRepository.insert(student);
	} else {
		System.out.println(student + "already exists");
	}
}
}
