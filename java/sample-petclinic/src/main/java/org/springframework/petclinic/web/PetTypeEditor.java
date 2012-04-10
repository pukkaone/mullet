package org.springframework.petclinic.web;

import org.springframework.petclinic.repository.Clinic;

import org.springframework.petclinic.domain.PetType;

import java.beans.PropertyEditorSupport;


/**
 * @author Mark Fisher
 * @author Juergen Hoeller
 */
public class PetTypeEditor extends PropertyEditorSupport {

	private final Clinic clinic;


	public PetTypeEditor(Clinic clinic) {
		this.clinic = clinic;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		for (PetType type : this.clinic.getPetTypes()) {
			if (type.getName().equals(text)) {
				setValue(type);
			}
		}
	}

}
