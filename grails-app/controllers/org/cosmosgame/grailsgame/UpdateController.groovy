package org.cosmosgame.grailsgame

import grails.converters.JSON
import org.cosmosgame.mapbuilder.MapBuilder

class UpdateController {

    GameService gameService;
    MapService mapService;
    
    def index()
    {
        render gameService.getGameState() as JSON
    }
    
    def createWorld()
    {
        render mapService.createWorld() as JSON;
    }
}
