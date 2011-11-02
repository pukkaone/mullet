package org.springframework.samples.petclinic;

/**
 * Simple JavaBean domain object with an id property.
 * Used as a base class for objects needing this property.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
public class BaseEntity {

	private Integer id;
    private String editUrl;
	private String showUrl;

    public Integer getId() {
        return id;
    }

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isNew() {
		return (this.id == null);
	}

    public String getEditUrl() {
        return editUrl;
    }

    public void setEditUrl(String editUrl) {
        this.editUrl = editUrl;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String url) {
        this.showUrl = url;
    }
}
