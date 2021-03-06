package smolok.status.spring

import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import smolok.lib.process.ProcessManager
import net.smolok.paas.Paas
import smolok.status.StatusResolver

import static org.assertj.core.api.Assertions.assertThat
import static smolok.lib.process.Command.cmd
import static smolok.status.handlers.eventbus.EventBusMetricHandler.EVENTBUS_CAN_SEND_METRIC_KEY

@RunWith(SpringRunner)
@SpringBootTest(classes = [KapuaApplication, StatusResolverConfigurationTest])
class StatusResolverConfigurationTest {

    // Tests subject

    @Autowired
    StatusResolver statusResolver

    // Collaborators

    @Autowired
    Paas paas

    @Autowired
    ProcessManager processManager

    // Fixtures

    @Before
    void before() {
        paas.reset()
        paas.start()
    }

    // Tests

    @Test
    void canSendToEventBus() {
        // When
        def canSendToEventBus = statusResolver.status().find{ it.key() == EVENTBUS_CAN_SEND_METRIC_KEY }

        // Then
        assertThat(canSendToEventBus).isNotNull()
        assertThat(canSendToEventBus.value()).isEqualTo(true)
        assertThat(canSendToEventBus.warning()).isEqualTo(false)
    }

    @Test
    void cannotSendToEventBus() {
        // Given
        def eventsBusPid = processManager.execute(cmd('docker ps')).find{ it.contains('k8s_eventbus') }.replaceFirst(/\s.+/, '')
        processManager.execute(cmd("docker stop ${eventsBusPid}"))

        // When
        def canSendToEventBus = statusResolver.status().find{ it.key() == EVENTBUS_CAN_SEND_METRIC_KEY }

        // Then
        assertThat(canSendToEventBus).isNotNull()
        assertThat(canSendToEventBus.value()).isEqualTo(false)
        assertThat(canSendToEventBus.warning()).isEqualTo(true)
    }

}
