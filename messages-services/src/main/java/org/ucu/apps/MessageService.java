package org.ucu.apps;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class MessageService {
    private final Logger logger = Logger.getLogger(MessageService.class.getName());

    private IQueue<String> messagesQueue = Hazelcast.newHazelcastInstance().getQueue("messagesQueue");

    private final ArrayList<String> messageList = new ArrayList<>();

    public String getMessages() {
        logger.info("Getting local messages");
        return messageList.toString();
    }

    @PostConstruct
    public void listenForMessages() {
        var listener = new Runnable() {
            @Override
            public void run() {
                listen();
            }
        };
        var thread = new Thread(listener);
        thread.start();
    }

    private void listen() {
        logger.info("Listening for messages");
        while (true) {
            String message = messagesQueue.poll();
            if (message != null) {
                if (message.equals("exit")) {
                    break;
                }
                logger.info("Received message: " + message);
                messageList.add(message);
                System.out.println("Array list: " + messageList);
            }
        }
    }
}
