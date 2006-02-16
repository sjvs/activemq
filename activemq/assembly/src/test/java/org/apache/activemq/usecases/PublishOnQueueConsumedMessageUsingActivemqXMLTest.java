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
package org.apache.activemq.usecases;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.xbean.BrokerFactoryBean;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;

/**
 *
 * Test Publish/Consume queue using the release activemq.xml configuration file
 *
 * @version $Revision: 1.2 $
 */
public class PublishOnQueueConsumedMessageUsingActivemqXMLTest extends PublishOnTopicConsumedMessageTest {
    protected static final String JOURNAL_ROOT = "../data/";
    BrokerService broker;



     /**
     * Use the transportConnector uri configured on the activemq.xml
     *
     * @return ActiveMQConnectionFactory
     * @throws Exception
     */
    protected ActiveMQConnectionFactory createConnectionFactory() throws Exception {
        return new ActiveMQConnectionFactory("tcp://localhost:61616");
    }


    /**
     * Sets up a test where the producer and consumer have their own connection.
     *
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        ;
        File journalFile = new File(JOURNAL_ROOT);
        recursiveDelete(journalFile);
        // Create broker from resource
        System.out.print("Creating broker... ");
        broker = createBroker("org/apache/activemq/usecases/activemq.xml");
        System.out.println("Success");
        super.setUp();

    }



     /*
     * Stops the Broker
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
         System.out.println("Closing Broker");
            if (broker != null) {
               broker.stop();
            }
         System.out.println("Broker closed...");


    }


    /*
     * clean up the journal
     */

    protected static void recursiveDelete(File file) {
        if( file.isDirectory() ) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                recursiveDelete(files[i]);
            }
        }
        file.delete();
    }

    protected BrokerService createBroker(String resource) throws Exception {
        return createBroker(new ClassPathResource(resource));
    }

    protected BrokerService createBroker(Resource resource) throws Exception {
        BrokerFactoryBean factory = new BrokerFactoryBean(resource);
        factory.afterPropertiesSet();

        BrokerService broker = factory.getBroker();

        //assertTrue("Should have a broker!", broker != null);


        return broker;
    }
}
