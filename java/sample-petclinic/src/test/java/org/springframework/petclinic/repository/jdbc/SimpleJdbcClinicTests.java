package org.springframework.petclinic.repository.jdbc;

import org.springframework.petclinic.repository.AbstractClinicTests;

import org.springframework.petclinic.repository.jdbc.SimpleJdbcClinic;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * <p>
 * Integration tests for the {@link SimpleJdbcClinic} implementation.
 * </p>
 * <p>
 * "SimpleJdbcClinicTests-context.xml" determines the actual beans to test.
 * </p>
 *
 * @author Thomas Risberg
 */
@ContextConfiguration
@DirtiesContext
public class SimpleJdbcClinicTests extends AbstractClinicTests {

}
