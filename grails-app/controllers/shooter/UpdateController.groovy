package shooter

import grails.converters.JSON;

class UpdateController {

    GameService gameService;
    
    def index() 
	{
	    render gameService.getGameState() as JSON
 	}
}

