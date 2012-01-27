package shooter

import grails.converters.deep.JSON
class UpdateController {

    def index() 
	{
	  Sprite sprite = new Sprite()
	  sprite.x = 20
	  sprite.y = 20
	  sprite.name = "booya!"

          render sprite as JSON
 	}
}

