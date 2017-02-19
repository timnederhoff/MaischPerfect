package nl.timnederhoff.tools.maischperfect;

import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatus;
import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatusRequest;
import nl.timnederhoff.tools.maischperfect.model.MaischPerfectRepo;
import nl.timnederhoff.tools.maischperfect.model.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(MaischPerfectApplication.class);

	@Value("${baseDir}")
	String baseDir;

	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	@Autowired
	private MaischPerfectRepo repository;

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
	public Iterable<Recipe> getRecipe() {
		return repository.findAll();
	}

	@RequestMapping(value = "/recipe", method = RequestMethod.POST)
	public void writeRecipeToRepo(@RequestBody Recipe recipe) throws IOException {
		repository.save(recipe);
	}

}
