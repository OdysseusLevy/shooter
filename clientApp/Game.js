
//
//  globals.
//
var width = 320, 
        height = 500,
        gLoop,
        _canvas = document.getElementById('id_canvas'), 
        ctx = _canvas.getContext('2d');
                
_canvas.width = width;
_canvas.height = height;

if (!window.console) console = {};
console.log = console.log || function(){};
console.warn = console.warn || function(){};
console.error = console.error || function(){};
console.info = console.info || function(){};

var clearCanvas = function(){
	ctx.fillStyle = '#808080';  /* background colour. */
	ctx.clearRect(0, 0, width, height);
	ctx.beginPath();
	ctx.rect(0, 0, width, height);
	ctx.closePath();
	ctx.fill();
} 

var gameFactory = new GameFactory();

//
//  input event handlers.
//

document.onkeydown = function( event )
{
    switch ( event.keyCode )
    {
        case 87:  /* w */
        case 38:  /* up-arrow */
            //player.setIsMovingUp( true ); 
            break;
        case 83:  /* s */
        case 40:  /* down-arrow */
            //player.setIsMovingDown( true );
            break;
        case 65:  /* a */
        case 37:  /* left-arrow */
            //player.setIsMovingLeft( true );
            break;
        case 68:  /* d */
        case 39:  /* right-arrow */ 
            //player.setIsMovingRight( true );
            break; 
        case 32:  /* spaceBar */
            //player.setIsShooting( true );
            break; 
        default:
            break;
    }
}

document.onkeyup = function( event )
{
    switch ( event.keyCode )
    {
        case 87:  /* w */
        case 38:  /* up-arrow */
            //player.setIsMovingUp( false );
            break;
        case 83:  /* s */
        case 40:  /* down-arrow */
            //player.setIsMovingDown( false );
            break;
        case 65:  /* a */
        case 37:  /* left-arrow */
            //player.setIsMovingLeft( false );
            break;
        case 68:  /* d */
        case 39:  /* right-arrow */
            //player.setIsMovingRight( false );
            break;
        case 32:  /* spaceBar */
            //player.setIsShooting( false );
            break; 
        default:
            break;
    }
} 

var isGetting;  /* jQuery get lock. */ 
var model = new Array();      /* the client's copy of the current server game graph model. */

var GET_JSON_URL = "http://localhost:8080/shooter/update/index?callback=?";  /* the special url to get our game model. */

/*
*   return our jquery get results.
*/
var refreshModel = function()
{
    if ( isGetting ) 
            return;

    isGetting = true; 
    $.getJSON( GET_JSON_URL, 
            function( jason )
            {  
                model = jason; 
                isGetting = false;  // release lock.
            } );
} 

/*
*   send out our batched user input commands out to the server.
*/
var postUserInput = function()
{
    //..TODO..
}

var GAME_LOOP_TIMEOUT = 20;     /* 1000/50 = 20; the rate at which our game loops. */

/*
*   the client's game loop is responsible for 3 things:
*       1.) get/pull our server's model.
*       2.) draw out our current copy of the model.
*       3.) push out batched user inputs.
*
*/
var GameLoop = function(){
	clearCanvas(); 
	refreshModel();
	gameFactory.drawModel( ctx, model );
    postUserInput();

	gLoop = setTimeout(GameLoop, GAME_LOOP_TIMEOUT );   // do last!
}

GameLoop();
