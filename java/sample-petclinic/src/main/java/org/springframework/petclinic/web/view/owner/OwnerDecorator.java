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
package org.springframework.petclinic.web.view.owner;

import org.springframework.petclinic.domain.Owner;
import org.springframework.petclinic.domain.Pet;

import java.util.List;

/**
 * Decorates Owner class with view specific logic.
 */
public class OwnerDecorator extends Owner {

    private Owner delegate;
    private String entityUrl;

    public OwnerDecorator(Owner delegate) {
        this.delegate = delegate;
        this.entityUrl = "owner/" + getId();
    }

    @Override
    public Integer getId() {
        return delegate.getId();
    }

    @Override
    public boolean isNew() {
        return delegate.isNew();
    }

    @Override
    public String getFirstName() {
        return delegate.getFirstName();
    }

    @Override
    public String getLastName() {
        return delegate.getLastName();
    }

    @Override
    public String getAddress() {
        return delegate.getAddress();
    }

    @Override
    public String getCity() {
        return delegate.getCity();
    }

    @Override
    public String getTelephone() {
        return delegate.getTelephone();
    }

    @Override
    public List<Pet> getPets() {
        return delegate.getPets();
    }

    /**
     * Gets URL which renders details of the entity.
     *
     * @return URL
     */
    public String getDetailUrl() {
        return entityUrl;
    }

    /**
     * Gets URL which renders form to edit the entity.
     *
     * @return URL
     */
    public String getEditUrl() {
        return entityUrl + "/edit";
    }

    /**
     * Gets URL which renders form to add a pet.
     *
     * @return URL
     */
    public String getAddPetUrl() {
        return entityUrl + "/pet/new";

    }
}
