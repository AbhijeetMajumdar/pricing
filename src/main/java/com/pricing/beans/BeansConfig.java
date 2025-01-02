package com.pricing.beans;

import com.pricing.pojos.Instrument;
import com.pricing.service.vendor.EnrichAndDistributeEquityService;
import com.pricing.types.InstrumentTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.ExecutorChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableIntegration
public class BeansConfig {

    @Bean
    public QueueChannel instrumentBatchQueue() {
        return new QueueChannel();
    }

    @Bean
    public ExecutorChannelSpec equityPollerQueue() {
//        return MessageChannels.executor("equityPollerQueue", equityPollerQueueTaskExecutor());
//        return MessageChannels.executor("equityPollerQueue");
        return MessageChannels.executor(threadPoolTaskExecutor());
    }


    @Bean
    public ExecutorChannelSpec enrichAndDistributeEquityQueue() {
//        return MessageChannels.executor("enrichAndDistributeEquityQueue", enrichAndDistributeTaskExecutor());
//        return MessageChannels.executor("enrichAndDistributeEquityQueue");
        return MessageChannels.executor(threadPoolTaskExecutor());
    }

    @Bean()
    public QueueChannel mutualfundPollerQueue() {
        return new QueueChannel();
    }

    @Bean()
    public QueueChannel optionPollerQueue() {
        return new QueueChannel();
    }


    @Bean
    public PublishSubscribeChannel latestEquityPricePubSubChannel() {
        return new PublishSubscribeChannel();
    }


    @Bean
    public IntegrationFlow routingFlow() {
        // Streaming Or Polling
        // Eq Or MF Or Option
        return IntegrationFlow.from("instrumentBatchQueue")
                .<List<Instrument>, String>route(payload -> {
                    Instrument firstInst = payload.get(0);
                    if (InstrumentTypes.equity == firstInst.getType()) {
                        return "equityPollerQueue";
                    } else if (InstrumentTypes.mutualfund == firstInst.getType()) {
                        return "mutualfundPollerQueue";
                    }
                    return "optionPollerQueue";


                }, mapping -> mapping
                        .channelMapping("equityPollerQueue", "equityPollerQueue")
                        .channelMapping("mutualfundPollerQueue", "mutualfundPollerQueue")
                        .defaultOutputChannel("optionPollerQueue"))
                .get();
    }

    @Bean
    public IntegrationFlow equityPriceFLow() {
        return IntegrationFlow
                .from(equityPollerQueue())
                .handle("factsetEquityClient", "getEquityPrices")
                .split(ArrayList.class, list -> list)
                .channel(enrichAndDistributeEquityQueue())
                .get();
    }

    @Bean
    public IntegrationFlow enrichAndDistributeFLow() {
        return IntegrationFlow
                .from("enrichAndDistributeEquityQueue")
                .handle("enrichAndDistributeEquityService", "transformAndDistributeMessage")
                .channel("latestEquityPricePubSubChannel")
                .get();
    }

    // Subscriber 1 flow
    @Bean
    public IntegrationFlow subscriberIXSFlow() {
        return IntegrationFlow
                .from("latestEquityPricePubSubChannel"
                )
                .filter(subscriberIXSFilter())

//                .channel(pubSubChannelTaskExecutor())
                .handle(messageHandler(" IXS : "))
                .get();
    }

    @Bean
    public IntegrationFlow subscriberRMCSFlow() {
        return IntegrationFlow
                .from("latestEquityPricePubSubChannel"
                )
                .filter(subscriberRMCSFilter())
                .handle(messageHandler(" RMCS : "))
                .get();
    }

    // Common message handler for demonstration

    public MessageHandler messageHandler(String subscriberName) {
        return message -> {
            System.out.println(Thread.currentThread() + ":: " + subscriberName + " received: " + message.getPayload() + " :: Headers: " + message.getHeaders());
//            System.out.println(subscriberName +" received :"  +  " ::\t" + System.currentTimeMillis());
        };
    }

    @Bean
    public org.springframework.integration.core.MessageSelector subscriberIXSFilter() {
        return message -> {
            // Example: Only process messages with payload "subscriber1"
            String val = (String) message.getHeaders().get(EnrichAndDistributeEquityService.ConsumerChannelName);
            return val.contains("*") || val.contains("IXS");
        };
    }

    @Bean
    public org.springframework.integration.core.MessageSelector subscriberRMCSFilter() {
        return message -> {
            // Example: Only process messages with payload "subscriber2"
            String val = (String) message.getHeaders().get(EnrichAndDistributeEquityService.ConsumerChannelName);
            return val.contains("*") || val.contains("RMCS");
        };
    }



//    @Bean
//    public ExecutorChannel pubSubChannelTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // Number of core threads
//        executor.setMaxPoolSize(100); // Maximum number of threads
//        executor.setQueueCapacity(1); // Size of the task queue
//        executor.setThreadNamePrefix("pubSubChannelTaskExecutor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return new ExecutorChannel(executor); // Create a thread pool with 5 threads
//
//    }

//    @Bean
//    public ThreadPoolTaskExecutor enrichAndDistributeTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // Number of core threads
//        executor.setMaxPoolSize(100); // Maximum number of threads
//        executor.setQueueCapacity(5); // Size of the task queue
//        executor.setThreadNamePrefix("enrichAndDistributeTaskExecutor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100); // Number of core threads
        executor.setMaxPoolSize(500); // Maximum number of threads
        executor.setQueueCapacity(0); // Size of the task queue
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
//    @Bean
//    public ThreadPoolTaskExecutor equityPollerQueueTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(5); // Number of core threads
//        executor.setMaxPoolSize(100); // Maximum number of threads
//        executor.setQueueCapacity(5); // Size of the task queue
//        executor.setThreadNamePrefix("equityPollerQueueTaskExecutor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.initialize();
//        return executor;
//    }

//     Define the default poller
//    @Bean(name = PollerMetadata.DEFAULT_POLLER)
//    public PollerMetadata defaultPoller() {
//        return Pollers.fixedRate(200)  // Poll every 1 second
//                .maxMessagesPerPoll(20)  // Process up to 5 messages per poll
//                .getObject();
//    }

}
