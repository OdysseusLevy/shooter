/*
*   Resources.js, uly, jan2011
*
*   Resources is another fake static class.
*   It is responsible for helping cache all of our client's resources files and making them available (such as images).
*
*/
function Resources()
{
    //
    //  public variables.
    //

    //
    //  private variables.
    //

    /* cached image files "hashMap". key:fileName, val:image. */
    var _images;

    //
    //  public methods.
    // 

    /*
    *   return our cached image files.
    */
    this.getImages = function()
    {
        if ( _images )
                return _images;

        _images = new Object();

        //
        //  TODO:find a better way of collecting image file names.
        //  perhaps an "ls", or something.
        //
        var imageFileNames = [ "ship.png", "pew.png" ];

        //
        //  assemble our image resource dictionary.
        //  key:    fileName.
        //  value:  javaScript image.
        // 

        var fileName, image;
        for ( var i = imageFileNames.length; i > 0; i-- )
        {
            fileName = imageFileNames[ i - 1 ];
            image = new Image();
            image.src = fileName;
            _images[ fileName ] = image;
        }

        return _images;
    } 

    //
    //  private methods.
    //

}
