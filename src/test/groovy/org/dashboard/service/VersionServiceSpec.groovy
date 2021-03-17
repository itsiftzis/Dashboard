package org.dashboard.service

import org.dashboard.configuration.ConfigProperties
import org.dashboard.dto.Build
import org.dashboard.dto.Info
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneOffset

class VersionServiceSpec extends Specification {
    private VersionService versionService

    def setup() {
        versionService = new VersionService()
        versionService.with {
            configProperties = Mock(ConfigProperties)
            restTemplate = Mock(RestTemplate)
        }
    }

    def "populate a map with version properties"() {
        given:
            def mapping = ["sdc-mil-dev1": "url"]
            def build = new Build(artifact: "artifact", version: "version", name: "name", group: "group", time: LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

        when:
            def data = versionService.fetchData()

        then:
            1 * versionService.configProperties.getMapping() >> mapping
            1 * versionService.restTemplate.getForObject(_, _, _) >> new Info(build: build)
            0 * _

        and:
            data["sdc-mil-dev1"]
            data["sdc-mil-dev1"].artifact == build.artifact
            data["sdc-mil-dev1"].version == build.version
            data["sdc-mil-dev1"].name == build.name
            data["sdc-mil-dev1"].group == build.group
            data["sdc-mil-dev1"].time == build.time
    }
}