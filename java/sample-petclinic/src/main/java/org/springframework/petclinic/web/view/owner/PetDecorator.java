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

import org.springframework.petclinic.domain.Pet;
import org.springframework.petclinic.domain.PetType;
import org.springframework.petclinic.domain.Visit;

import java.util.Date;
import java.util.List;

/**
 * Decorates Pet class with view specific logic.
 */
public class PetDecorator extends Pet {

    private Pet delegate;
    private String entityUrl;

    public PetDecorator(Pet delegate) {
        this.delegate = delegate;
        this.entityUrl = "pet/" + getId();
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
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Date getBirthDate() {
        return delegate.getBirthDate();
    }

    @Override
    public PetType getType() {
        return delegate.getType();
    }

    @Override
    public List<Visit> getVisits() {
        return delegate.getVisits();
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
     * Gets URL which renders form to add a visit.
     *
     * @return URL
     */
    public String getAddVisitUrl() {
        return entityUrl + "/visit/new";

    }

    /**
     * Gets URL which renders visit feed.
     *
     * @return URL
     */
    public String getVisitFeedUrl() {
        return entityUrl + "/visits.atom";

    }
}
