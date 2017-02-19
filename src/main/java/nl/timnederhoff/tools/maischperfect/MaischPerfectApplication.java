package nl.timnederhoff.tools.maischperfect;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatus;
import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatusRequest;
import nl.timnederhoff.tools.maischperfect.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@EnableScheduling
@SpringBootApplication
public class MaischPerfectApplication {

	private static List<Integer[]> maischModel;

	private static BrewProcess brewProcess;

	@Value("${baseDir}")
	String baseDir;

	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	public static void main(String[] args) {
		SpringApplication.run(MaischPerfectApplication.class, args);
	}

	@MessageMapping("/templog")
	@SendTo("/topic/temps")
	public BrewProcessStatus getTempLog(BrewProcessStatusRequest brewProcessStatusRequest) {

		return new BrewProcessStatus(
				brewProcess.getTempLog(brewProcessStatusRequest.getFromPointTemp()),
				brewProcess.getAppliedModel(brewProcessStatusRequest.getFromPointMaisch()),
				brewProcess.getSwitchLog(brewProcessStatusRequest.getFromPointHeater()),
				brewProcess.isEnded(),
				//TODO: find out why slope is always 0.0 over websocket
				brewProcess.getSlope()
		);

	}

	@Scheduled(fixedRate = 1500)
	public void broadcastTemperature() {
		this.brokerMessagingTemplate.convertAndSend("/topic/livedata", Double.toString(Temperature.get().currentTemperature()));
	}

	@RequestMapping("/start")
	public void startProcess() {
		maischModel = new ArrayList<>();
		maischModel.add(new Integer[] {25,1});
		maischModel.add(new Integer[] {29,2});
		maischModel.add(new Integer[] {32,1});
		maischModel.add(new Integer[] {35,2});
		maischModel.add(new Integer[] {50,2});
		brewProcess = new BrewProcess(maischModel, 2000, 1);
		brewProcess.start();
		System.out.println("brewprocess has started");
	}

	@RequestMapping("/stop")
	public void stopProcess() {
		brewProcess.stop();
		System.out.println("brewprocess has started");
	}

	@RequestMapping("/recipe")
	public Recipe getRecipe() {
		return new Recipe(
				"Witbier met koreander",
				"Recept volgens Wil van den Broek"
		);
	}

	@RequestMapping(value = "/recipe", method = RequestMethod.POST)
	public void writeRecipeToFile(@RequestBody Recipe recipe) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(baseDir + "/testrecipe.json"), recipe);
	}

}
