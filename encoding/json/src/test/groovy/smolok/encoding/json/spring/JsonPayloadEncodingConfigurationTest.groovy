/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smolok.encoding.json.spring

import org.eclipse.kapua.locator.spring.KapuaApplication;
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import smolok.encoding.spi.PayloadEncoding

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KapuaApplication.class)
class JsonPayloadEncodingConfigurationTest {

    @Autowired
    PayloadEncoding payloadEncoding

    def payload = 'payload'

    // Tests

    @Test
    void shouldEncodePayload() {
        // When
        byte[] encodedPayload = payloadEncoding.encode(payload)
        def json = new String(encodedPayload)

        // Then
        assertThat(json).contains('"payload":"payload"')
    }

    @Test
    void shouldEncodeNullPayload() {
        // When
        def encodedPayload = payloadEncoding.encode(null)
        def json = new String(encodedPayload)

        // Then
        assertThat(json).contains('"payload":null')
    }

    @Test
    void shouldDecodePayload() {
        // Given
        byte[] encodedPayload = payloadEncoding.encode(payload);

        // When
        String decodedPayload = (String) payloadEncoding.decode(encodedPayload);

        // Then
        assertThat(decodedPayload).isEqualTo(payload)
    }

    @Test
    void shouldDecodeNullPayload() {
        // Given
        def encodedPayload = payloadEncoding.encode(null)

        // When
        def decodedPayload = payloadEncoding.decode(encodedPayload)

        // Then
        assertThat(decodedPayload).isNull()
    }

}