package nl.timnederhoff.tools.maischperfect;

import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatusRequest;
import nl.timnederhoff.tools.maischperfect.model.BrewProcessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
@EnableScheduling
@SpringBootApplication
public class MaischPerfectApplication {

	private static List<Integer[]> maischModel = new ArrayList<>();

	private static BrewProcess brewProcess;

	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	public static void main(String[] args) {
		maischModel.add(new Integer[] {40,5});
		maischModel.add(new Integer[] {53,20});
		maischModel.add(new Integer[] {65,20});
		maischModel.add(new Integer[] {78,1});
		maischModel.add(new Integer[] {100,60});
		brewProcess = new BrewProcess(maischModel, 500);
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
				brewProcess.getSlope()
		);

	}

	@Scheduled(fixedRate = 1000)
	public void broadcastTemperature() {
		this.brokerMessagingTemplate.convertAndSend("/topic/livedata", Double.toString(brewProcess.getCurrentTemperature()));
	}

	@RequestMapping("/start")
	public void startProcess() {
		brewProcess.start();
		System.out.println("brewprocess has started");
	}


}
