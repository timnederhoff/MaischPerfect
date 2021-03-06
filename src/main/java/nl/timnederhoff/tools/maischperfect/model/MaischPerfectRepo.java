package nl.timnederhoff.tools.maischperfect.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MaischPerfectRepo extends CrudRepository<Recipe, Long> {

	List<Recipe> findByName(String name);

}
