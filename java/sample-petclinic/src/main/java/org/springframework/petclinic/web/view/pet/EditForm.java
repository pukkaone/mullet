/*
Copyright (c) 2012, Chin Huang
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.springframework.petclinic.web.view.pet;

import com.github.pukkaone.mullet.ModelDecorator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.petclinic.domain.Pet;
import org.springframework.petclinic.domain.PetType;

/**
 * Decorates model for pet edit form.
 */
public class EditForm extends ModelDecorator {

    public static class PetTypeOption {
        public String selected;
        public Integer value;
        public String text;

        public PetTypeOption(
                String selected, Integer value, String text)
        {
            this.selected = selected;
            this.value = value;
            this.text = text;
        }
    }

    public List<PetTypeOption> getTypes() {
        List<PetTypeOption> typeOptions = new ArrayList<PetTypeOption>();

        Pet pet = (Pet) getObject("pet");
        PetType currentType = pet.getType();
        Integer currentTypeId = (currentType != null)
                ? currentType.getId() : null;

        @SuppressWarnings("unchecked")
        Collection<PetType> types = (Collection<PetType>) getObject("types");
        for (PetType type : types) {
            typeOptions.add(new PetTypeOption(
                    (type.getId().equals(currentTypeId)) ? "selected" : null,
                    type.getId(),
                    type.getName()));
        }
        return typeOptions;
    }
}
