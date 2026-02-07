package app.onestepcloser.blog;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class BlogApplication {

	private static final Logger logger = LogManager.getLogger(BlogApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BlogApplication.class);
		Environment env = app.run(args).getEnvironment();
		applicationStartupLogging(env);
	}

	private static void applicationStartupLogging(Environment env) {
		String protocol = env.getProperty("server.ssl.key-store") != null ? "https" : "http";
		String serverPort = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path");
		contextPath = StringUtils.isBlank(contextPath) ? "/" : contextPath;
		String activatedProfiles = Arrays.toString(env.getActiveProfiles());
		activatedProfiles = activatedProfiles.equals("[]") ? "[default]" : activatedProfiles;
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.warn("The host name could not be determined, using `localhost` as fallback");
		}
		logger.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! Access URLs:\n\t" +
						"Local API: \t\t{}://localhost:{}{}\n\t" +
						"External API: \t{}://{}:{}{}\n\t" +
						"Profile(s): \t{}" +
						"\n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				activatedProfiles);
	}

}
