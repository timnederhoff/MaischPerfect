package nl.timnederhoff.tools.maischperfect;

import nl.timnederhoff.tools.maischperfect.model.TempRequest;
import nl.timnederhoff.tools.maischperfect.model.TempResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@SpringBootApplication
public class MaischPerfectApplication {

	private static List<Integer[]> maischModel = new ArrayList<>();

	private static BrewProcess brewProcess;

	public static void main(String[] args) {
		maischModel.add(new Integer[] {30,12});
		maischModel.add(new Integer[] {45,9});
		maischModel.add(new Integer[] {51,10});
		maischModel.add(new Integer[] {60,11});
		brewProcess = new BrewProcess(maischModel);
		SpringApplication.run(MaischPerfectApplication.class, args);
	}

	@MessageMapping("/templog")
	@SendTo("/topic/temps")
	public TempResponse getTempLog(TempRequest tempRequest) {
		return new TempResponse(brewProcess.getTempLog(), brewProcess.getAppliedModel(), brewProcess.getSwitchLog());
	}

}
