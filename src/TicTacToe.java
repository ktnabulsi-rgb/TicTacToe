import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class TicTacToe extends ApplicationAdapter
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world

    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text), and Textures (images)
    private GlyphLayout layout;//used to format text
    private ShapeRenderer renderer; //used to draw points, lines, shapes

    private int[][] grid; //2D array holding the values for x's, o's and empty squares
    private Array<Texture> images; //textures are our images for the blank, x, and o buttons
    private boolean xTurn;//xturn is true, then its x's turn, if it is false then it is o's turn
    private boolean xWon;//x's won
    private boolean oWon;//o's won
    private boolean catsGame; //it was a tie game
    private Sound clickSound;
    private Music thinkingMusic;

    //make width and height proportional to the confi.width and config.height in the GameLauncher class
    public static final int WORLD_WIDTH = 300;
    public static final int WORLD_HEIGHT = 300;
    public static final int NUM_COLUMNS = 3;
    public static final int NUM_ROWS = 3;
    public static final int X = 1;
    public static final int O = 2;
    public static final int EMPTY = 0;
    public static final int CELL_WIDTH = WORLD_WIDTH / NUM_COLUMNS;
    public static final int CELL_HEIGHT = WORLD_HEIGHT / NUM_ROWS;

    @Override//this is called once when you first run your program
    public void create(){
        camera = new OrthographicCamera(); //camera for our world, it is not moving
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); //maintains world units from screen units

        font = new BitmapFont(Gdx.files.internal("impact32.fnt")); //load our font style and size

        //initialize instance variables
        batch = new SpriteBatch();
        layout = new GlyphLayout();
        renderer = new ShapeRenderer();
        grid = new int[3][3];
        images = new Array<Texture>();
        images.add(new Texture(Gdx.files.internal("button.png")));
        images.add(new Texture(Gdx.files.internal("button_x.png")));
        images.add(new Texture(Gdx.files.internal("button_o.png")));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("Hello.mp3"));
        thinkingMusic = Gdx.audio.newMusic(Gdx.files.internal("Hello.mp3"));

        xTurn = true;
        xWon = false;
        oWon = false;
        catsGame = false;
    }

    @Override//this is called 60 times a second, all the drawing is in here, or helper
    //methods that are called from here
    public void render(){
        //these two lines wipe and reset the screen so when something action had happened
        //the screen won't have overlapping images
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getInput(); //helper method that is continually checking if the user has clicked
        xWon = checkWinner(X);//helper method check if x's won, after getting input
        oWon = checkWinner(O); //helper method check if o's won, after getting input
        catsGame = checkCatsGame(); //helper method check if cats game, after getting input

        //set the appropriate message based on who won
        if(xWon)
        {
            layout.setText(font, "X's WON!!!");
        }
        else if(oWon)
        {
            layout.setText(font, "O's WON!!!");
        }
        else if(catsGame)//check this last
        {
            layout.setText(font, "CATS GAME!!!");
        }

        //this line lets us draw based on the coordinates within our WORLD
        batch.setProjectionMatrix(viewport.getCamera().combined);
        //all drawing has to be done between a begin and an end
        //you cannot have nested begin and ends, so be sure to end one before beginning
        //another
        batch.begin();
        drawBoard();//helper method to draw the board (it is between begin and
        //end calls)

        if(xWon || oWon || catsGame)//the game is  over
        {
            font.setColor(Color.BLACK);
            font.draw(batch, //our SpriteBatch
                    layout, //our GlyphLayout
                    (WORLD_WIDTH - layout.width) / 2, //x coordinate of top left of text
                    (WORLD_HEIGHT + layout.height) / 2);// y coordinate of top left of text
            layout.setText(font, "Click to Play Again");
            font.draw(batch,
                    layout,
                    (WORLD_WIDTH - layout.width) / 2,
                    (WORLD_HEIGHT -  2 * layout.height) / 2);
        }

        batch.end(); //done drawing
    }


    private boolean checkCatsGame()
    {

        for(int r = 0; r < NUM_ROWS; r++)
        {
            for(int c = 0; c < NUM_COLUMNS; c++)
            {
                if(grid[r][c] == EMPTY) {

                    return false;

                }
            }
        }


        //TODO: check the logic if it is a cats game
        //return true if nobody has won and all the
        //cells are filled. 'grid' is the 2D array instance variable

        return true; //stubbed out so it will compile
    }

    private boolean checkWinner(int player)
    {

        //horizontal
        if((grid[0][0] == player && grid[0][1] == player && grid[0][2] == player) || (grid[1][0] == player && grid[1][1] == player && grid[1][2] == player) || (grid[2][0] == player && grid[2][1] == player && grid[2][2] == player)) {

            return true;
        }
        //vertical
        //decided to hard code the values in bcs it wasnt working when i was using .length.

        if((grid[0][0] == player && grid[1][0] == player && grid[2][0] == player) ||
                (grid[0][1] == player && grid[1][1] == player && grid[2][1] == player) ||
                (grid[0][2] == player && grid[1][2] == player && grid[2][2] == player)) {
            return true;
        }
        //diagonal

        if((grid[0][0] == player && grid[1][1] == player && grid[2][2] == player) ||
                grid[0][2] == player && grid[1][1] == player && grid[2][0] == player) {
            return true;
        }

        //TODO check if the 'player' won
        //return true if the 'player' has 3 in a row
        //horizontally, vertically, or digaonally
        return false;

    }

    private void getInput()
    {
        //check if the game is over and reset everything
        if(catsGame || xWon || oWon)
        {
            if(Gdx.input.justTouched())//checks for a click
            {
                catsGame = false;
                xWon = false;
                oWon = false;
                xTurn = true;
                resetBoard();
            }

        }
        else//the game is not over
        {
            if(Gdx.input.justTouched())//checks for a click
            {
                int x = Gdx.input.getX(); //gets the x of the screen
                int y = Gdx.input.getY(); //gets y of the screen
                Vector2 pos = viewport.unproject(new Vector2(x,y)); //maps
                //(x,y) coordinate of the screen to whatever the world units are
                //example if screen is 300 x 300 and world is 3 x 3 when we click the
                //top right it will be (300, 300), but our game logic depends on the
                //world units so the unproject line of code will map (300, 300) to
                //(3,3), Also the y - axis of the screen is actually flipped

                //now we need to change the coordinate clicked to
                //the corresponding position in the 2D array

                //int dividing by 100 because
                //the world width is 300 and there are 3 evenly spaced columns
                //example if I click (250, 275) that would fall in row 0 and column 2
                //[ --> inclusive
                //( --> exclusive
                int row = (grid.length - 1) - (int)pos.y / CELL_HEIGHT;//2 is index of last row
                //y coordinate maps to row 0 for values from [200,300)
                //y coordinate maps to row 1 for values from [100,200)
                //y coordinate maps to row 2 for values from [0,100)

                int col = (int)pos.x / CELL_WIDTH;
                // x coordinate maps to column 0 for values from [0,100)
                // x coordinate maps to column 1 for values from [100,200)
                // x coordinate maps to column 2 for values from [200,300)

                if(grid[row][col] == EMPTY)//if you clicked an empty cell
                {
                    if(xTurn)
                        grid[row][col] = X;
                    else
                        grid[row][col] = O;
                    xTurn = !xTurn; //change turns every click
                }
            }
        }

    }

    private void resetBoard()
    {
        for(int r = 0; r < grid.length; r++) {

            for(int c = 0; c < grid[0].length; c++) {
                grid[r][c] = EMPTY;

            }
        }
        clickSound.play();
        //TODO reset all the grid cells back to EMPTY

    }

    private void drawBoard()
    {

        for(int r = 0; r < grid.length; r++)
        {
            for(int c = 0; c < grid[0].length; c++)
            {
                if(grid[r][c] == EMPTY)
                {
                    //grid[0][0] --> draw at coordinate (0, 200)
                    //grid[0][1] --> draw at coordinate (100, 200)
                    //grid[0][2] --> draw at coordinate (220, 200)

                    //grid[1][0] --> draw at coordinate (0, 100)
                    //grid[1][1] --> draw at coordinate (100, 100)
                    //grid[1][2] --> draw at coordinate (200, 100)

                    //grid[2][0] --> draw at coordinate (0, 0)
                    //grid[2][1] --> draw at coordinate (100, 0)
                    //grid[2][2] --> draw at coordinate (200, 0)
                    batch.draw(images.get(0),//Texture object to draw
                            c * CELL_WIDTH,//bottom left x position
                            CELL_HEIGHT * ((grid.length - 1) - r),//bottom right y position
                            CELL_WIDTH,//width
                            CELL_HEIGHT);  //height
                }
                if(grid[r][c] == X)
                {

                    batch.draw(images.get(1),
                            c * CELL_WIDTH,
                            CELL_HEIGHT * ((grid.length - 1) - r),
                            CELL_WIDTH,
                            CELL_HEIGHT);

                }
                if(grid[r][c] == O)
                {
                    batch.draw(images.get(2),
                            c * CELL_WIDTH,
                            CELL_HEIGHT * ((grid.length - 1) - r),
                            CELL_WIDTH,
                            CELL_HEIGHT);

                }
            }

        }

    }
    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true);
    }

    @Override
    public void dispose(){
        batch.dispose();
        renderer.dispose();
        font.dispose();
    }
}
