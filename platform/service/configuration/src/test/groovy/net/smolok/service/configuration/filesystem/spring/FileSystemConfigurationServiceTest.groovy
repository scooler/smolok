package net.smolok.service.configuration.filesystem.spring

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import smolok.bootstrap.Smolok
import smolok.eventbus.client.EventBus

import static java.io.File.createTempFile
import static org.assertj.core.api.Assertions.assertThat
import static smolok.eventbus.client.Header.arguments
import static smolok.lib.common.Properties.setSystemStringProperty
import static smolok.lib.common.Uuids.uuid

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Smolok.class)
class FileSystemConfigurationServiceTest {

    def key = uuid()

    @Autowired
    EventBus eventBus

    @BeforeClass
    static void beforeClass() {
        setSystemStringProperty('configuration.file', createTempFile('smolok', 'test').absolutePath)
    }

    @Test
    void shouldReadStoredProperty() {
        // Given
        eventBus.toBusAndWait('configuration.put', 'value', arguments(key))

        // When
        def property = eventBus.fromBus('configuration.get', key, String.class)

        // Then
        assertThat(property).isEqualTo('value')
    }

}