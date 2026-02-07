package app.onestepcloser.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
	
	@Value("${app.multithreading.pool-size.read}")
    private int numberOfReadingThread;

	@Value("${app.multithreading.pool-size.write}")
    private int numberOfWritingThread;

	@Bean("readingThreadPool")
    public ExecutorService readThreadPool(){
        return Executors.newFixedThreadPool(numberOfReadingThread);
    }

	@Bean("writingThreadPool")
    public ExecutorService writeThreadPool(){
        return Executors.newFixedThreadPool(numberOfWritingThread);
    }

}
