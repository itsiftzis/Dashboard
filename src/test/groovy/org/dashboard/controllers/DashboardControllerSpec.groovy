package org.dashboard.controllers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.dashboard.application.WebApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get


@SpringBootTest(classes = WebApplication, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerSpec extends Specification {
    @Autowired
    private MockMvc mvc

    @Shared
    WireMockServer wireMockServer

    def setupSpec() {
        wireMockServer = new WireMockServer(8888)
        wireMockServer.start()
    }

    def "return a view with a model containing data"() {
        given:
            configureFor("localhost", 8888)
            stubFor(WireMock.get(WireMock.urlEqualTo("/mil-sdc-dev1/actuator/info"))
                .willReturn(WireMock.aResponse()
                    .withHeader("content-type", "application/json")
                    .withBodyFile("version-mil-sdc-dev1.json")))

            stubFor(WireMock.get(WireMock.urlEqualTo("/mil-pdc-dev1/actuator/info"))
                .willReturn(WireMock.aResponse()
                    .withHeader("content-type", "application/json")
                    .withBodyFile("version-mil-pdc-dev1.json")))

        expect:
            String content = mvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()
            content.contains("mil-pdc-dev1")
            content.contains("mil-sdc-dev1")
    }

    def cleanupSpec() {
        wireMockServer.shutdown()
    }
}
