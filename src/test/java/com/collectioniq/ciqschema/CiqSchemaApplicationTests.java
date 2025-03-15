package com.collectioniq.ciqschema;

import org.flywaydb.test.FlywayTestExecutionListener;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = CiqSchemaApplication.class)
@Rollback
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
@FlywayTest
class CiqSchemaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setup() {
		jdbcTemplate.execute("use ciq");
	}

	@FlywayTest(invokeCleanDB = false)
	@Test
	public void verifyBrand() throws Exception {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) from brand", Integer.class), is(0));
	}

}
