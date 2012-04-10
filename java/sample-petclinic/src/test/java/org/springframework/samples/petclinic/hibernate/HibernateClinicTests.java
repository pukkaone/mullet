package org.springframework.samples.petclinic.hibernate;

import org.springframework.petclinic.repository.AbstractClinicTests;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * <p>
 * Integration tests for the {@link HibernateClinic} implementation.
 * </p>
 * <p>
 * "HibernateClinicTests-context.xml" determines the actual beans to test.
 * </p>
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@ContextConfiguration
@DirtiesContext
public class HibernateClinicTests extends AbstractClinicTests {

}
