package org.dashboard.service

import org.dashboard.configuration.ConfigProperties
import org.dashboard.dto.Build
import org.dashboard.dto.Info
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneOffset

class VersionServiceSpec extends Specification {
    private VersionService versionService

    @Shared
    def build = new Build(id: "id", artifact: "artifact", version: "version", name: "name", group: "group")

    @Shared
    def mapping = ["name": "url"]

    def setup() {
        versionService = new VersionService()
        versionService.with {
            configProperties = Mock(ConfigProperties)
            restTemplate = Mock(RestTemplate)
        }
    }

    def "populate a map with version properties"() {
        given:
            build.time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        when:
            def data = versionService.fetchData()

        then:
            1 * versionService.configProperties.getMapping() >> mapping
            1 * versionService.restTemplate.getForObject(_, _, _) >> new Info(build: build)
            0 * _

        and:
            data["name"].id == build.id
            data["name"].artifact == build.artifact
            data["name"].version == build.version
            data["name"].name == build.name
            data["name"].group == build.group
            data["name"].time == build.time
    }

    @Unroll
    def "should return #expectedTime when input is #returnedTimeObject"() {
        given:
            build.time = returnedTimeObject

        when:
            def data = versionService.fetchData()

        then:
            1 * versionService.configProperties.getMapping() >> mapping
            1 * versionService.restTemplate.getForObject(_, _, _) >> new Info(build: build)
            0 * _

        and:
            data["name"].time == expectedTime

        where:
            returnedTimeObject                             | expectedTime
            "2020 Mon 15:00:00"                            | "2020 Mon 15:00:00"
            1616056518D                                    | "2021-03-18T08:35:18"
            ["nano": 147000000, "epochSecond": 1615201243] | "2021-03-08T11:00:43"
    }

    def "should handle Exceptions"() {
        given:
            build.time = 1616056518D

        when:
            def data = versionService.fetchData()

        then:
            1 * versionService.configProperties.getMapping() >> mapping
            1 * versionService.restTemplate.getForObject(_, _, _) >> { throw new IOException("error") }

        and:
            !data["name"]
    }

    @Unroll
    def "should handle #returnedInfoObject and return #expectedObject"() {
        given:
            build.time = 1616056518D

        when:
            def data = versionService.fetchData()

        then:
            1 * versionService.configProperties.getMapping() >> mapping
            1 * versionService.restTemplate.getForObject(_, _, _) >> returnedInfoObject

        and:
            data["name"].id == "name"
            data["name"].artifact == expectedObject.build.artifact
            data["name"].version == expectedObject.build.version
            data["name"].name == expectedObject.build.name
            data["name"].group == expectedObject.build.group
            data["name"].time == expectedObject.build.time

        where:
            returnedInfoObject | expectedObject
            Info.emptyInfo()   | Info.emptyInfo()
            null               | Info.emptyInfo()
    }
}
