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
package org.apache.activemq.console.filter;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Destination;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.QueueBrowser;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;

public class MessagesQueryFilter extends AbstractQueryFilter {

    private URI brokerUrl;
    private Destination destination;

    /**
     * Create a JMS message query filter
     * @param brokerUrl - broker url to connect to
     * @param destination - JMS destination to query
     */
    public MessagesQueryFilter(URI brokerUrl, Destination destination) {
        super(null);
        this.brokerUrl   = brokerUrl;
        this.destination = destination;
    }

    /**
     * Queries the specified destination using the message selector format query
     * @param queries - message selector queries
     * @return list messages that matches the selector
     * @throws Exception
     */
    public List query(List queries) throws Exception {
        String selector = "";

        // Convert to message selector
        for (Iterator i=queries.iterator(); i.hasNext();) {
            selector = selector + "(" + i.next().toString() + ") AND ";
        }

        // Remove last AND
        if (selector != "") {
            selector = selector.substring(0, selector.length() - 5);
        }

        if (destination instanceof ActiveMQQueue) {
            return queryMessages((ActiveMQQueue)destination, selector);
        } else {
            return queryMessages((ActiveMQTopic)destination, selector);
        }
    }

    /**
     * Query the messages of a queue destination using a queue browser
     * @param queue - queue destination
     * @param selector - message selector
     * @return list of messages that matches the selector
     * @throws Exception
     */
    protected List queryMessages(ActiveMQQueue queue, String selector) throws Exception {
        Connection conn = createConnection(getBrokerUrl());

        Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        QueueBrowser browser = sess.createBrowser(queue, selector);

        List messages = Collections.list(browser.getEnumeration());

        conn.close();

        return messages;
    }

    /**
     * Query the messages of a topic destination using a message consumer
     * @param topic - topic destination
     * @param selector - message selector
     * @return list of messages that matches the selector
     * @throws Exception
     */
    protected List queryMessages(ActiveMQTopic topic, String selector) throws Exception {
        // TODO: should we use a durable subscriber or a retroactive non-durable subscriber?
        // TODO: if a durable subscriber is used, how do we manage it? subscribe/unsubscribe tasks?
        return null;
    }

    /**
     * Create and start a JMS connection
     * @param brokerUrl - broker url to connect to.
     * @return JMS connection
     * @throws JMSException
     */
    protected Connection createConnection(URI brokerUrl) throws JMSException {
        Connection conn = (new ActiveMQConnectionFactory(brokerUrl)).createConnection();
        conn.start();
        return conn;
    }

    /**
     * Get the broker url being used.
     * @return broker url
     */
    public URI getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Set the broker url to use.
     * @param brokerUrl - broker url
     */
    public void setBrokerUrl(URI brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * Get the destination being used.
     * @return - JMS destination
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     * Set the destination to use.
     * @param destination - JMS destination
     */
    public void setDestination(Destination destination) {
        this.destination = destination;
    }

}
