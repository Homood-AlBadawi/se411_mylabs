package edu.psu.se411.lab13.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public record Product(
			@Id Integer id,
			String name,
			String description,
			BigDecimal price
		) {}
