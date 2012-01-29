/*
*   ..GameFactory.js, uly, dec2011..
*
*   The GameFactory is a "static" javaScript class that knows how to draw everything.
*
*
*   references:
*       http://stackoverflow.com/questions/800593/loop-through-json-object-list
*/
function GameFactory()
{
    //
    //  public variables.
    //

    //
    //  private variables.
    //

    /* cached image files. */
    var images = Resources.getImages();

    //
    //  public methods.
    // 

    /*
    *   draw our game graph onto the canvas.
    *
    *   @param ctx      the drawing canvas 2d context.
    *   @param model    the game graph as a json list of objects. 
    */
    this.drawModel = function( ctx, model )
    {
        $.each( model, function( item ) ) 
        {
            drawSprite( ctx, x, y,  );
        }
    }

    /*
    *   draw a basic sprint onto the canvas.
    *   note: word vs local coordinate scoping is NOT handled here.
    *
    *   @ctx        the canvas drawing context.
    *   @param x    the literal x canvas coordinate.
    *   @param y    the literal y canvas coordinate.
    *   @fileName   the image fileName.
    */
    this.drawSprite = function( ctx, x, y, fileName )
    {
        res = images.fileName;

        try
        {
            ctx.drawImage( res.image, x, y );
        }
        catch ( e )
        {
            console.warn( e );
        } 
    } 

    //
    //  private methods.
    //

}
