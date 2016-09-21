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

import com.mongodb.Mongo
import net.smolok.service.documentstore.api.DocumentStore
import net.smolok.service.documentstore.mongodb.MongodbDocumentStore
import net.smolok.service.documentstore.mongodb.MongodbMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongodbDocumentStoreConfiguration {

    @ConditionalOnMissingBean
    @Bean
    DocumentStore documentStore(Mongo mongo, MongodbMapper mongodbMapper,
                                @Value('${documentStore.mongodb.db:documents}') String documentsDbName) {
        new MongodbDocumentStore(mongo, mongodbMapper, documentsDbName)
    }

    @Bean
    MongodbMapper mongodbMapper(@Value('${documentStore.mongodb.idField:id}') String idField) {
        new MongodbMapper(idField)
    }

}
