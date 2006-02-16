/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * @version $Revision: 1.4 $
 */
public class JmsTopicRedeliverTest extends TestSupport {
    
    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(JmsTopicRedeliverTest.class);
   
    
    protected Connection connection;
    protected Session session;
    protected Session consumeSession;
    protected MessageConsumer consumer;
    protected MessageProducer producer;
    protected Destination consumerDestination;
    protected Destination producerDestination;
    protected boolean topic = true;
    protected boolean durable = false;
    protected boolean verbose = false;
    protected long    initRedeliveryDelay = 0;

    protected void setUp() throws Exception {
        super.setUp();

        connectionFactory = createConnectionFactory();
        connection = createConnection();
        initRedeliveryDelay = ((ActiveMQConnection)connection).getRedeliveryPolicy().getInitialRedeliveryDelay();

        if (durable) {
            connection.setClientID(getClass().getName());
        }

        log.info("Created connection: " + connection);

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        consumeSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        log.info("Created session: " + session);
        log.info("Created consumeSession: " + consumeSession);
        producer = session.createProducer(null);
        //producer.setDeliveryMode(deliveryMode);

        log.info("Created producer: " + producer);

        if (topic) {
            consumerDestination = session.createTopic(getConsumerSubject());
            producerDestination = session.createTopic(getProducerSubject());
        } else {
            consumerDestination = session.createQueue(getConsumerSubject());
            producerDestination = session.createQueue(getProducerSubject());
        }

        log.info("Created  consumer destination: " + consumerDestination + " of type: " + consumerDestination.getClass());
        log.info("Created  producer destination: " + producerDestination + " of type: " + producerDestination.getClass());
        consumer = createConsumer();
        connection.start();

        log.info("Created connection: " + connection);
    }

    
    protected void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
        super.tearDown();
    }


    /**
     * Returns the consumer subject.
     *
     * @return String - consumer subject
     * @see org.apache.activemq.test.TestSupport#getConsumerSubject()
     */
    protected String getConsumerSubject() {
        return "TEST";
    }

    /**
     * Returns the producer subject.
     *
     * @return String - producer subject
     * @see org.apache.activemq.test.TestSupport#getProducerSubject()
     */
    protected String getProducerSubject() {
        return "TEST";
    }

    /**
     * Sends and consumes the messages.
     *
     * @throws Exception
     */
    public void testRecover() throws Exception {
        String text = "TEST";
        Message sendMessage = session.createTextMessage(text);

        if (verbose) {
            log.info("About to send a message: " + sendMessage + " with text: " + text);
        }
        producer.send(producerDestination, sendMessage);

        //receive but don't acknowledge
        Message unackMessage = consumer.receive(initRedeliveryDelay + 1000);
        assertNotNull(unackMessage);
        String unackId = unackMessage.getJMSMessageID();
        assertEquals(((TextMessage) unackMessage).getText(), text);
        assertFalse(unackMessage.getJMSRedelivered());
        //assertEquals(unackMessage.getIntProperty("JMSXDeliveryCount"),1);

        //receive then acknowledge
        consumeSession.recover();
        Message ackMessage = consumer.receive(initRedeliveryDelay + 1000);
        assertNotNull(ackMessage);
        ackMessage.acknowledge();
        String ackId = ackMessage.getJMSMessageID();
        assertEquals(((TextMessage) ackMessage).getText(), text);
        assertTrue(ackMessage.getJMSRedelivered());
        //assertEquals(ackMessage.getIntProperty("JMSXDeliveryCount"),2);
        assertEquals(unackId, ackId);
        consumeSession.recover();
        assertNull(consumer.receiveNoWait());
    }

    protected MessageConsumer createConsumer() throws JMSException {
        if (durable) {
            log.info("Creating durable consumer");
            return consumeSession.createDurableSubscriber((Topic) consumerDestination, getName());
        }
        return consumeSession.createConsumer(consumerDestination);
    }

}
