package org.springframework.samples.petclinic.web;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.samples.petclinic.Clinic;
import org.springframework.samples.petclinic.Owner;
import org.springframework.samples.petclinic.util.BindingResultUtils;
import org.springframework.samples.petclinic.validation.OwnerValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Handles owner actions.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@RequestMapping("/owner")
@SessionAttributes(types = Owner.class)
public class OwnerController {

    @Autowired
	private Clinic clinic;

    @Autowired
    private MessageSource messageSource;

	/**
     * Sets URLs in owner so the view can render them.
     *
     * @param owner
     *            owner to update
     */
	private void setUrls(Owner owner) {
	    String entityUrl = "owner/" + owner.getId();
        owner.setShowUrl(entityUrl);
        owner.setEditUrl(entityUrl + "/edit");
	}

    /**
     * Shows owner search form.
     *
     * @param model
     *            enriched with empty form backing object
     * @return view name
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String showSearchForm(Model model) {
        model.addAttribute(new Owner());
        return "owner/search";
    }

    /**
     * Performs search.
     *
     * @param owner
     * @param bindingResult
     * @param model
     * @return view name
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String search(
            @ModelAttribute("owner") Owner owner,
            BindingResult bindingResult,
            Model model)
    {
        // allow parameterless GET request for /owners to return all records
        if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies broadest possible search
        }

        String viewName;

        // find owners by last name
        Collection<Owner> owners = this.clinic.findOwners(owner.getLastName());
        for (Owner curOwner : owners) {
            setUrls(curOwner);
        }

        if (owners.size() < 1) {
            // no owners found
            bindingResult.rejectValue("lastName", "notFound", "not found");
            viewName = "owner/search";
        } else if (owners.size() > 1) {
            // multiple owners found
            model.addAttribute("selections", owners);
            viewName = "owner/list";
        } else {
            // 1 owner found
            owner = owners.iterator().next();
            viewName = "redirect:/owner/" + owner.getId();
        }

        model.addAttribute(
                "allErrors",
                BindingResultUtils.getErrorMessages(
                        bindingResult, messageSource));

        return viewName;
    }

	/**
	 * Displays an owner.
	 *
	 * @param ownerId the ID of the owner to display
	 * @return view name
	 */
	@RequestMapping(value = "/{ownerId}", method = RequestMethod.GET)
	public String show(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = clinic.loadOwner(ownerId);
		setUrls(owner);
		model.addAttribute(owner);

		model.addAttribute("addPetUrl", "owner/" + ownerId + "/pet/new");

		return "owner/show";
	}

    /**
     * Shows add form.
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String showAddForm(Model model) {
        Owner owner = new Owner();
        setUrls(owner);
        model.addAttribute(owner);
        return "owner/edit";
    }

    /**
	 * Shows edit form.
	 */
	@RequestMapping(value = "/{ownerId}/edit", method = RequestMethod.GET)
    public String showEditForm(
            @PathVariable("ownerId") int ownerId,
            Model model)
	{
        Owner owner = clinic.loadOwner(ownerId);
        setUrls(owner);
        model.addAttribute(owner);
        return "owner/edit";
    }

    /**
     * Saves form data.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(
            @ModelAttribute("owner") Owner owner,
            BindingResult bindingResult,
            Model model,
            SessionStatus sessionStatus)
    {
        new OwnerValidator().validate(owner, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "allErrors",
                    BindingResultUtils.getErrorMessages(
                            bindingResult, messageSource));
            return "owner/edit";
        } else {
            clinic.storeOwner(owner);
            sessionStatus.setComplete();
            return "redirect:/owner/" + owner.getId();
        }
    }
}
