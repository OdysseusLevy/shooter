/*
*   ..Pew.js, uly, dec2011..
*
*   TODO:abstract base class as Projectile.js
*   TODO:handle collision.
*
*/
function Pew( theCanvasHeight, theX, theY )
{ 
	var that = this;

    //
    //  public variables.
    // 
	that.image = new Image(); 
	that.image.src = "pew.png";
	that.width =    24;
	that.height =   72;
	that.X = theX;
	that.Y = theY;	
    that.type = "Pew";

    //
    //  private variables.
    //

    var _canvasHeight = theCanvasHeight; 
    var _velocity = 15;

    //
    //  public methods.
    //

    that.isDisabled = function()
    {
        return true == _disabled;
    }

    that.moveUp = function()
    {
		if (that.Y > 0 ) 
        {
			that.setPosition( that.X, that.Y - _velocity );
		}
        else
        {
            that.disable();
        }
	} 

    that.setPosition = function( x, y )
    {
		that.X = x;
		that.Y = y;
	}

    that.draw = function( ctx )
    {
        if ( _disabled )
        {
            return;
        }

        that.moveUp();

		try 
        {
			ctx.drawImage( that.image, that.X, that.Y );
		} 
		catch ( e ) 
        {
            console.warn( e );
		}; 
	}

    that.respawn = function( pX, pY )
    {
        _disabled = false;
        that.setPosition( pX, pY );
    }

    //
    //  private methods.
    // 

    var _disabled = false;

    /* sequestor this object and move off screen. */
    that.disable = function()
    {
        _disabled = true;
        that.X = -that.width;
        that.Y = -that.height;
    }
}




