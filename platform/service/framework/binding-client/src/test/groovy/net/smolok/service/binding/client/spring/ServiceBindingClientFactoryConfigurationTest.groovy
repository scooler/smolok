package net.smolok.service.binding.client.spring

import net.smolok.service.binding.ServiceBinding
import net.smolok.service.binding.ServiceBindingFactory
import net.smolok.service.binding.client.ServiceBindingClientFactory
import org.eclipse.kapua.locator.spring.KapuaApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringRunner)
@SpringBootTest(classes = [KapuaApplication, ServiceBindingClientFactoryConfigurationTest])
class ServiceBindingClientFactoryConfigurationTest {

    @Bean
    TestServiceImpl testService() {
        new TestServiceImpl()
    }

    @Bean
    ServiceBinding serviceBinding(ServiceBindingFactory serviceBindingFactory) {
        serviceBindingFactory.serviceBinding('testService')
    }

    @Bean
    TestService testServiceClient(ServiceBindingClientFactory bindingClientFactory) {
        bindingClientFactory.build(TestService.class, 'testService')
    }

    @Autowired
    TestServiceImpl testServiceClient

    @Test
    void shouldAcceptSingleArgument() {
        def response = testServiceClient.echo('foo')
        assertThat(response).isEqualTo('foo')
    }

    @Test
    void shouldAcceptTwoArguments() {
        def response = testServiceClient.doubleEcho('foo', 'bar')
        assertThat(response).isEqualTo('foobar')
    }

    @Test
    void shouldAcceptNoArguments() {
        def response = testServiceClient.noArguments()
        assertThat(response).isEqualTo('echo')
    }

    @Test
    void shouldHandleVoidOperation() {
        testServiceClient.noResponse('innerValue')
        assertThat(testServiceClient.innerValue).isEqualTo('innerValue')
    }

    static interface TestService {

        String echo(String echo)

        String doubleEcho(String firstEcho, String secondEcho)

        String noArguments()

        void noResponse(String innerValue)

    }

    static class TestServiceImpl implements TestService {

        String innerValue

        @Override
        String echo(String echo) {
            echo
        }

        @Override
        String doubleEcho(String firstEcho, String secondEcho) {
            firstEcho + secondEcho
        }

        @Override
        String noArguments() {
            'echo'
        }

        @Override
        void noResponse(String innerValue) {
            this.innerValue = innerValue
        }

    }

}