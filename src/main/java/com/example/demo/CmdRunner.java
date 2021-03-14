package com.example.demo;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.data.nosql.Customer;
import com.example.demo.data.nosql.CustomerDao;
import com.example.demo.data.struc.User;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.unstruc.Product;
import com.example.demo.data.unstruc.ProductDao;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CmdRunner implements CommandLineRunner {

	@Autowired
	private UserDao userRepository;

	@Autowired
	private ProductDao prodRepository;

	@Autowired
	private CustomerDao custRepository;

	@Override
	public void run(String... args) throws Exception {
		return args -> {
			log.debug("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				Package packageName = ctx.getBean(beanName).getClass().getPackage();
				if (packageName != null && packageName.getName().contains("demo"))
					log.debug(beanName);
			}

			//userRepository.deleteAll();
			final long i = 10;
			Stream.of("amit kumar", "sumit singh", "ravi kishan").forEach(name -> {
				String[] usrName = name.split(" ");
				User user = new User(null, usrName[0], usrName[1], (int) (50 * Math.random() + 20));
				userRepository.save(user);
			});
			userRepository.findAll().forEach(System.out::println);

			//prodRepository.deleteAll();
			final long j = 100;
			Stream.of("pen", "paper", "pencil").forEach(prodName -> {
				Product prod = new Product(null, prodName, (int) (50 * Math.random() + 100));
				prodRepository.save(prod);
			});
			prodRepository.findAll().forEach(System.out::println);

			//custRepository.deleteAll();
			Stream.of("Alice Smith", "Bob Smith").forEach(name -> {
				String[] custName = name.split(" ");
				custRepository.save(new Customer(null, custName[0], custName[1]));
			});
			custRepository.findAll().forEach(System.out::println);

			log.debug("All data has been loaded by Cmd-line-runner");
		};
	};

}
