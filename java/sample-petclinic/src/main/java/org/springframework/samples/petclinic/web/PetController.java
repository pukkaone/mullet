
package org.springframework.samples.petclinic.web;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.Pet;
import org.springframework.samples.petclinic.PetType;
import org.springframework.samples.petclinic.util.BindingResultUtils;
import org.springframework.samples.petclinic.validation.PetValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Handles pet actions.
 *
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owner/{ownerId}/pet")
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

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String showAddForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.clinic.loadOwner(ownerId);
		Pet pet = new Pet();
		owner.addPet(pet);
		model.addAttribute("pet", pet);
		return "pet/edit";
	}

	/**
	 * Saves form data.
	 *
	 * @param pet
	 * @param bindingResult
	 * @param sessionStatus
	 * @return view name
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String processSubmit(
	        @ModelAttribute("pet") Pet pet,
	        BindingResult bindingResult,
            Model model,
	        SessionStatus sessionStatus)
	{
		new PetValidator().validate(pet, bindingResult);
		if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "allErrors",
                    BindingResultUtils.getErrorMessages(
                            bindingResult, messageSource));
			return "pet/edit";
		} else {
			this.clinic.storePet(pet);
			sessionStatus.setComplete();
			return "redirect:/owner/" + pet.getOwner().getId();
		}
	}
}
