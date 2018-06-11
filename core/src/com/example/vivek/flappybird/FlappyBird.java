package com.example.vivek.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
    //ShapeRenderer shapeRenderer;
    int score = 0;
    int highScore = 0;
    int scoringTube = 0;
    BitmapFont font;
    BitmapFont highScorefont;
    Texture gameover;

    Texture topTube;
    Texture bottomTube;
    float gap = 400;
    float maxTubeOffset;
    Random rand;
    float tubeVelocity = 4;
    int noOfTubes = 4;
    float[] tubeX = new float[noOfTubes];
    float[] tubeOffset = new float[noOfTubes];
    float distanceBetweenTubes;
    Rectangle[] topTubeRectangle;
    Rectangle[] bottomTubeRectangle;

    private Texture[] birds;
    private int flapState = 0;
    float birdY=0;
    private float velocity = 0;
    Circle birdCircle;

    int gameState = 0;
    float gravity = 2;

    @Override
	public void create () {
        batch = new SpriteBatch();
        birdCircle = new Circle();
        //shapeRenderer = new ShapeRenderer();
        topTubeRectangle = new Rectangle[noOfTubes];
        bottomTubeRectangle = new Rectangle[noOfTubes];
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(15);
        highScorefont = new BitmapFont();
        highScorefont.setColor(Color.WHITE);
        highScorefont.getData().setScale(8);

		gameover = new Texture("gameover.png");
		background = new Texture("bg.png");
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 -100;
        rand = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

        startGame();

	}

	public void startGame(){

        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
        for(int i=0;i<noOfTubes;i++){
            tubeOffset[i] = (rand.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()/2-gap-150);
            tubeX[i] = Gdx.graphics.getWidth()/2-topTube.getWidth()/2 + Gdx.graphics.getWidth()/2+i*distanceBetweenTubes;
            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
        }
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState == 1) {

            if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2){

                score++;
                Gdx.app.log("Score:",String.valueOf(score));

                if(scoringTube<noOfTubes-1){
                    scoringTube++;
                }else {
                    scoringTube = 0;
                }
            }

            if(Gdx.input.justTouched()){
                velocity = -30;
            }

            for(int i=0;i<noOfTubes;i++) {

                if(tubeX[i] < -topTube.getWidth()){
                    tubeX[i] += distanceBetweenTubes * noOfTubes;
                    tubeOffset[i] = (rand.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()/2-gap-150);
                }else {
                    tubeX[i] -= tubeVelocity;
                }

                batch.draw(topTube,tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());
            }
            if(birdY >0) {
                velocity += gravity;
                birdY -= velocity;
            }else {
                gameState = 2;
            }
        }else if (gameState == 0){
            if(Gdx.input.justTouched()){
                gameState = 1;
            }
        }else if(gameState ==2 ){

            batch.draw(gameover,Gdx.graphics.getWidth()/2-400,Gdx.graphics.getHeight()/2-150,800,300);
            highScorefont.draw(batch,"HighScore: "+String.valueOf(highScore),Gdx.graphics.getWidth()/2-320,Gdx.graphics.getHeight()/2+300);
            if(highScore<score){
                highScore = score;
            }
            if(birdY>0){
                velocity += gravity;
                birdY -= velocity;
            }
            if(Gdx.input.justTouched()){
                gameState = 1;
                startGame();
                score = 0;
                velocity = 0;
                scoringTube = 0;
            }

        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2,birdY);
        font.draw(batch,String.valueOf(score),Gdx.graphics.getWidth()/2-50,Gdx.graphics.getHeight()-300);

        birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        for(int i=0;i<noOfTubes;i++){
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],topTube.getWidth(),topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth(),bottomTube.getHeight());

            if(Intersector.overlaps(birdCircle,topTubeRectangle[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangle[i])){
                gameState = 2;
            }

        }
        //shapeRenderer.end();
        batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
