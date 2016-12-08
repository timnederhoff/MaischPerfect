package nl.timnederhoff.experiments;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HtmlController {

	@RequestMapping("/")
	public String index(Model model) {
		return "index";
	}
}
