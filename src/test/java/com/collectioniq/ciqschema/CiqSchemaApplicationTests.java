package com.collectioniq.ciqschema;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = CiqSchemaApplication.class)
@Rollback
@FlywayTest
class CiqSchemaApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setup() {
		jdbcTemplate.execute("use ciqtest");
	}

	@Test
	public void verifyBrand() throws Exception {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) from brand", Integer.class), is(0));
	}

	@AfterEach
	void teardown() {
		jdbcTemplate.execute("drop schema IF EXISTS ciqtest");
	}

}
