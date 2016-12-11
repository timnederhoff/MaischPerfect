package nl.timnederhoff.tools.maischperfect;

import nl.timnederhoff.tools.maischperfect.model.TempRequest;
import nl.timnederhoff.tools.maischperfect.model.TempResponse;
import nl.timnederhoff.tools.maischperfect.model.highcharts.Point;
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
		brewProcess = new BrewProcess(maischModel);
		SpringApplication.run(MaischPerfectApplication.class, args);
	}

	@MessageMapping("/templog")
	@SendTo("/topic/temps")
	public TempResponse getTempLog(TempRequest tempRequest) {

		return new TempResponse(
				brewProcess.getTempLog(tempRequest.getFromPointTemp()),
				brewProcess.getAppliedModel(tempRequest.getFromPointMaisch()),
				brewProcess.getSwitchLog(tempRequest.getFromPointHeater()),
				brewProcess.isEnded()
		);

	}

	@Scheduled(fixedRate = 1000)
	public void broadcastTemperature() {
		this.brokerMessagingTemplate.convertAndSend("/topic/livedata", Integer.toString(brewProcess.getCurrentTemperature()));
	}

	@RequestMapping("/start")
	public void startProcess() {
		brewProcess.start();
		System.out.println("brewprocess has started");
	}


}
