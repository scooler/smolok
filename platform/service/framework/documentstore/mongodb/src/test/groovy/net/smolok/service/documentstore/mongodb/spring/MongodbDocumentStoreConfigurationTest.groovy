/**
 * Licensed to the Smolok under one or more
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
package net.smolok.service.documentstore.mongodb.spring

import com.fasterxml.jackson.databind.ObjectMapper
import net.smolok.service.documentstore.api.DocumentStore
import net.smolok.service.documentstore.api.QueryBuilder
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.common.Networks.findAvailableTcpPort
import static smolok.lib.common.Properties.setIntProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = [MongoAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class,
        MongodbDocumentStoreConfiguration.class])
class MongodbDocumentStoreConfigurationTest {

    def collection = uuid()

    def invoice = new Invoice(invoiceId: 'foo')

    def mapper = new ObjectMapper()

    @Autowired
    DocumentStore documentStore

    @BeforeClass
    static void beforeClass() {
        setIntProperty("spring.data.mongodb.port", findAvailableTcpPort())
    }

    // Tests

    @Test
    void shouldCountInvoice() {
        // Given
        documentStore.save(collection, serialize(invoice))

        // When
        def count = documentStore.countByQuery(collection, QueryBuilder.queryBuilder())

        // Then
        assertThat(count).isEqualTo(1)
    }

    @Test
    void shouldFindOne() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice).isNotNull()
    }

    @Test
    void loadedDocumentShouldHasId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice.id).isInstanceOf(String.class)
    }

    @Test
    void loadedDocumentShouldHasNotMongoId() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))

        // When
        def loadedInvoice = documentStore.findOne(collection, id)

        // Then
        assertThat(loadedInvoice._id).isNull()
    }

    @Test
    public void shouldUpdateDocument() {
        // Given
        def id = documentStore.save(collection, serialize(invoice))
        invoice.id = id
        invoice.invoiceId = 'newValue'

        // When
        documentStore.save(collection, serialize(invoice))

        // Then
        def updatedInvoice = documentStore.findOne(collection, id)
        assertThat(updatedInvoice.invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    void shouldReturnEmptyList() {
        // When
        def invoices = documentStore.findByQuery(collection, new QueryBuilder())

        // Then
        assertThat(invoices.size()).isEqualTo(0);
    }

    @Test
    public void shouldFindByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        def query = [invoiceId: invoice.invoiceId]

        // When
        def invoices = documentStore.findByQuery(collection, new QueryBuilder(query))

        // Then
        assertThat(invoices.size()).isEqualTo(1)
        assertThat(invoices.first().invoiceId).isEqualTo(invoice.invoiceId)
    }

    @Test
    public void shouldFindAllByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))
        documentStore.save(collection, serialize(invoice))

        // When
        def invoices = documentStore.findByQuery(collection, new QueryBuilder())

        // Then
        assertThat(invoices).hasSize(2)
    }

    @Test
    public void shouldNotFindByQuery() {
        // Given
        documentStore.save(collection, serialize(invoice))

        invoice.invoiceId = 'invoice001'
        def query = new QueryBuilder([invoiceId: "randomValue"])

        // When
        def invoices = documentStore.findByQuery(collection, query)

        // Then
        assertThat(invoices).isEmpty()
    }

    // Helpers

    private Map<String, Object> serialize(Invoice invoice) {
        mapper.convertValue(invoice, Map.class)
    }


    // Class fixtures

    static class Invoice {

        String id

        Date timestamp = new Date()

        String invoiceId

        Address address

        static class Address {

            String street

        }

    }

}