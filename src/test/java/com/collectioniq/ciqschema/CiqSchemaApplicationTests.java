package com.collectioniq.ciqschema;

import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = CiqSchemaApplication.class)
@Rollback
@FlywayTest
class CiqSchemaApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String SCHEMA_NAME = "ciq";
	private static final String FLYWAY_TABLE_NAME = "flyway_schema_history";
	private static final List<String> tableList = List.of("brand");

	@Test
	public void verifySchema() {
		//todo: elaborate on this using an object mapper to validate a schema object, using a complex join of several
		//	tables in the information schema (exclude the flyway table)
		/*
		private static final ObjectMapper om = new ObjectMapper();
		e.g. Schema
				- name
				- List<Tables>
					Table
						- name
						- List<Columns>
							Column
								- column_name
								- column_default
								- is_nullable
								- data_type
								- character_maximum_length
						List<Contraints>
							Constraint
								- constraint_name
								- constraint_type
		 */


		//a schema should exist named 'ciq'
		assertThat(jdbcTemplate.queryForObject("SELECT count(*) from information_schema.schemata " +
				"where schema_name = '" + SCHEMA_NAME + "';", Integer.class), is(1));

		//all the tables in the table list should exist
		for (String table: tableList) {
			assertThat("A table (" + table + ") expected in the " + SCHEMA_NAME + " schema is not present.",
					jdbcTemplate.queryForObject("SELECT count(*) from information_schema.tables " +
							"where table_schema = '" + SCHEMA_NAME + "' AND table_name = '" + table + "';"
							, Integer.class), is(1));
		}

		//no tables outside the table list should exist
		assertThat(jdbcTemplate.queryForObject(" SELECT count(*) FROM information_schema.tables " +
				"WHERE table_schema = '" + SCHEMA_NAME + "' AND table_name <> '" + FLYWAY_TABLE_NAME + "';"
				, Integer.class), is(1));

	}

	@Test
	void contextLoads() {
	}

}
