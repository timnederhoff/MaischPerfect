package nl.timnederhoff.tools.maischperfect.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Recipe {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	private String name;
	private String description;

	public Recipe() {
	}

	public Recipe(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}


	@Override
	public String toString() {
		return "Recipe{" +
				"id=" + id +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
