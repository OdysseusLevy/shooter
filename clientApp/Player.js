/*
*   ..player.js, uly, dec2011..
*
*/
function Player( canvasWidth, canvasHeight, gameFactory )
{ 
	var that = this;

    //
    //  public variables.
    // 
	that.image = new Image(); 
	that.image.src = "ship.png"
	that.width = 65;
	that.height = 95;
	that.frames = 1;
	that.actualFrame = 0;
	that.X = 0;
	that.Y = 0;	
	that.interval = 0;

    //
    //  private variables.
    //
    
    var _gameFactory = gameFactory;
    var _canvasWidth = canvasWidth;
    var _canvasHeight = canvasHeight;

    var _isMovingUp =       false;
    var _isMovingDown =     false;
    var _isMovingLeft =     false;
    var _isMovingRight =    false;
    var _isShooting = false;

    //
    //  public methods.
    //

    that.setIsShooting = function( b )
    {
        _isShooting = b;
    }

    that.setIsMovingUp = function( b )
    {
        _isMovingUp = b;
    }

    that.setIsMovingDown = function( b )
    {
        _isMovingDown = b;
    }

    that.setIsMovingRight = function( b )
    {
        _isMovingRight = b;
    }

    that.setIsMovingLeft = function( b )
    {
        _isMovingLeft = b;
    } 

    that.moveLeft = function()
    {
		if (that.X > 0) 
        {
			that.setPosition( that.X - 5, that.Y );
		}
	}
	
	that.moveRight = function()
    {
		if (that.X + that.width < _canvasWidth) 
        {
			that.setPosition( that.X + 5, that.Y );
		}
	}

    that.moveUp = function()
    {
		if (that.Y > 0 ) 
        {
			that.setPosition( that.X, that.Y - 5 );
		}
	}

    that.moveDown = function()
    {
		if (that.Y + that.height < _canvasHeight ) 
        {
			that.setPosition( that.X, that.Y + 5 );
		}
	}

    that.setPosition = function( x, y )
    {
		that.X = x;
		that.Y = y;
	}

    that.draw = function( ctx )
    {
        if ( _isMovingUp )
        {
            that.moveUp();
        }

        if ( _isMovingDown )
        {
            that.moveDown();
        }

        if ( _isMovingLeft )
        {
            that.moveLeft();
        }

        if ( _isMovingRight )
        {
            that.moveRight(); 
        }

		try 
        {
			ctx.drawImage( that.image, 0, that.height * that.actualFrame, 
                    that.width, that.height, that.X, 
                    that.Y, that.width, that.height );
		} 
		catch ( e ) 
        {
            //..
		};
		
		if (that.interval == 4 ) 
        {
			if (that.actualFrame == that.frames) 
            {
				that.actualFrame = 0;

                if ( _isShooting )
                {
                    that.shoot();
                }
			}
			else 
            {
				that.actualFrame++;
			}
			that.interval = 0;
		}

		that.interval++;		
	}

    that.shoot = function()
    {
        var zX = that.X + ~~( that.width / 2 );
        _gameFactory.newPew( _canvasHeight, zX, that.Y );
    }

    //
    //  private methods.
    //


	
	
	

	
}




