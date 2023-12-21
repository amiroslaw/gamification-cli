package ovh.miroslaw.gamification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.shell.command.annotation.CommandScan;

//@ConfigurationPropertiesScan
@CommandScan
@EnableConfigurationProperties(Deck.class)
@SpringBootApplication
public class GamificationApplication {

	GamificationApplication(){}
	public static void main(String[] args) {
		SpringApplication.run(GamificationApplication.class, args);
	}

}
