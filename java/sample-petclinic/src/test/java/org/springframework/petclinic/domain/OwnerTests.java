package org.springframework.petclinic.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.springframework.petclinic.domain.Owner;
import org.springframework.petclinic.domain.Pet;

import org.junit.Test;

/**
 * JUnit test for the {@link Owner} class.
 *
 * @author Ken Krebs
 */
public class OwnerTests {

	@Test
	public void testHasPet() {
		Owner owner = new Owner();
		Pet fido = new Pet();
		fido.setName("Fido");
		assertNull(owner.getPet("Fido"));
		assertNull(owner.getPet("fido"));
		owner.addPet(fido);
		assertEquals(fido, owner.getPet("Fido"));
		assertEquals(fido, owner.getPet("fido"));
	}

}
