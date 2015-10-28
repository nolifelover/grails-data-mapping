package grails.gorm.tests

/*
 * Copyright 2014 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author graemerocher
 */
class CypherQueryStringSpec extends GormDatastoreSpec {

    void "test find method that accepts cypher"() {
        given:
        setupDomain()

        when:"A find method is executed"
        def club = Club.find("MATCH n where n.name = {1} RETURN n", 'FC Bayern Muenchen')

        then:"The result is correct"
        club instanceof Club
        club.name == 'FC Bayern Muenchen'
        club.teams
        club.teams.size() == 2

        when:"A find method is executed with map arguments"
        club = Club.find("MATCH n where n.name = {name} RETURN n", [name:'FC Bayern Muenchen'])

        then:"The result is correct"
        club instanceof Club
        club.name == 'FC Bayern Muenchen'
        club.teams
        club.teams.size() == 2

        when:"A find method is executed on the inverse side"
        session.clear()
        def team = Team.find("MATCH n where n.name = {name} RETURN n", [name:'FCB Team 1'])

        then:"The result is correct"
        team instanceof Team
        team.name == 'FCB Team 1'
        team.club instanceof Club
        team.club.name == 'FC Bayern Muenchen'

    }

    void "test findAll method that accepts cypher"() {
        given:
        setupDomain()

        when:"A find method is executed"
        def clubs = Club.findAll("MATCH n where n.name = {1} RETURN n", 'FC Bayern Muenchen')

        then:"The result is correct"
        clubs.size() == 1
        clubs[0] instanceof Club
        clubs[0].name == 'FC Bayern Muenchen'
        clubs[0].teams
        clubs[0].teams.size() == 2

        when:"A find method is executed with map arguments"
        session.clear()
        clubs = Club.findAll("MATCH n where n.name = {name} RETURN n", [name:'FC Bayern Muenchen'])

        then:"The result is correct"
        clubs.size() == 1
        clubs[0] instanceof Club
        clubs[0].name == 'FC Bayern Muenchen'
        clubs[0].teams
        clubs[0].teams.size() == 2

    }

    void setupDomain() {
        def club = new Club(name: 'FC Bayern Muenchen')
        club.addToTeams(new Team(name: 'FCB Team 1'))
        club.addToTeams(new Team(name: 'FCB Team 2'))
        def otherClub = new Club(name: 'Borussia Dortmund')
        club.save()
        otherClub.addToTeams(new Team(name: 'BVB 1'))
        otherClub.addToTeams(new Team(name: 'BVB 2'))
        otherClub.save(flush:true)
        session.clear()
    }

    @Override
    List getDomainClasses() {
        [Club, Team]
    }
}
