/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.distribution.journal.messages;

import static org.apache.sling.distribution.journal.messages.Messages.SubscriberConfiguration;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import org.apache.sling.distribution.journal.messages.Messages.DiscoveryMessage;
import org.apache.sling.distribution.journal.messages.Messages.DiscoveryMessageOrBuilder;
import org.apache.sling.distribution.journal.messages.Messages.SubscriberState;
import org.apache.sling.distribution.journal.messages.Messages.SubscriberStateOrBuilder;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class SerTest {

    @Test
    public void test() throws InvalidProtocolBufferException {
        DiscoveryMessage discoveryMsg = createMessage();
        byte[] messageBytes = discoveryMsg.toByteArray();

        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        DiscoveryMessageOrBuilder messageIn = DiscoveryMessage.parseFrom(messageBytes, registry);
        checkMessage(messageIn);

    }

    @Test
    public void testFromClass() throws Exception {
        DiscoveryMessage discoveryMsg = createMessage();
        ByteString byteString = discoveryMsg.toByteString();
        
        ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
        Class<? extends GeneratedMessage> type = DiscoveryMessage.class;
        Method method = type.getMethod("parseFrom", ByteString.class, ExtensionRegistryLite.class);
        DiscoveryMessage messageIn = (DiscoveryMessage) method.invoke(null, byteString, registry);
        checkMessage(messageIn);
    }

    private DiscoveryMessage createMessage() {
        DiscoveryMessage discoveryMsg = DiscoveryMessage.newBuilder()
                .setSubSlingId("subscribersling1")
                .setSubAgentName("subscriber1")
                .setSubscriberConfiguration(SubscriberConfiguration
                        .newBuilder()
                        .setEditable(false)
                        .setMaxRetries(-1)
                        .build())
                .addSubscriberState(SubscriberState
                        .newBuilder()
                        .setPubAgentName("publisher1")
                        .setOffset(10).build())
                .build();
        return discoveryMsg;
    }

    private void checkMessage(DiscoveryMessageOrBuilder messageIn) {
        assertThat(messageIn.getSubSlingId(), equalTo("subscribersling1"));
        assertThat(messageIn.getSubAgentName(), equalTo("subscriber1"));
        SubscriberStateOrBuilder offset = messageIn.getSubscriberStateList().iterator().next();
        assertThat(offset.getPubAgentName(), equalTo("publisher1"));
        assertThat(offset.getOffset(), equalTo(10l));
    }
}
