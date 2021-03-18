package org.dashboard.controllers

import org.dashboard.dto.Build
import org.dashboard.service.VersionService
import spock.lang.Specification

class DashboardSpec extends Specification {
    private Dashboard dashboard

    def setup() {
        dashboard = new Dashboard()
        dashboard.with {
            versionService = Mock(VersionService)
        }
    }

    def "should return a model with values"() {
        given:
            def a = 1

        when:
            def model = dashboard.dashboard()

        then:
            1 * dashboard.versionService.fetchData() >> ["name": new Build()]
            model.model["hosts"]["name"]
    }
}
