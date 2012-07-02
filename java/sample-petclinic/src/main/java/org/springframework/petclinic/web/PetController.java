
package org.springframework.petclinic.web;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.petclinic.domain.Owner;
import org.springframework.petclinic.domain.Pet;
import org.springframework.petclinic.domain.PetType;
import org.springframework.petclinic.repository.Clinic;
import org.springframework.petclinic.util.BindingResultUtils;
import org.springframework.petclinic.validation.PetValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles pet actions.
 */
@Controller
@RequestMapping("/pet")
@SessionAttributes("pet")
public class PetController {

    @Autowired
	private Clinic clinic;

    @Autowired
    private MessageSource messageSource;

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.clinic.getPetTypes();
	}

	private PetType findPetTypeFromId(Integer typeId) {
	    for (PetType petType : clinic.getPetTypes()) {
	        if (petType.getId().equals(typeId)) {
	            return petType;
	        }
	    }

	    throw new IllegalArgumentException("Unknown pet type ID " + typeId);
	}

	/**
	 * Shows add form.
	 * @param ownerId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String showAddForm(
	        @RequestParam("ownerId") int ownerId,
	        Model model)
	{
		Owner owner = this.clinic.loadOwner(ownerId);
		Pet pet = new Pet();
		owner.addPet(pet);
		model.addAttribute("pet", pet);
		return "pet/EditForm";
	}

	/**
	 * Saves form data.
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
            @RequestParam("ownerId") int ownerId,
            @RequestParam("typeId") int typeId,
	        @ModelAttribute("pet") Pet pet,
	        BindingResult bindingResult,
	        Model model,
	        SessionStatus status)
	{
	    pet.setType(findPetTypeFromId(typeId));

		new PetValidator().validate(pet, bindingResult);
		if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "allErrors",
                    BindingResultUtils.getErrorMessages(
                            bindingResult, messageSource));
			return "pet/EditForm";
		}

		clinic.storePet(pet);

		status.setComplete();
		return "redirect:/owner/" + pet.getOwner().getId();
	}

    /**
     * Custom handler for displaying an list of visits.
     *
     * @param petId the ID of the pet whose visits to display
     * @return a ModelMap with the model attributes for the view
     */
    @RequestMapping(value = "/{petId}/visits", method = RequestMethod.GET)
    public ModelAndView visitsHandler(@PathVariable int petId) {
        ModelAndView mav = new ModelAndView("visits");
        mav.addObject("visits", this.clinic.loadPet(petId).getVisits());
        return mav;
    }
}
