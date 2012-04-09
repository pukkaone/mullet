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
package org.springframework.samples.petclinic.web.view.owner;

import com.github.pukkaone.mullet.ModelDecorator;
import java.util.ArrayList;
import java.util.List;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;

/**
 * Decorates model for owner detail view.
 */
public class Detail extends ModelDecorator {

    public OwnerDecorator getOwner() {
        Owner owner = (Owner) getObject("owner");
        return new OwnerDecorator(owner);
    }

    public List<PetDecorator> getPets() {
        Owner owner = (Owner) getObject("owner");

        List<PetDecorator> pets = new ArrayList<PetDecorator>();
        for (Pet pet : owner.getPets()) {
            pets.add(new PetDecorator(pet));
        }
        return pets;
    }
}
