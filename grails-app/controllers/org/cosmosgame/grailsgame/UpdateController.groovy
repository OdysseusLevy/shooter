package org.cosmosgame.grailsgame

import grails.converters.JSON

class UpdateController {

    GameService gameService;

    def index()
    {
        render gameService.getGameState() as JSON
    }
}
