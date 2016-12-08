package nl.timnederhoff.tools.maischperfect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SpringBootApplication
public class MaischPerfectApplication {

	private static int[][] maischModel = new int[][] {{12,30}, {9,45}, {10,51}, {11,60}};
	private static BrewProcess brewProcess;

	public static void main(String[] args) {
		brewProcess = new BrewProcess(maischModel);
		SpringApplication.run(MaischPerfectApplication.class, args);
	}

	@RequestMapping("/templog")
	private List<Integer> getTempLog() {
		return brewProcess.getTempLog();
	}

	@RequestMapping("/appliedmodel")
	private List<int[]> getAppliedModel() {
		return brewProcess.getAppliedModel();
	}
}
